package com.example.booksharingservicelicenta;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RentBookActivity extends AppCompatActivity {
    HomeFragment homeFragment = new HomeFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    TextView author;
    TextView title;
    ImageView image;
    Button getBookBut;
    String Sauthor;
    String imageURL;
    String itemId;

    ArrayList<BookDataClass> recievedData;
    private BookAdapter bookAdapter;
    ProgressBar progressBar;
    String Stitle;
    String username;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView uploadedBy;
    int position;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Books");
    final private DatabaseReference databaseReferenceRequest = FirebaseDatabase.getInstance("https://booksharingservicelicenta-default-rtdb.europe-west1.firebasedatabase.app").getReference("Requests");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentbook);
        author=findViewById(R.id.authordisplay);
        title=findViewById(R.id.titledisplay);
        image=findViewById(R.id.imagedisplay);
        getBookBut=findViewById(R.id.getbookbut);
        bookAdapter = new BookAdapter(this, recievedData);
        Intent intent = getIntent();
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        uploadedBy=findViewById(R.id.uploadedby);

        if (intent != null && intent.hasExtra("data") && intent.hasExtra("position")) {
            // Replace "defaultValue" with a default value to be used in case the key is not found in the intent extras
            // For example, if the data you are passing is a String, you can use getStringExtra()
            ArrayList<BookDataClass> recievedData = intent.getParcelableArrayListExtra("data");
            position=intent.getIntExtra("position",0);


            if(position >=0 && position <recievedData.size())
            {
                BookDataClass selectedData = recievedData.get(position);
                Sauthor = selectedData.getAuthor();
                Stitle = selectedData.getTitle();
                username= selectedData.getPosterUsername();
                imageURL = selectedData.getImageURL();
                Glide.with(this).load(imageURL).into(image);
                author.setText("Author: " + Sauthor);
                title.setText("Title: "+Stitle);
                uploadedBy.setText("Added by: \n"+ username);

                getBookBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadToFirebase(selectedData.getImageURL(),selectedData.getAuthor(),selectedData.getTitle(),selectedData.getPosterUserId(),selectedData.getPosterUsername(),"Pending");
                        itemId =selectedData.getItemId();
                        bookAdapter.notifyDataSetChanged();
                        Intent intent=new Intent(RentBookActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

            }

        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                    case R.id.library:
                    case R.id.profile:
                        Intent intent=new Intent(RentBookActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;

                }


                return false;
            }
        });


    }

    private void uploadToFirebase(String image, String author, String title,String posterId,String posterUsername,String state){
        String Title = title;
        String Author=author;
        String requesterId=mUser.getUid();
        String requesterUsername=mUser.getEmail();


        BookDataClass bookDataClass = new BookDataClass(image, title , author,posterId, posterUsername,requesterId,requesterUsername,state);
        String key = databaseReferenceRequest.push().getKey();

        databaseReferenceRequest.child(key).setValue(bookDataClass);
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(RentBookActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

       }



    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}
