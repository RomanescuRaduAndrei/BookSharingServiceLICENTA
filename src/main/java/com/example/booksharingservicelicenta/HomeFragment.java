package com.example.booksharingservicelicenta;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    FloatingActionButton fab;
    private GridView gridView;
    private ArrayList<BookDataClass> dataList;
    private BookAdapter bookAdapter;
    private BookAdapter bookAdapterFiltered;

    private EditText searchEditText;
    private Button searchButton;
    private ArrayList<BookDataClass> filteredList;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Books");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_home, container, false);

        fab = v.findViewById(R.id.fab);
        gridView = v.findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        filteredList = new ArrayList<>();
        bookAdapter = new BookAdapter(getActivity(), dataList);
        bookAdapterFiltered =new BookAdapter(getActivity(),filteredList);
        gridView.setAdapter(bookAdapter);
        searchEditText = v.findViewById(R.id.searchEditText);
        searchButton = v.findViewById(R.id.searchButton);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookDataClass bookDataClass = dataSnapshot.getValue(BookDataClass.class);
                        bookDataClass.setItemId(dataSnapshot.getKey()); // Set the item ID using the unique key from Firebase
                        dataList.add(bookDataClass);







                }
                bookAdapter.notifyDataSetChanged();
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {




                BookDataClass clickedBook = dataList.get(position);
                showBookDetailsDialog(clickedBook , dataList , position);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    performSearch(keyword);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return v;
    }

    private void showBookDetailsDialog(BookDataClass bookDataClass, ArrayList<BookDataClass> data, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(bookDataClass.getTitle()); // Set the title of the dialog to the book title


        String details = "Author: " + bookDataClass.getAuthor() + "\n"
                ;

        builder.setMessage(details);


        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Get Book", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), RentBookActivity.class);
                intent.putExtra("FROM_ACTIVITY", true);
                intent.putParcelableArrayListExtra("data",data);
                intent.putExtra("position",position);
                startActivity(intent);
                getActivity().finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performSearch(String keyword) {
        filteredList.clear();
        for (BookDataClass bookDataClass : dataList) {
            if (bookDataClass.getTitle().toLowerCase().contains(keyword.toLowerCase()) || bookDataClass.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(bookDataClass);
            }
        }

        if (filteredList.isEmpty()) {
            searchEditText.setText("Item not found");
        } else {
            searchEditText.setText("");
        }
        gridView.setAdapter(bookAdapterFiltered);
        bookAdapterFiltered.notifyDataSetChanged();

    }
}