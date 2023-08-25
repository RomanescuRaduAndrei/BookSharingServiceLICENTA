package com.example.booksharingservicelicenta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    RequestFragment requestFragment=new RequestFragment();
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    int authMethod;
    ProfileFragment profileFragment=new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final String TAG = "DocSnippets";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        authMethod=intent.getIntExtra("authMethod",0);
        bundle.putInt("authMethod", authMethod);
        profileFragment.setArguments(bundle);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.library:

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, libraryFragment).commit();
                        return true;

                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                        return true;

                        case R.id.chats:

                            if(mUser != null) {
                                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                builder.setTitle("WARNING"); // Set the title of the dialog to the book title


                                String details = "Please login using an created account to access chat !"+"\n"
                                        ;

                                builder.setMessage(details);
                                builder.show();
                            }
                    case R.id.requests:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, requestFragment).commit();



                            return true;

                }

                return false;
            }
        });


    }
}