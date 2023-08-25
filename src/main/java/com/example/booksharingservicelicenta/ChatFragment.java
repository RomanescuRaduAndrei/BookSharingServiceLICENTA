package com.example.booksharingservicelicenta;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


public class ChatFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    ImageView userImage;
    FirestoreRecyclerAdapter<MessageDataClass,NoteViewHolder> chatAdapter;
    RecyclerView mrecyclerview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.chatfragment,container,false);

       firebaseAuth=FirebaseAuth.getInstance();
       firebaseFirestore= FirebaseFirestore.getInstance();
       mrecyclerview=v.findViewById(R.id.recyclerview);


       // Query query=firebaseFirestore.collection("Users");
        Query query=firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());
        FirestoreRecyclerOptions<MessageDataClass> allusername=new FirestoreRecyclerOptions.Builder<MessageDataClass>().setQuery(query, MessageDataClass.class).build();

        chatAdapter=new FirestoreRecyclerAdapter<MessageDataClass, NoteViewHolder>(allusername) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull MessageDataClass MessageDataClass) {

                noteViewHolder.particularusername.setText(MessageDataClass.getName());
                String uri= MessageDataClass.getImage();

                Picasso.get().load(uri).into(userImage);
                if(MessageDataClass.getStatus().equals("Online"))
                {
                    noteViewHolder.statusofuser.setText(MessageDataClass.getStatus());
                    noteViewHolder.statusofuser.setTextColor(Color.GREEN);
                }
                else
                {
                    noteViewHolder.statusofuser.setText(MessageDataClass.getStatus());
                }

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), SpecificChatActivity.class);
                        intent.putExtra("name", MessageDataClass.getName());
                        intent.putExtra("receiveruid", MessageDataClass.getUid());
                        intent.putExtra("imageuri", MessageDataClass.getImage());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,parent,false);
                return new NoteViewHolder(view);
            }
        };


        mrecyclerview.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mrecyclerview.setLayoutManager(linearLayoutManager);
        mrecyclerview.setAdapter(chatAdapter);

        return v;

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {

        private TextView particularusername;
        private TextView statusofuser;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            particularusername=itemView.findViewById(R.id.nameofuser);
            statusofuser=itemView.findViewById(R.id.statusofuser);
            userImage =itemView.findViewById(R.id.imageviewofuser);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null)
        {
            chatAdapter.stopListening();
        }
    }
}
