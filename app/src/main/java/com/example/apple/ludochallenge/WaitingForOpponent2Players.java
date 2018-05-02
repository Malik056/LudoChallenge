package com.example.apple.ludochallenge;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.apple.ludochallenge.networking.MainMenu;
import com.example.apple.ludochallenge.networking.MySQLDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EventListener;

import static com.example.apple.ludochallenge.LudoActivity.UIDS;
import static com.example.apple.ludochallenge.LudoActivity.UPDATING_USER;

public class WaitingForOpponent2Players extends AppCompatActivity {


    private ImageView waiting_for_opponent_gif;
    private TextView player2_name;
    private ImageView yourPic;
    private ImageView player2Pic;
    private Bitmap player2_bitmap;
    private String noOfPlayers_online_multiplayer;
    private String entry_coins;
    private boolean check_online_multiplayer = false;
    private String current_uid;
    private DatabaseReference mDatabase;
    DatabaseReference reference;
    String LOGIN_STATUS;
    String facebook_uid;
    private TextView yourName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_opponent2_players);


        waiting_for_opponent_gif = (ImageView) findViewById(R.id.waiting_for_opponent_gif_2players);
        player2_name = (TextView) findViewById(R.id.waitingForOpponent_player2_name_2players);
        yourPic = (ImageView) findViewById(R.id.waitingForOpponent_yourPic_2players);
        player2Pic = (ImageView) findViewById(R.id.waitingForOpponent_player2Pic_2players);
        yourName = (TextView) findViewById(R.id.yourName);

        final MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
        current_uid = mySQLDatabase.fetchCurrentLoggedInID();
        LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedInStatus();
        if (LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)) {
            Intent intent = getIntent();
            facebook_uid = intent.getStringExtra("facebook_uid");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(facebook_uid).child("Online_Multiplayer");
        }
        else{
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("Online_Multiplayer");
        }





            Glide.with(getApplicationContext()).load(R.raw.random_playergif).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(waiting_for_opponent_gif);

            Intent onlineMultiplayer_intent = getIntent();
            noOfPlayers_online_multiplayer = onlineMultiplayer_intent.getStringExtra("noOfPlayers");
            entry_coins = onlineMultiplayer_intent.getStringExtra("entry_coins");
            check_online_multiplayer = onlineMultiplayer_intent.getBooleanExtra("online_multiplayer", true);


            if (!check_online_multiplayer) {
                final String hisUID = getIntent().getStringArrayExtra("UIDS")[0];
                final String myUID = getIntent().getStringArrayExtra("UIDS")[1];

                final byte[] myPic = (byte[]) mySQLDatabase.getData(myUID, MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.TABLE_NAME);

                Bitmap yourPic = BitmapFactory.decodeByteArray(myPic, 0, myPic.length);
                this.yourPic.setImageBitmap(yourPic);

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference reference = database.getReference();

                final Intent intent = getIntent();
                final String[] names = intent.getStringArrayExtra("names");
                final int[] colors = new int[]{Color.getInt(Color.RED), Color.getInt(Color.YELLOW)};
                final int noOfPlayer = 2;
                final int[] playerTypes = new int[]{PlayerType.getInt(PlayerType.HUMAN), PlayerType.getInt(PlayerType.ONLINE)};
                final String[] uids = intent.getStringArrayExtra("UIDS");
                final ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(hisUID) && dataSnapshot.child(hisUID).hasChild(myUID) && dataSnapshot.child(hisUID).child(myUID).hasChild("dice_value")
                                && dataSnapshot.child(hisUID).child(myUID).hasChild("firstUID") && dataSnapshot.child(hisUID).child(myUID).hasChild("piece")
                                && dataSnapshot.child(hisUID).child(myUID).hasChild("secondUID") && dataSnapshot.child(hisUID).child(myUID).hasChild("turn") && dataSnapshot.child(hisUID).child(myUID).hasChild("updateUI")) {
                            reference.child("started_games").removeEventListener(this);
                            final Intent intent1 = new Intent(getApplicationContext(), LudoActivity.class);
                            FirebaseDatabase.getInstance().getReference().child("Users").child(hisUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String pic_url = (String) dataSnapshot.child("thumb_image").getValue();
                                    final Bitmap[] bitmap = {null};
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                bitmap[0] = Picasso.get().load(pic_url).get();
                                                player2_bitmap = bitmap[0];

                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        player2Pic.setImageBitmap(player2_bitmap);
                                                        player2_name.setText(names[0]);
                                                        synchronized (this) {
                                                            this.notify();
                                                        }
                                                    }
                                                };

                                                synchronized (runnable) {
                                                    runOnUiThread(runnable);
                                                    runnable.wait();

                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                    intent1.putExtra("player2Pic", byteArrayOutputStream.toByteArray());
                                                    intent1.putExtra(LudoActivity.NAMES_KEY, names);
                                                    intent1.putExtra(LudoActivity.PLAYERS_KEY, noOfPlayer);
                                                    intent1.putExtra(LudoActivity.COLORS_KEY, colors);
                                                    intent1.putExtra(LudoActivity.PLAYERS_TYPE_KEY, playerTypes);
                                                    intent1.putExtra(UPDATING_USER, true);
                                                    intent1.putExtra(LudoActivity.TURN, 1);
                                                    intent1.putExtra("REFERENCE", reference.child("started_games").child(uids[0]).child(uids[1]).toString());
                                                    intent1.putExtra(UIDS, uids);
                                                    startActivity(intent1);
                                                    finish();
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                    thread.start();
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


                reference.child("started_games").addValueEventListener(eventListener);
            }


            mDatabase.child("still_searching").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDatabase.child("noOfPlayers").setValue(noOfPlayers_online_multiplayer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDatabase.child("entry_coins").setValue(entry_coins).addOnCompleteListener(new OnCompleteListener<Void>() {
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


            final DatabaseReference loopThrough = FirebaseDatabase.getInstance().getReference().child("Users");
            final ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ValueEventListener listener1 = this;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final String key = (String) ds.getKey();

                        DatabaseReference keyReference = FirebaseDatabase.getInstance().getReference().child("Users").child(key).child("Online_Multiplayer");
                        keyReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("still_searching")) {
                                    String code = dataSnapshot.child("still_searching").getValue(String.class);
                                    String NO_OF_PLAYERS =  "" + dataSnapshot.child("noOfPlayers").getValue();
                                    String ENTRY_COINS = "" + dataSnapshot.child("entry_coins").getValue();
                                    if (code.equals("true") && !key.equals(current_uid) && noOfPlayers_online_multiplayer.equals(NO_OF_PLAYERS) && entry_coins.equals(ENTRY_COINS)) {
                                        Toast.makeText(getApplicationContext(), code + "  " + key, Toast.LENGTH_LONG).show();
                                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                                        firebaseDatabase.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String opponent_name = dataSnapshot.child("name").getValue().toString();
                                                if (dataSnapshot.hasChild("thumb_image")) {
                                                    String opponent_pic = dataSnapshot.child("thumb_image").getValue().toString();
                                                    Picasso.get().load(opponent_pic).into(player2Pic);
                                                }
                                                player2_name.setText(opponent_name);
                                                loopThrough.removeEventListener(listener1);

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
                        });
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            loopThrough.addValueEventListener(listener);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.waitingForOpponent_2players));
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
