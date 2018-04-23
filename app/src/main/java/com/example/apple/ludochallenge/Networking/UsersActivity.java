package com.example.apple.ludochallenge.Networking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.ludochallenge.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private int noOfPlayers;
    private int color;
    private int vsComputer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        getDataFromParentActivity();

        mUsersList = findViewById(R.id.users_recyclerlist);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference();


        FirebaseRecyclerOptions<Users> options  = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mUsersDatabase, Users.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                view.setLayoutParams(new ViewGroup.LayoutParams(displayMetrics.widthPixels,displayMetrics.heightPixels/5));
                return new UsersViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                holder.setName(model.getName());
                holder.setUserImage(model.getThumb_image(), getApplicationContext());
                holder.setUserFlag(model.getFlag_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id", user_id);
                        intent.putExtra("noOfPlayers",noOfPlayers);
                        intent.putExtra("color",color);
                        intent.putExtra("vsComputer",vsComputer);
                        startActivity(intent);
                    }
                });
            }

        };

        mUsersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.users_name);
            userNameView.setText(name);

        }
        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_singleImage);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_pic).into(userImageView);
        }
        public void setUserFlag(String flag, Context ctx){
            ImageView flagView = (ImageView) mView.findViewById(R.id.user_single_flag);
            Picasso.get().load(flag).placeholder(R.drawable.pakistan).into(flagView);
        }
    }

    private void getDataFromParentActivity(){
        Intent intent = getIntent();
        noOfPlayers = intent.getIntExtra("noOfPlayers", 0);
        color = intent.getIntExtra("color", 0);
        vsComputer = intent.getIntExtra("vsComputer",0);
    }
}
