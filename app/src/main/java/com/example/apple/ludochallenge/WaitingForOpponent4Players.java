package com.example.apple.ludochallenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.apple.ludochallenge.networking.MySQLDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;

public class WaitingForOpponent4Players extends AppCompatActivity {


    private ImageView waiting_for_opponent_gif;
    private TextView player2_name;
    private TextView player3_name;
    private TextView player4_name;
    private ImageView yourPic;
    private ImageView player2Pic;
    private ImageView player3Pic;
    private ImageView player4Pic;
    MySQLDatabase mySQLDatabase;
    private String current_uid;
    private DatabaseReference mDatabase;
    String facebook_uid;
    String LOGIN_STATUS;
    private String noOfPlayers_online_multiplayer;
    private String entry_coins;
    private boolean check_online_multiplayer = false;
    private TextView yourName;
    String player2_uid;
    String player3_uid;
    String player4_uid;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_opponent4_players);

        waiting_for_opponent_gif = (ImageView) findViewById(R.id.waiting_for_opponent_gif);
        player2_name = (TextView) findViewById(R.id.waitingForOpponent_player2_name);
        player3_name = (TextView) findViewById(R.id.waitingForOpponent_player3_name);
        player4_name = (TextView) findViewById(R.id.waitingForOpponent_player4_name);
        yourPic = (ImageView) findViewById(R.id.waitingForOpponent_yourPic);
        player2Pic = (ImageView) findViewById(R.id.waitingForOpponent_player2Pic);
        player3Pic = (ImageView) findViewById(R.id.waitingForOpponent_player3Pic);
        player4Pic = (ImageView) findViewById(R.id.waitingForOpponent_player4Pic);
        yourName = (TextView) findViewById(R.id.yourName);

        Glide.with(getApplicationContext()).load(R.raw.random_playergif).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(waiting_for_opponent_gif);

        mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
        final MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
        current_uid = mySQLDatabase.fetchCurrentLoggedInID();
        LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedInStatus();
        if (LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)) {
            Intent intent = getIntent();
            facebook_uid = intent.getStringExtra("facebook_uid");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(facebook_uid).child("Online_Multiplayer");
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("Online_Multiplayer");
        }


        Intent onlineMultiplayer_intent = getIntent();
        noOfPlayers_online_multiplayer = onlineMultiplayer_intent.getStringExtra("noOfPlayers");
        entry_coins = onlineMultiplayer_intent.getStringExtra("entry_coins");
        check_online_multiplayer = onlineMultiplayer_intent.getBooleanExtra("online_multiplayer", true);

        final DatabaseReference[] loopThrough = {FirebaseDatabase.getInstance().getReference().child("Users")};
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ValueEventListener listener1 = this;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String key = (String) ds.getKey();
                    final DatabaseReference[] keyReference = {FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("Online_Multiplayer")};
                    keyReference[0].addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("still_searching")) {
                                String code = dataSnapshot.child("still_searching").getValue(String.class);
                                String NO_OF_PLAYERS = "" + dataSnapshot.child("noOfPlayers").getValue();
                                String ENTRY_COINS = "" + dataSnapshot.child("entry_coins").getValue();
                                if (code.equals("true") && !key.equals(current_uid) && noOfPlayers_online_multiplayer.equals(NO_OF_PLAYERS) && entry_coins.equals(ENTRY_COINS)) {
                                    Toast.makeText(getApplicationContext(), code + "  " + key, Toast.LENGTH_LONG).show();
                                    String check_player2_value = dataSnapshot.child("player2_uid").getValue().toString();
                                    if (check_player2_value.equals("0")) {
                                        dataSnapshot.child("player2_uid").getRef().setValue(current_uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dataSnapshot.child("still_searching").getRef().setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        //////////////////////
                                                                loopThrough[0].child(current_uid).child("Online_Multiplayer").child("player2_uid").setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (dataSnapshot.child(key).hasChild("name")) {
                                                                            if (dataSnapshot.child(key).hasChild("thumb_image")) {
                                                                                String opponent_name = dataSnapshot.child(key).child("name").getValue().toString();
                                                                                player2_name.setText(opponent_name);
                                                                                String opponent_pic = dataSnapshot.child(key).child("thumb_image").getValue().toString();
                                                                                Picasso.get().load(opponent_pic).into(player2Pic);
                                                                                dataSnapshot.child("waiting_for_Other_2Players").getRef().setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        loopThrough[0].removeEventListener(listener1);
                                                                                        final ValueEventListener listener = new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                final ValueEventListener listener1 = this;
                                                                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                                                                    final String key1 = (String) ds.getKey();
                                                                                                    if (key1 != current_uid) {
                                                                                                        FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                                                                                                                .child("Online_Multiplayer").addValueEventListener(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                String player2_uid_value = dataSnapshot.child("player2_uid").getValue().toString();
                                                                                                                if (key1 != player2_uid_value) {
                                                                                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("Online_multiplayer")
                                                                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                                                                @Override
                                                                                                                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                                                                                                                    String search_mode = dataSnapshot.child("waiting_for_Other_2Players").getValue().toString();
                                                                                                                                    if (search_mode.equals("true")) {
                                                                                                                                        dataSnapshot.child("player3_uid").getRef().setValue(current_uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                dataSnapshot.child("player4_uid").getRef().setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                        dataSnapshot.child("still_searching").getRef().setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                dataSnapshot.child("waiting_for_Other_2Players").getRef().setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                        String player4_uid = dataSnapshot.child("player4_uid").getValue().toString();
                                                                                                                                                                        FirebaseDatabase.getInstance().getReference().child("Users").child(player4_uid).child("Online_Multiplayer").addValueEventListener(new ValueEventListener() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                                                                                dataSnapshot.child("player3_uid").getRef().setValue("");
                                                                                                                                                                            }

                                                                                                                                                                            @Override
                                                                                                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                                                                                                            }
                                                                                                                                                                        });
                                                                                                                                                                    }
                                                                                                                                                                });
                                                                                                                                                            }
                                                                                                                                                        });
                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                            }
                                                                                                                                        });
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
                                                                                                    }

                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        };
                                                                                    }
                                                                                });
//
                                                                            }
                                                                        }
                                                                    }


                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }

                                }
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
        };
        loopThrough[0].addValueEventListener(listener);



        mDatabase.child("still_searching").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("noOfPlayers").setValue(noOfPlayers_online_multiplayer).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDatabase.child("entry_coins").setValue(entry_coins).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDatabase.child("player3_uid").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDatabase.child("player4_uid").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDatabase.child("player1_uid").setValue(current_uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDatabase.child("waiting_for_Other_2Players").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                String player_name = (String ) mySQLDatabase.getData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.NAME_USER, mySQLDatabase.TABLE_NAME);
                                                                byte[] pic = (byte[]) mySQLDatabase.getData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.IMAGE_PROFILE_COL, mySQLDatabase.TABLE_NAME);
                                                                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(pic);
                                                                Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
                                                                yourPic.setImageBitmap(bitmap);
                                                                yourName.setText(player_name);
                                                            }
                                                        });
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.waitingForOpponent));
        Runtime.getRuntime().gc();
        System.gc();
    }


    private void unbindDrawables(View view) {
        if (view.getBackground() != null)
            view.getBackground().setCallback(null);

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                unbindDrawables(viewGroup.getChildAt(i));

            if (!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }
}
