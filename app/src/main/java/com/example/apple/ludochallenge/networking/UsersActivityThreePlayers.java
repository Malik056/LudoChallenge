package com.example.apple.ludochallenge.networking;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apple.ludochallenge.FacebookFriendAdapter;
import com.example.apple.ludochallenge.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivityThreePlayers extends AppCompatActivity {

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
    private ImageView dull_circle;
    private CircleImageView second_player;
    private CircleImageView third_player;
    private CircleImageView fourth_player;
    private LinearLayout ll_player2;
    private LinearLayout ll_player3;
    private LinearLayout ll_player4;
    private TextView player2_name;
    private TextView player3_name;
    private TextView player4_name;
    private int checkCount = 0;
    private int checkCountTemp = 0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_three_players);
        getDataFromParentActivity();


        Intent intent = getIntent();
        noOfPlayers = intent.getIntExtra("noOfPlayers", 0);


        second_player = (CircleImageView) findViewById(R.id.second_player);
        third_player = (CircleImageView) findViewById(R.id.third_player);
        fourth_player = (CircleImageView) findViewById(R.id.fourth_player);
        player2_name = (TextView) findViewById(R.id.player2_name);
        player3_name = (TextView) findViewById(R.id.player3_name);
        player4_name = (TextView) findViewById(R.id.player4_name);

        ll_player2 = (LinearLayout) findViewById(R.id.ll_player2);
        ll_player3 = (LinearLayout) findViewById(R.id.ll_player3);
        ll_player4 = (LinearLayout) findViewById(R.id.ll_player4);

        ll_player2.setVisibility(View.GONE);
        ll_player3.setVisibility(View.GONE);
        ll_player4.setVisibility(View.GONE);





        mCurrent_state = "not_challenged";
