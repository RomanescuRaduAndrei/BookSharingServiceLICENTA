package com.example.booksharingservicelicenta;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LibraryFragment extends Fragment {
    private ArrayList<BookDataClass> dataList;
    private BookAdapter bookAdapter;
    private GridView gridView;
    int itemId=0;
    FirebaseAuth mAuth;
    int counter=0;
    FirebaseUser mUser;

    final private DatabaseReference databaseReferenceLibrary = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Library");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_library, container, false);
        gridView = v.findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        bookAdapter = new BookAdapter(getActivity(), dataList);
        gridView.setAdapter(bookAdapter);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();



        databaseReferenceLibrary.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userId= mUser.getUid();
                    String userUID;
                    String userRUID;
                    String itemId;
                    dataList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookDataClass bookDataClass = dataSnapshot.getValue(BookDataClass.class);
                        bookDataClass.setItemId(dataSnapshot.getKey()); // Set the item ID using the unique key from Firebase
                        itemId=bookDataClass.getItemId();
                        dataList.add(bookDataClass);


                        for(int i=0; i<dataList.size();i++)
                        {
                            userUID=dataList.get(i).getRequesterId();
                            userRUID=dataList.get(i).getRequesterId();
                            if(userId.equals(userUID))
                            {


                            }
                            else
                            {
                                dataList.remove(i);
                            }
                        }





                    }
                    bookAdapter.notifyDataSetChanged();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        return v;
    }

}