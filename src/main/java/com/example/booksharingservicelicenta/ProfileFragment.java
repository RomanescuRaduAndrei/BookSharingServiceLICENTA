package com.example.booksharingservicelicenta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

    Button signOutBtn;
    Button resetPassword;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount gsa;
    int authMethod;
    Button deleteAccount;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView userMail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_profile2, container, false);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        resetPassword=v.findViewById(R.id.resetPassword);
        deleteAccount=v.findViewById(R.id.deleteAccount);
        userMail=v.findViewById(R.id.displayEmail);
        gsa=GoogleSignIn.getLastSignedInAccount(getActivity());
        Bundle args = getArguments();
        if (args != null) {
            authMethod = args.getInt("authMethod", 0);
        }
        signOutBtn = v.findViewById(R.id.signOut);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(),gso);

        if(authMethod == 1) {
            String email = gsa.getEmail();
            userMail.setText("Welcome " + email);
            signOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    signOut();
                }

            });
        }else if(authMethod== 0){



            String email = mUser.getEmail();
            userMail.setText("Welcome " + email);
            signOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(),MainActivity.class));
                }


            });

            resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Password reset");
                                        String details = "Password reset email has been sent successfully !";
                                        builder.setMessage(details);
                                        builder.show();
                                    }

                                }
                            });
                }
            });



            deleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("WARNING");
                    String details = "Warning! You are about to delete your account permanently. This is not reversible!";
                    builder.setMessage(details);
                    builder.setPositiveButton("I acknowledged", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Account deleted successfully.
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                    } else {
                                        // An error occurred while deleting the account.
                                        // Handle the error here or display a message to the user.
                                    }
                                }
                            });
                        }
                    });

                    builder.show(); // Show the dialog to the user.
                }
            });

        }






        return v;

    }

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getActivity(),MainActivity.class));
            }
        });
    }
}