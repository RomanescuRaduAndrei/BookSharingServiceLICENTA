package com.example.booksharingservicelicenta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestFragment extends Fragment {

    private ArrayList<BookDataClass> dataList;
    final private DatabaseReference databaseReferenceRequest = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Requests");
    final private DatabaseReference databaseReferenceLibrary = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Library");
    final private DatabaseReference databaseReferenceBook = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Books");
    private BookAdapter dataAdapter;
    private GridView gridView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_request, container, false);
        dataList = new ArrayList<>();
        gridView = v.findViewById(R.id.gridView);
        dataAdapter = new BookAdapter(getActivity(), dataList);
        gridView.setAdapter(dataAdapter);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        databaseReferenceRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookDataClass bookDataClass = dataSnapshot.getValue(BookDataClass.class);

                    bookDataClass.setItemId(dataSnapshot.getKey()); // Set the item ID using the unique key from Firebase
                    String posterId=bookDataClass.getPosterUserId();
                    String state=bookDataClass.getState();
                    if(posterId.equals(mUser.getUid()) && state.equals("Pending")) {
                        dataList.add(bookDataClass);
                    }

                }
                dataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {




                BookDataClass clickedBook = dataList.get(position);
                showRequestDetails(clickedBook , dataList , position);
            }
        });






        return v;
    }

    private void showRequestDetails(BookDataClass bookDataClass, ArrayList<BookDataClass> data, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(bookDataClass.getTitle()); // Set the title of the dialog to the book title


        String details = "This book has been requested by" + bookDataClass.getRequesterUsername() + " please reply to the request.\n"
                ;

        builder.setMessage(details);


        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String ItemId=bookDataClass.getItemId();

                uploadToFirebase(bookDataClass.getImageURL(),bookDataClass.getAuthor(), bookDataClass.getTitle(), bookDataClass.getPosterUserId(),bookDataClass.getPosterUsername(),bookDataClass.getRequesterId(), bookDataClass.getRequesterUsername(), "Rejected");
                databaseReferenceRequest.child(ItemId).removeValue();
                dataAdapter.notifyDataSetChanged();

                dialog.dismiss();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("FROM_ACTIVITY", true);
                intent.putParcelableArrayListExtra("data",data);
                intent.putExtra("position",position);
                startActivity(intent);
                getActivity().finish();


                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                            String posterId=bookDataClass.getPosterUserId();
                            String ItemId=bookDataClass.getItemId();
                            String title=bookDataClass.getTitle();
                            uploadToFirebase(bookDataClass.getImageURL(),bookDataClass.getAuthor(), bookDataClass.getTitle(), bookDataClass.getPosterUserId(),bookDataClass.getPosterUsername(),bookDataClass.getRequesterId(), bookDataClass.getRequesterUsername(), "Accepted");
                            databaseReferenceRequest.child(ItemId).removeValue();
                            dataAdapter.notifyDataSetChanged();

                Query query = databaseReferenceBook.orderByChild("posterUserId").equalTo(posterId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("title").getValue(String.class).equals(title)) {
                                snapshot.getRef().removeValue();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                dialog.dismiss();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
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

    private void uploadToFirebase(String image, String author, String title,String posterId,String posterUsername,String requesterId,String requesterUsername,String state){
        BookDataClass bookDataClass = new BookDataClass(image, title , author,posterId, posterUsername,requesterId,requesterUsername,state);
        String key = databaseReferenceLibrary.push().getKey();

        databaseReferenceLibrary.child(key).setValue(bookDataClass);
    }
}