//        final String user_id = getIntent().getStringExtra("user_id");
//        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mChallenge_database = FirebaseDatabase.getInstance().getReference().child("challenge");
        mGamePlay_database = FirebaseDatabase.getInstance().getReference().child("Game_started");
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
                            .inflate(R.layout.users_three_layout, parent, false);
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

                                    ImageView imageView = holder.mView.findViewById(R.id.users_three_layout_checkbox);
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
                                    ImageView challenge_btn = holder.mView.findViewById(R.id.users_three_layout_checkbox);

                                    challenge_btn.setImageResource(R.drawable.recycle_view_challenged);
                                    challenge_btn.setTag("challenged");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    holder.mView.findViewById(R.id.users_three_layout_checkbox).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {

                            if (noOfPlayers == 3) {

                                if (((ImageView) view).getTag().equals("checked")) {
                                    ((ImageView) view).setImageResource(R.drawable.dull_circle);
                                    ((ImageView) view).setTag("unChecked");
                                    checkCount--;
                                } else if (((ImageView) view).getTag().equals("unChecked")) {
                                    if (checkCount < 2) {
                                        ((ImageView) view).setImageResource(R.drawable.circle_tick);
                                        ((ImageView) view).setTag("checked");
                                        checkCount++;
                                    }
                                }

                                if (checkCount == 0) {
                                    ll_player2.setVisibility(View.GONE);
                                    ll_player3.setVisibility(View.GONE);
                                } else if (checkCount == 1 && checkCountTemp == 0) {
                                    ll_player2.setVisibility(View.VISIBLE);
                                    second_player.setImageDrawable(holder.getUserImage());
                                    player2_name.setText(holder.getUserName());
                                } else if (checkCount == 2 && checkCountTemp == 1) {
                                    if (ll_player2.getVisibility() == View.VISIBLE) {
                                        ll_player3.setVisibility(View.VISIBLE);
                                        third_player.setImageDrawable(holder.getUserImage());
                                        player3_name.setText(holder.getUserName());
                                    } else if (ll_player3.getVisibility() == View.VISIBLE) {
                                        ll_player2.setVisibility(View.VISIBLE);
                                        second_player.setImageDrawable(holder.getUserImage());
                                        player2_name.setText(holder.getUserName());
                                    }
                                } else if (checkCount == 1 && checkCountTemp == 2) {
                                    if (holder.getUserImage() == second_player.getDrawable() && holder.getUserName() == player2_name.getText().toString()) {
                                        ll_player2.setVisibility(View.GONE);
                                        player2_name.setText("Player 2");
                                    } else if (holder.getUserImage() == third_player.getDrawable() && holder.getUserName() == player3_name.getText().toString()) {
                                        ll_player3.setVisibility(View.GONE);
                                        player3_name.setText("Player 3");
                                    }
                                } else {
                                    TastyToast.makeText(getApplicationContext(), "You've selected maximum no of players", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                                }
                                checkCountTemp = checkCount;
                            }
                            else if(noOfPlayers == 4){
                                if (((ImageView) view).getTag().equals("checked")) {
                                    ((ImageView) view).setImageResource(R.drawable.dull_circle);
                                    ((ImageView) view).setTag("unChecked");
                                    checkCount--;
                                } else if (((ImageView) view).getTag().equals("unChecked")) {
                                    if (checkCount < 3) {
                                        ((ImageView) view).setImageResource(R.drawable.circle_tick);
                                        ((ImageView) view).setTag("checked");
                                        checkCount++;
                                    }
                                }

                                if (checkCount == 0) {
                                    ll_player2.setVisibility(View.GONE);
                                    ll_player3.setVisibility(View.GONE);
                                    ll_player4.setVisibility(View.GONE);
                                } else if (checkCount == 1 && checkCountTemp == 0) {
                                    ll_player2.setVisibility(View.VISIBLE);
                                    second_player.setImageDrawable(holder.getUserImage());
                                    player2_name.setText(holder.getUserName());
                                } else if (checkCount == 2 && checkCountTemp == 1) {
//                                    if (ll_player2.getVisibility() == View.VISIBLE) {
                                        ll_player3.setVisibility(View.VISIBLE);
                                        third_player.setImageDrawable(holder.getUserImage());
                                        player3_name.setText(holder.getUserName());
//                                    } else if (ll_player3.getVisibility() == View.VISIBLE) {
//                                        ll_player2.setVisibility(View.VISIBLE);
//                                        second_player.setImageDrawable(holder.getUserImage());
//                                        player2_name.setText(holder.getUserName());
//                                    }
                                } else if (checkCount == 3 && checkCountTemp == 2) {
//                                    if (ll_player3.getVisibility() == View.VISIBLE) {
                                        ll_player4.setVisibility(View.VISIBLE);
                                        fourth_player.setImageDrawable(holder.getUserImage());
                                        player4_name.setText(holder.getUserName());
//                                    } else if (ll_player4.getVisibility() == View.VISIBLE) {
//                                        ll_player3.setVisibility(View.VISIBLE);
//                                        third_player.setImageDrawable(holder.getUserImage());
//                                        player3_name.setText(holder.getUserName());
//                                    }
                                }
                                 else if ((checkCount == 2 && checkCountTemp == 3) || (checkCount == 1 && checkCountTemp == 2)) {
                                    if (holder.getUserImage() == second_player.getDrawable() && holder.getUserName() == player2_name.getText().toString()) {
                                        ll_player2.setVisibility(View.GONE);
                                        player2_name.setText("Player 2");
                                    } else if (holder.getUserImage() == third_player.getDrawable() && holder.getUserName() == player3_name.getText().toString()) {
                                        ll_player3.setVisibility(View.GONE);
                                        player3_name.setText("Player 3");
                                    }
                                    else if (holder.getUserImage() == fourth_player.getDrawable() && holder.getUserName() == player4_name.getText().toString()) {
                                        ll_player4.setVisibility(View.GONE);
                                        player4_name.setText("Player 4");
                                    }
                                } else {
                                    TastyToast.makeText(getApplicationContext(), "You've selected maximum no of players", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                                }
                                checkCountTemp = checkCount;
                            }
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
        public Drawable getUserImage(){
            return ((ImageView) mView.findViewById(R.id.user_singleImage)).getDrawable();
        }
        public String getUserName(){
            return ((TextView) mView.findViewById(R.id.users_name)).getText().toString();
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
