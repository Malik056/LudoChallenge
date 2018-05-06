package com.example.apple.ludochallenge.networking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.ludochallenge.Color;
import com.example.apple.ludochallenge.FacebookFriendAdapter;
import com.example.apple.ludochallenge.LudoActivity;
import com.example.apple.ludochallenge.PlayerType;
import com.example.apple.ludochallenge.R;
import com.example.apple.ludochallenge.WaitingForOpponent2Players;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.apple.ludochallenge.LudoActivity.UIDS;
import static com.example.apple.ludochallenge.LudoActivity.UPDATING_USER;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private int noOfPlayers;
    private int color;
    private int vsComputer;
    MySQLDatabase mySQLDatabase;
    String LOGIN_STATUS;
    FirebaseRecyclerAdapter<Users, UsersViewHolder> adapter;
    String myName;


    private DatabaseReference mChallenge_database;
    private DatabaseReference mGamePlay_database;
    private DatabaseReference mNotification_database;
    private FirebaseUser mCurrent_user;
    String mCurrent_state;
    private DatabaseReference gameStartedDatabase;
    private DatabaseReference gameStarted;
    private Bitmap player2_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getDataFromParentActivity();


        mCurrent_state = "not_challenged";
//        final String user_id = getIntent().getStringExtra("user_id");
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mChallenge_database = FirebaseDatabase.getInstance().getReference().child("challenge");
        mGamePlay_database = FirebaseDatabase.getInstance().getReference().child("game_started");
        mNotification_database = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        gameStartedDatabase = FirebaseDatabase.getInstance().getReference();
        gameStarted = FirebaseDatabase.getInstance().getReference();

        mUsersList = findViewById(R.id.users_recyclerlist);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        myName = (String) MySQLDatabase.getInstance(getApplicationContext()).getData(mCurrent_user.getUid(),MySQLDatabase.NAME_USER, MySQLDatabase.TABLE_NAME);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference();

        mySQLDatabase = MySQLDatabase.getInstance(this);
        LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedInStatus();


        if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_LUDOCHALLENGE)) {

            FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                    .setQuery(mUsersDatabase, Users.class)
                    .build();

            adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
                @NonNull
                @Override
                public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.users_single_layout, parent, false);
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    view.setLayoutParams(new ViewGroup.LayoutParams(displayMetrics.widthPixels, displayMetrics.heightPixels / 5));
                    return new UsersViewHolder(view);

                }

                @Override
                protected void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position, @NonNull final Users model) {

                    if (getRef(position).getKey().equals(mCurrent_user.getUid())) {
                        holder.setName("You");
                        holder.mView.setBackgroundColor(android.graphics.Color.GREEN);
                        holder.setUserImage(model.getThumb_image(), getApplicationContext());
                        holder.setUserFlag(model.getFlag_image(), getApplicationContext());
                        return;
                    } else {
                        holder.mView.setBackgroundColor(android.graphics.Color.BLACK);
                    }
                    holder.setName(model.getName());
                    holder.setUserImage(model.getThumb_image(), getApplicationContext());
                    holder.setUserFlag(model.getFlag_image(), getApplicationContext());

                    final String user_id = getRef(position).getKey();

                    DatabaseReference myChallenges = mChallenge_database != null ? mChallenge_database.child(mCurrent_user.getUid()) : null;
                    DatabaseReference hisChallenges = mChallenge_database != null ? mChallenge_database.child(user_id) : null;

                    if (myChallenges != null && myChallenges.child(user_id) != null) {

                        myChallenges.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild("challenge")) {

                                    ImageView imageView = holder.mView.findViewById(R.id.challenge_btn);
                                    imageView.setImageResource(R.drawable.recyler_view_accept_challenge);
                                    imageView.setTag("accept_challenge");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else if (hisChallenges != null && hisChallenges.child(mCurrent_user.getUid()) != null) {
                        myChallenges.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("challenge")) {
                                    ImageView challenge_btn = holder.mView.findViewById(R.id.challenge_btn);

                                    challenge_btn.setImageResource(R.drawable.recycle_view_challenged);
                                    challenge_btn.setTag("challenged");



                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    holder.mView.findViewById(R.id.challenge_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.setEnabled(false);

                            if (view.getTag().equals("challenged")) {
                                mChallenge_database.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ((ImageView) view).setImageResource(R.drawable.recycler_view_challenge);
                                            view.setTag("challenge");
                                        }
                                    }
                                });
                            } else if (view.getTag().equals("challenge")) {
                                mChallenge_database.child(user_id).child(mCurrent_user.getUid()).child("challenge").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            HashMap<String, String> notificationData = new HashMap<>();
                                            notificationData.put("from", mCurrent_user.getUid());
                                            notificationData.put("type", "challenged");
                                            mNotification_database.child(user_id).setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    ((ImageView) view).setImageResource(R.drawable.recycle_view_challenged);
                                                    view.setTag("challenged");
                                                    FirebaseDatabase.getInstance().getReference().child("started_games");
                                                    Intent intent = new Intent(getApplicationContext(), WaitingForOpponent2Players.class);
                                                    intent.putExtra("UIDS", new String[]{user_id, mCurrent_user.getUid()});
                                                    intent.putExtra("names", new String[]{model.name, myName});
                                                    startActivity(intent);
                                                }
                                            });
                                        } else {
                                            Toast.makeText(UsersActivity.this, "Failed sending challenge!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });
                            } else if (view.getTag().equals("accept_challenge")) {
                                gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("players").setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            mChallenge_database.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("updateUI").setValue(0);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("turn").setValue(user_id);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("dice_value").setValue(4);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("piece").setValue(0);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child(mCurrent_user.getUid()).setValue(false);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child(user_id).setValue(false);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("updated").setValue(false);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("firstUID").setValue(user_id);
                                                            gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).child("secondUID").setValue(mCurrent_user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        view.setEnabled(true);
                                                                        final Intent intent = new Intent(getApplicationContext(), LudoActivity.class);

                                                                        FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                final String pic_url = (String) dataSnapshot.child("thumb_image").getValue();
                                                                                final Bitmap[] bitmap = {null};
                                                                                Thread thread = new Thread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        try {
                                                                                            bitmap[0] = Picasso.get().load(pic_url).get();
                                                                                            if(bitmap[0] == null)
                                                                                            {
                                                                                                bitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.default_pic2);
                                                                                            }
                                                                                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                                                            bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                                                                                            byte[] arr = byteArrayOutputStream.toByteArray();
                                                                                            byte[] array = new byte[arr.length];
                                                                                            System.arraycopy(arr, 0, array, 0, arr.length);
                                                                                            intent.putExtra("player2Pic", array);
                                                                                            String[] names = new String[]{myName, model.name};
                                                                                            int[] colors = new int[]{Color.getInt(Color.RED), Color.getInt(Color.YELLOW)};
                                                                                            int noOfPlayer = 2;
                                                                                            int[] playerTypes = new int[]{PlayerType.getInt(PlayerType.ONLINE), PlayerType.getInt(PlayerType.ONLINE)};
                                                                                            String[] uids = new String[]{mCurrent_user.getUid(), user_id};
                                                                                            intent.putExtra(LudoActivity.NAMES_KEY, names);
                                                                                            intent.putExtra(LudoActivity.PLAYERS_KEY, noOfPlayer);
                                                                                            intent.putExtra(LudoActivity.COLORS_KEY, colors);

                                                                                            intent.putExtra(LudoActivity.PLAYERS_TYPE_KEY, playerTypes);
                                                                                            intent.putExtra(UPDATING_USER, true);
                                                                                            intent.putExtra(LudoActivity.TURN, 1);
                                                                                            intent.putExtra(LudoActivity.REFERENCE, gameStarted.child("started_games").child(mCurrent_user.getUid()).child(user_id).toString());
                                                                                            intent.putExtra(UIDS, uids);
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        } catch (IOException e) {
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
                                                            });
                                                        }
                                                    });

                                        } else {
                                            Toast.makeText(UsersActivity.this, "Couldn't Connect to Server", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            view.setEnabled(true);
                        }
                    });
                }

            };
            mUsersList.setAdapter(adapter);
            adapter.startListening();
        }

        else if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)) {

            ArrayList<FacebookUser> users = mySQLDatabase.getFacebookFriendList();

            FacebookFriendAdapter adapter = new FacebookFriendAdapter(users);

            mUsersList.setAdapter(adapter);

        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.usersActivityRoot));
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Runtime.getRuntime().gc();

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
