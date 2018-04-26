package com.example.apple.ludochallenge.networking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.ludochallenge.LudoActivity;
import com.example.apple.ludochallenge.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayId;
    private ImageView imageView;
    private DatabaseReference mUsersDatabase;
    private ProgressDialog mLoadingBar;
    private Button challenge;
    private DatabaseReference mChallenge_database;
    private DatabaseReference mGamePlay_database;
    private DatabaseReference mNotification_database;
    private FirebaseUser mCurrent_user;
    String mCurrent_state;
    private int noOfPlayers;
    private int color;
    private int vsComputer;
    private DatabaseReference gameStartedDatabase;
    private DatabaseReference gameStarted;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getDataFromParentActivity();

        mDisplayId = (TextView) findViewById(R.id.profile_text);
        imageView = (ImageView) findViewById(R.id.profile_image);
        challenge = (Button) findViewById(R.id.challenge);


        mCurrent_state = "not_challenged";





        final String user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mChallenge_database = FirebaseDatabase.getInstance().getReference().child("challenge");
        mGamePlay_database = FirebaseDatabase.getInstance().getReference().child("Game_started");
        mNotification_database = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        gameStartedDatabase = FirebaseDatabase.getInstance().getReference();
        gameStarted = FirebaseDatabase.getInstance().getReference();

        mLoadingBar = new ProgressDialog(this);
        mLoadingBar.setTitle("Loading User Data");
        mLoadingBar.setMessage("Please wait, while we load the user data");
        mLoadingBar.setCanceledOnTouchOutside(false);
        mLoadingBar.show();


       mGamePlay_database.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.hasChild(mCurrent_user.getUid())){

                   gameStarted.child("Game_started").child(mCurrent_user.getUid()).child(user_id).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           String d_isPlaying = dataSnapshot.child("isPlaying").getValue().toString();
                           if(d_isPlaying.equals("1")){
                               Intent intent = new Intent(getApplicationContext(), LudoActivity.class);
                               intent.putExtra("user_id", user_id);
                               intent.putExtra("challengeType", "0");
                               startActivity(intent);
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });

               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });



        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mDisplayId.setText(display_name);
                Picasso.get().load(image).placeholder(R.drawable.default_pic).into(imageView);

                //----------> Challenged player list / challenged feature <------------------//

                mChallenge_database.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String request_type = dataSnapshot.child(user_id).child("challenge_type").getValue().toString();
                            if (request_type.equals("received")) {

                                mCurrent_state = "challenge_received";
                                challenge.setText("Accept Challenge");
                            } else if (request_type.equals("challenged")) {
                                mCurrent_state = "challenge_received";
                                challenge.setText("Cancel Challenge");
                            }

                            mLoadingBar.dismiss();
                        } else {
                            mGamePlay_database.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        mCurrent_state = "game_play";
                                        challenge.setText("Stop Playing");
                                    }
                                    mLoadingBar.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mLoadingBar.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                challenge.setEnabled(false);


                //----------------NOT CHALLENGED STATE-------------//
                if (mCurrent_state.equals("not_challenged")) {
                    mChallenge_database.child(mCurrent_user.getUid()).child(user_id).child("challenge_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mChallenge_database.child(user_id).child(mCurrent_user.getUid()).child("challenge_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrent_user.getUid());
                                        notificationData.put("type","challenged");


                                        mNotification_database.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mCurrent_state = "challenged";
                                                challenge.setText("Cancel Challenge");
                                            }
                                        });


                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed sending challenge!", Toast.LENGTH_SHORT).show();
                            }
                            challenge.setEnabled(true);
                        }
                    });
                }

                //----------------cancel  CHALLENGED STATE-------------//

                if (mCurrent_state.equals("challenged")) {
                    mChallenge_database.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mChallenge_database.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    challenge.setEnabled(true);
                                    mCurrent_state = "not_challenged";
                                    challenge.setText("Challenge");
                                }
                            });
                        }
                    });
                }

                //----------------Challenge Received State-------------//
                if (mCurrent_state.equals("challenge_received")) {
                    final HashMap<String, Integer> gameData = new HashMap<>();
                    gameData.put("color", color);
                    gameData.put("noOfPlayers", noOfPlayers);
                    gameData.put("isPlaying", 0);
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mGamePlay_database.child(user_id).child(mCurrent_user.getUid()).setValue(gameData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mChallenge_database.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mChallenge_database.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            challenge.setEnabled(true);
                                            mCurrent_state = "game_play";
                                            challenge.setText("Stop Playing");
                                            Intent intent = new Intent(getApplicationContext(), LudoActivity.class);
                                            intent.putExtra("color", color);
                                            intent.putExtra("noOfPlayers", noOfPlayers);
                                            intent.putExtra("user_id", user_id);
                                            intent.putExtra("challengeType", "1");
                                            intent.putExtra("playWithFriends", 1);
                                            startActivity(intent);

                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }
        });

    }

    private void getDataFromParentActivity(){
        Intent intent = getIntent();
        noOfPlayers = intent.getIntExtra("noOfPlayers", 0);
        color = intent.getIntExtra("color", 0);
        vsComputer = intent.getIntExtra("vsComputer",0);
    }
}
