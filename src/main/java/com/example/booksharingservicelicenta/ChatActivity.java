package com.example.booksharingservicelicenta;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem chat;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    String name;
    androidx.appcompat.widget.Toolbar mtoolbar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tabLayout=findViewById(R.id.include);
        chat =findViewById(R.id.chat);
        viewPager=findViewById(R.id.fragmentcontainer);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        mtoolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);


        Drawable drawable= ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_more_vert_24);
        mtoolbar.setOverflowIcon(drawable);


        pageAdapter =new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition()==0 || tab.getPosition()==1|| tab.getPosition()==2)
                {
                    pageAdapter.notifyDataSetChanged();
                }
                
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                    case R.id.library:
                    case R.id.profile:
                        Intent intent=new Intent(ChatActivity.this,HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;

                }
                
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }
    
    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        
    }
    private void sendDataForNewUser()
    {

        sendDataToRealTimeDatabase();

    }

    private void sendDataToRealTimeDatabase()
    {


        name=firebaseAuth.getCurrentUser().getEmail();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        ChatUserProfile muserprofile=new ChatUserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(muserprofile);
        sendDataTocloudFirestore();




    }

    private void sendDataTocloudFirestore() {


        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String , Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("image","gs://booksharingservicelicenta.appspot.com/download.png");
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            sendDataForNewUser();
        }
        else
        {
            sendDataForNewUser();
        }




        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Now User is Online",Toast.LENGTH_SHORT).show();
            }
        });

    }
}