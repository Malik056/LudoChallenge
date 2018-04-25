package com.example.apple.ludochallenge.networking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apple.ludochallenge.LudoActivity;
import com.example.apple.ludochallenge.R;
import com.example.apple.ludochallenge.UserProgressData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    int noOfPlayers;
    int color = 1, vsComputer = 0;
    private ImageView logo;
    private ImageView diceGif0;
    private ImageView diceGif1;
    private ImageView diceGif2;
    private Button local_multiplayer;
    private FrameLayout frameLayout;
    private FrameLayout four_player_frameLayout;
    private FrameLayout three_player_frameLayout;
    private FrameLayout two_player_frameLayout;
    private ImageView selectNoOfPlayers_backBtn;
    private LinearLayout two_players, three_players, four_players;
    ImageView four_players_backBtn, four_players_playBtn;
    EditText four_players_player1_name, four_players_player2_name, four_players_player3_name, four_players_player4_name;
    ImageView three_players_backBtn, two_players_backBtn;
    EditText two_players_player1_name, two_players_player2_name;
    EditText three_players_player1_name, three_players_player2_name, three_players_player3_name;
    ImageView two_players_playBtn, three_players_playBtn;
    ImageView two_players_leftPress1, two_players_rightPress1, two_players_leftPress2, two_players_rightPress2;
    ImageView three_players_leftPress1, three_players_rightPress1, three_players_leftPress2, three_players_rightPress2, three_players_leftPress3, three_players_rightPress3;
    ImageView two_players_pawn1, two_players_pawn2, three_players_pawn1, three_players_pawn2, three_players_pawn3;
    int checkPawn = 0, checkPawn_3players = 0;
    int color_3players = 2;
    ImageView vsComputer_check1, vsComputer_check2, vsComputer_check3, vsComputer_check4;
    Button vsComputer_btn;
    FrameLayout vsComputer_frameLayout;
    ImageView vsComputer_playBtn, vsComputer_backBtn;
    int vsComputer_color = 1;
    ImageView edit_profile_dilaogBox_backBtn;
    LinearLayout profile_picBox;
    FrameLayout edit_profile_dialogBox;
    private ImageView flag_image, profile_image;
    private TextView userName, countryName;
    private DatabaseReference mUserDatabase;
    private StorageReference mImageStorage;
    private FirebaseUser mCurrentUser;
    private ImageView editProfile_userImage;
    private ImageView edit_profile_dialog_flagImage;
    private TextView edit_profile_dialog_userName;
    private TextView edit_profile_dialog_countryName;
    private ImageView edit_profile_dialog_coin;
    private TextView edit_profile_dialog_coinText;
    private Button playWithFriends;
    FrameLayout playWithFriends_dialogBox;
    ImageView playWithFriends_backBtn;
    private ImageView play_editProfileDialog_edit_profileBtn;
    LinearLayout playWithFriends_2players;
    public static MySQLDatabase mySQLDatabase;
    private String LOGIN_STATUS;
    String facebook_display_name;
    String facebook_country_name;
    byte[] facebook_country_image;
    byte[] facebook_photo_byte_array;
    private TextView play_dialog_coins;
    private TextView play_coins;
    private TextView play_dialog_LudoChallenge_vsComputer_win;
    private TextView play_dialog_LudoChallenge_vsComputer_lose;
    private TextView play_dialog_LudoChallenge_vsMultiplayer_win;
    private TextView play_dialog_LudoChallenge_vsMultiplayer_lose;
    private TextView play_dialog_SAL_vsMultiplayer_lose;
    private TextView play_dialog_SAL_vsMultiplayer_win;
    private TextView play_dialog_SAL_vsComputer_lose;
    private TextView play_dialog_SAL_vsComputer_win;
    private TextView play_dialog_level;




    String LudoChallenge_vsComputer_WIN;
    String LudoChallenge_vsComputer_LOSE;
    String LudoChallenge_vsMultiplayer_WIN;
    String LudoChallenge_vsMultiplayer_LOSE;
    String SAL_vsComputer_WIN;
    String SAL_vsComputer_LOSE;
    String SAL_vsMultiplayer_WIN;
    String SAL_vsMultiplayer_LOSE;
    String coins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);


//        FirebaseAuth.getInstance().signOut();

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        flag_image = (ImageView) findViewById(R.id.play_flag_pic);
        profile_image = (ImageView) findViewById(R.id.play_profile_pic);
        userName = (TextView) findViewById(R.id.play_username);
        countryName = (TextView) findViewById(R.id.play_country_name);
        logo = (ImageView) findViewById(R.id.register_logo);
        diceGif0 = (ImageView) findViewById(R.id.play_selectNoOfPlayers_dilaog_gif0);
        diceGif1 = (ImageView) findViewById(R.id.play_selectNoOfPlayers_dilaog_gif1);
        diceGif2 = (ImageView) findViewById(R.id.play_selectNoOfPlayers_dilaog_gif2);
        Glide.with(getApplicationContext()).load(R.raw.logo).into(logo);
        Glide.with(getApplicationContext()).load(R.raw.dice_gif0).into(diceGif0);
        Glide.with(getApplicationContext()).load(R.raw.dice_gif1).into(diceGif1);
        Glide.with(getApplicationContext()).load(R.raw.dice_gif0).into(diceGif2);
        two_players = (LinearLayout) findViewById(R.id.two_players);
        three_players = (LinearLayout) findViewById(R.id.three_players);
        two_players_backBtn = (ImageView) findViewById(R.id.play_two_players_back);
        two_players_player1_name = (EditText) findViewById(R.id.two_players_playerOne_name);
        two_players_player2_name = (EditText) findViewById(R.id.two_players_playerTwo_name);
        two_players_playBtn = (ImageView) findViewById(R.id.play_two_players_play);
        two_players_pawn1 = (ImageView) findViewById(R.id.two_players_pawn1);
        two_players_pawn2 = (ImageView) findViewById(R.id.two_players_pawn2);
        three_players_leftPress1 = (ImageView) findViewById(R.id.play_3players_leftBtn1);
        three_players_leftPress2 = (ImageView) findViewById(R.id.play_3players_leftBtn2);
        three_players_leftPress3 = (ImageView) findViewById(R.id.play_3players_leftBtn3);
        three_players_rightPress1 = (ImageView) findViewById(R.id.play_3players_rightBtn1);
        three_players_rightPress2 = (ImageView) findViewById(R.id.play_3players_rightBtn2);
        three_players_rightPress3 = (ImageView) findViewById(R.id.play_3players_rightBtn3);
        three_players_pawn1 = (ImageView) findViewById(R.id.three_players_pawn1);
        three_players_pawn2 = (ImageView) findViewById(R.id.three_players_pawn2);
        three_players_pawn3 = (ImageView) findViewById(R.id.three_players_pawn3);
        three_players_playBtn = (ImageView) findViewById(R.id.play_three_players_play);
        three_players_backBtn = (ImageView) findViewById(R.id.play_three_players_back);
        three_players_player1_name = (EditText) findViewById(R.id.three_players_playerOne_name);
        three_players_player2_name = (EditText) findViewById(R.id.three_players_playerTwo_name);
        three_players_player3_name = (EditText) findViewById(R.id.three_players_playerThree_name);
        vsComputer_check1 = (ImageView) findViewById(R.id.vs_computer_check1);
        vsComputer_check2 = (ImageView) findViewById(R.id.vs_computer_check2);
        vsComputer_check3 = (ImageView) findViewById(R.id.vs_computer_check3);
        vsComputer_check4 = (ImageView) findViewById(R.id.vs_computer_check4);
        vsComputer_btn = (Button) findViewById(R.id.vs_computer_btn);
        vsComputer_playBtn = (ImageView) findViewById(R.id.play_vsComputer_play);
        vsComputer_backBtn = (ImageView) findViewById(R.id.play_vsComputer_back);
        profile_picBox = (LinearLayout) findViewById(R.id.profile_pic_box);
        edit_profile_dialogBox = (FrameLayout) findViewById(R.id.edit_profile_dialogBox);
        edit_profile_dilaogBox_backBtn = (ImageView) findViewById(R.id.edit_profile_dilaogBox_backBtn);
        editProfile_userImage = (ImageView) findViewById(R.id.play_editProfile_userImage);
        edit_profile_dialog_flagImage = (ImageView) findViewById(R.id.edit_profile_dialog_flagImage);
        edit_profile_dialog_userName = (TextView) findViewById(R.id.edit_profile_dialog_userName);
        edit_profile_dialog_countryName = (TextView) findViewById(R.id.edit_profile_dialog_countryName);
        edit_profile_dialog_coin = (ImageView) findViewById(R.id.edit_profile_dialog_coin);
        edit_profile_dialog_coinText = (TextView) findViewById(R.id.play_dialog_coins);
        local_multiplayer = (Button) findViewById(R.id.play_local_multiplayer);
        frameLayout = (FrameLayout) findViewById(R.id.play_slectNoOfPlayers_dialogBox);
        selectNoOfPlayers_backBtn = (ImageView) findViewById(R.id.select_no_of_players_back);
        four_players = (LinearLayout) findViewById(R.id.four_players);
        four_player_frameLayout = (FrameLayout) findViewById(R.id.four_player_select_color);
        three_player_frameLayout =(FrameLayout) findViewById(R.id.three_player_select_color_3players);
        two_player_frameLayout =(FrameLayout) findViewById(R.id.two_player_select_color_2players);
        four_players_backBtn = (ImageView) findViewById(R.id.four_players_back);
        four_players_playBtn = (ImageView) findViewById(R.id.four_players_play);
        four_players_player1_name = (EditText) findViewById(R.id.four_players_playerOne_name);
        four_players_player2_name = (EditText) findViewById(R.id.four_players_playerTwo_name);
        four_players_player3_name = (EditText) findViewById(R.id.four_players_playerThree_name);
        four_players_player4_name = (EditText) findViewById(R.id.four_players_playerFour_name);
        two_players_leftPress1 = (ImageView) findViewById(R.id.play_2players_leftBtn1);
        two_players_leftPress2 = (ImageView) findViewById(R.id.play_2players_leftBtn2);
        two_players_rightPress1 = (ImageView) findViewById(R.id.play_2players_rightBtn1);
        two_players_rightPress2 = (ImageView) findViewById(R.id.play_2players_rightBtn2);
        vsComputer_frameLayout = (FrameLayout) findViewById(R.id.vs_computer_frameLayout);
        playWithFriends = (Button) findViewById(R.id.play_playWithFriends);
        playWithFriends_dialogBox = (FrameLayout) findViewById(R.id.play_slectNoOfPlayers_dialogBox_playWithFriends);
        playWithFriends_backBtn = (ImageView) findViewById(R.id.select_no_of_players_playWithFriends_back);
        playWithFriends_2players = (LinearLayout) findViewById(R.id.two_players_playWithFriends);
        play_editProfileDialog_edit_profileBtn = (ImageView) findViewById(R.id.play_editProfileDialog_edit_profileBtn);
        play_dialog_coins = (TextView) findViewById(R.id.play_dialog_coins);
        play_coins = (TextView) findViewById(R.id.play_coins);
        play_dialog_LudoChallenge_vsComputer_win = (TextView) findViewById(R.id.play_dialog_LudoChallenge_vsComputer_win);
        play_dialog_LudoChallenge_vsComputer_lose = (TextView) findViewById(R.id.play_dialog_LudoChallenge_vsComputer_lose);
        play_dialog_LudoChallenge_vsMultiplayer_win = (TextView) findViewById(R.id.play_dialog_LudoChallenge_vsMultiplayer_win);
        play_dialog_LudoChallenge_vsMultiplayer_lose = (TextView) findViewById(R.id.play_dialog_LudoChallenge_vsMultiplayer_lose);
        play_dialog_SAL_vsMultiplayer_lose = (TextView) findViewById(R.id.play_dialog_SAL_vsMultiplayer_lose);
        play_dialog_SAL_vsMultiplayer_win = (TextView) findViewById(R.id.play_dialog_SAL_vsMultiplayer_win);
        play_dialog_SAL_vsComputer_lose = (TextView) findViewById(R.id.play_dialog_SAL_vsComputer_lose);
        play_dialog_SAL_vsComputer_win = (TextView) findViewById(R.id.play_dialog_SAL_vsComputer_win);
        play_dialog_level = (TextView) findViewById(R.id.play_dialog_level);







        mySQLDatabase = MySQLDatabase.getInstance(this);
        LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedInStatus();



        String ID = mySQLDatabase.fetchCurrentLoggedInID();


        ArrayList<UserProgressData> userProgressData = mySQLDatabase.getUserProgressData(ID);
        coins = userProgressData.get(0).getCoins();

        for(UserProgressData user: userProgressData)
        {
            if(user.getUserGameType().getVersus().getVersus().equals(MySQLDatabase.VS_COMPUTER))
            {
                if(user.getUserGameType().getGameType().equals(MySQLDatabase.LUDO_CHALLENGE))
                {
                    LudoChallenge_vsComputer_WIN = user.getUserGameType().getVersus().getWinAndLoses().getWins();
                    LudoChallenge_vsComputer_LOSE = user.getUserGameType().getVersus().getWinAndLoses().getLoses();
                }
                else if(user.getUserGameType().getGameType().equals(MySQLDatabase.SNAKES_AND_LADDERS))
                {
                    SAL_vsComputer_WIN = user.getUserGameType().getVersus().getWinAndLoses().getWins();
                    SAL_vsComputer_LOSE = user.getUserGameType().getVersus().getWinAndLoses().getLoses();
                }
            }
            else if(user.getUserGameType().getVersus().getVersus().equals(MySQLDatabase.VS_MULTIPLAYTER))
            {
                if(user.getUserGameType().getGameType().equals(MySQLDatabase.LUDO_CHALLENGE))
                {
                    LudoChallenge_vsMultiplayer_WIN = user.getUserGameType().getVersus().getWinAndLoses().getWins();
                    LudoChallenge_vsMultiplayer_LOSE = user.getUserGameType().getVersus().getWinAndLoses().getLoses();
                }
                else if(user.getUserGameType().getGameType().equals(MySQLDatabase.SNAKES_AND_LADDERS))
                {
                    SAL_vsMultiplayer_WIN = user.getUserGameType().getVersus().getWinAndLoses().getWins();
                    SAL_vsMultiplayer_LOSE = user.getUserGameType().getVersus().getWinAndLoses().getLoses();
                }
            }

        }


        play_coins.setText(coins);
        play_dialog_coins.setText(coins);
        play_dialog_LudoChallenge_vsComputer_win.setText(LudoChallenge_vsComputer_WIN);
        play_dialog_LudoChallenge_vsComputer_lose.setText(LudoChallenge_vsComputer_LOSE);
        play_dialog_LudoChallenge_vsMultiplayer_win.setText(LudoChallenge_vsMultiplayer_WIN);
        play_dialog_LudoChallenge_vsMultiplayer_lose.setText(LudoChallenge_vsMultiplayer_LOSE);
        play_dialog_SAL_vsComputer_win.setText(SAL_vsComputer_WIN);
        play_dialog_SAL_vsComputer_lose.setText(SAL_vsComputer_LOSE);
        play_dialog_SAL_vsMultiplayer_win.setText(SAL_vsMultiplayer_WIN);
        play_dialog_SAL_vsMultiplayer_lose.setText(SAL_vsMultiplayer_LOSE);



        if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)){
//            Toast.makeText(this, "FACEBOOK LOGGED IN!", Toast.LENGTH_SHORT).show();
//            facebook_display_name = mCurrentUser.getDisplayName();
//            facebook_photo_Uri = mCurrentUser.getPhotoUrl();
            MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());

            facebook_display_name = (String) mySQLDatabase.getData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.NAME_USER, MySQLDatabase.FACEBOOK_USER_TABLE);
            facebook_country_name = (String) mySQLDatabase.getData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.NAME_FLAG, MySQLDatabase.FACEBOOK_USER_TABLE);
            facebook_photo_byte_array = (byte[]) mySQLDatabase.getData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.FACEBOOK_USER_TABLE);
            facebook_country_image = (byte[]) mySQLDatabase.getData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.PIC_FLAG, MySQLDatabase.FACEBOOK_USER_TABLE);

            userName.setText(facebook_display_name);
            edit_profile_dialog_userName.setText(facebook_display_name);
            countryName.setText(facebook_country_name);
            edit_profile_dialog_countryName.setText(facebook_country_name);
            Bitmap bitmap = BitmapFactory.decodeByteArray(facebook_photo_byte_array, 0, facebook_photo_byte_array.length);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(facebook_country_image, 0, facebook_country_image.length);
            editProfile_userImage.setImageBitmap(bitmap);
            profile_image.setImageBitmap(bitmap);
            flag_image.setImageBitmap(bitmap1);
            edit_profile_dialog_flagImage.setImageBitmap(bitmap1);
        }



        if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_LUDOCHALLENGE)) {
            byte[] d_profilePic = (byte[]) mySQLDatabase.getData(current_uid, MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.TABLE_NAME);
            byte[] d_flagPic = (byte[]) mySQLDatabase.getData(current_uid, MySQLDatabase.PIC_FLAG, MySQLDatabase.TABLE_NAME);
            String d_userName = (String) mySQLDatabase.getData(current_uid, MySQLDatabase.NAME_USER, MySQLDatabase.TABLE_NAME);
            String d_countryName = (String) mySQLDatabase.getData(current_uid, MySQLDatabase.NAME_FLAG, MySQLDatabase.TABLE_NAME);


            Bitmap bitmap = BitmapFactory.decodeByteArray(d_profilePic, 0, d_profilePic.length);
            profile_image.setImageBitmap(bitmap);
            editProfile_userImage.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeByteArray(d_flagPic, 0, d_flagPic.length);
            flag_image.setImageBitmap(bitmap);
            edit_profile_dialog_flagImage.setImageBitmap(bitmap);
            userName.setText(d_userName);
            countryName.setText(d_countryName);
            edit_profile_dialog_userName.setText(d_userName);
            edit_profile_dialog_countryName.setText(d_countryName);
        }


//        mUserDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild("flag_image")) {
//                    String username = dataSnapshot.child("name").getValue().toString();
//                    String country_name = dataSnapshot.child("country").getValue().toString();
//                    String flagImage = dataSnapshot.child("flag_image").getValue().toString();
//                    String profile_pic = dataSnapshot.child("thumb_image").getValue().toString();
//                    userName.setText(username);
//                    countryName.setText(country_name);
//                    Picasso.get().load(flagImage).placeholder(R.drawable.pakistan).into(flag_image);
//                    if (!profile_pic.equals("default")) {
//                        Picasso.get().load(profile_pic).placeholder(R.drawable.default_pic).into(profile_image);
//                    }
//                    editProfile_userImage.setImageDrawable(profile_image.getDrawable());
//                    edit_profile_dialog_flagImage.setImageDrawable(flag_image.getDrawable());
//                    edit_profile_dialog_userName.setText(username);
//                    edit_profile_dialog_countryName.setText(country_name);
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


        playWithFriends_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playWithFriends_dialogBox.setVisibility(View.GONE);
            }
        });

        playWithFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)){
                    Intent intent = new Intent(PlayActivity.this, UsersActivity.class);
                    intent.putExtra("noOfPlayers",noOfPlayers);
                    intent.putExtra("color",color);
                    intent.putExtra("vsComputer",vsComputer);
                    startActivity(intent);
                }else {
                    playWithFriends_dialogBox.setVisibility(View.VISIBLE);
                }
            }
        });

        playWithFriends_2players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOfPlayers = 2;
                color = 3;
                vsComputer = 0;
                playWithFriends_dialogBox.setVisibility(View.GONE);
                Intent intent = new Intent(PlayActivity.this, UsersActivity.class);
                intent.putExtra("noOfPlayers",noOfPlayers);
                intent.putExtra("color",color);
                intent.putExtra("vsComputer",vsComputer);
                startActivity(intent);
            }
        });

        play_editProfileDialog_edit_profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_editProfileDialog_edit_profileBtn.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });







        profile_picBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_profile_dialogBox.setVisibility(View.VISIBLE);
                local_multiplayer.setClickable(false);
                vsComputer_btn.setClickable(false);
            }
        });
        edit_profile_dilaogBox_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_profile_dialogBox.setVisibility(View.GONE);
            }
        });

        local_multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.VISIBLE);
            }
        });

        vsComputer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vsComputer_frameLayout.setVisibility(View.VISIBLE);
            }
        });

        vsComputer_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vsComputer_frameLayout.setVisibility(View.GONE);
            }
        });
        selectNoOfPlayers_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
            }
        });

        four_players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
                four_player_frameLayout.setVisibility(View.VISIBLE);
            }
        });
        three_players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
                three_player_frameLayout.setVisibility(View.VISIBLE);
            }
        });
        two_players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
                two_player_frameLayout.setVisibility(View.VISIBLE);
            }
        });
        four_players_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                four_player_frameLayout.setVisibility(View.GONE);
            }
        });
        three_players_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                three_player_frameLayout.setVisibility(View.GONE);
            }
        });

        two_players_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                two_player_frameLayout.setVisibility(View.GONE);
            }
        });

        four_players_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOfPlayers = 4;
                color = 0;
                String player1_name = four_players_player1_name.getText().toString();
                String player2_name = four_players_player2_name.getText().toString();
                String player3_name = four_players_player3_name.getText().toString();
                String player4_name = four_players_player4_name.getText().toString();

                if(TextUtils.isEmpty(player1_name) || TextUtils.isEmpty(player2_name) || TextUtils.isEmpty(player3_name) || TextUtils.isEmpty(player4_name)){
                    Toast.makeText(getApplicationContext(), "Please enter name!", Toast.LENGTH_SHORT).show();
                }
                else{
                    four_player_frameLayout.setVisibility(View.GONE);
                    Intent intent = new Intent(PlayActivity.this, LudoActivity.class);
                    intent.putExtra("noOfPlayers",noOfPlayers);
                    intent.putExtra("color",color);
                    intent.putExtra("vsComputer",vsComputer);
                    startActivity(intent);
                }
            }
        });

        three_players_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOfPlayers = 3;
                String player1_name = three_players_player1_name.getText().toString();
                String player2_name = three_players_player2_name.getText().toString();
                String player3_name = three_players_player3_name.getText().toString();

                if(TextUtils.isEmpty(player1_name) || TextUtils.isEmpty(player2_name) || TextUtils.isEmpty(player3_name)){
                    Toast.makeText(getApplicationContext(), "Please enter name!", Toast.LENGTH_SHORT).show();
                }
                else{
                    three_player_frameLayout.setVisibility(View.GONE);
                    Intent intent = new Intent(PlayActivity.this, LudoActivity.class);
                    intent.putExtra("noOfPlayers",noOfPlayers);
                    intent.putExtra("color",color_3players);
                    intent.putExtra("vsComputer",vsComputer);
                    startActivity(intent);
                    three_players_player1_name.setText("");
                    three_players_player2_name.setText("");
                    three_players_player3_name.setText("");
                }
            }
        });

        two_players_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOfPlayers = 2;
                String player1_name = two_players_player1_name.getText().toString();
                String player2_name = two_players_player2_name.getText().toString();


                if(TextUtils.isEmpty(player1_name) || TextUtils.isEmpty(player2_name)){
                    Toast.makeText(getApplicationContext(), "Please enter name!", Toast.LENGTH_SHORT).show();
                }
                else{
                    two_player_frameLayout.setVisibility(View.GONE);
                    Intent intent = new Intent(PlayActivity.this, LudoActivity.class);
                    intent.putExtra("noOfPlayers",noOfPlayers);
                    intent.putExtra("color",color);
                    intent.putExtra("vsComputer",vsComputer);
                    startActivity(intent);
                    two_players_player1_name.setText("");
                    two_players_player2_name.setText("");
                }
            }
        });
        vsComputer_playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    noOfPlayers = 2;
                    vsComputer = 1;
                    vsComputer_frameLayout.setVisibility(View.GONE);
                    Intent intent = new Intent(PlayActivity.this, LudoActivity.class);
                    intent.putExtra("noOfPlayers",noOfPlayers);
                    intent.putExtra("color",vsComputer_color);
                    intent.putExtra("vsComputer",vsComputer);
                    startActivity(intent);
                }
        });

        setColor();
        vsComputer();
    }


    void setColor(){

        two_players_leftPress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn == 0) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_blue_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_green_pawn);
                    checkPawn = 1;
                    color = 3;
                }
                else if(checkPawn == 1) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_red_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn = 0;
                    checkPawn = 0;
                    color = 1;
                }
            }
        });
        two_players_rightPress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn == 0) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_blue_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_green_pawn);
                    checkPawn = 1;
                    color = 3;
                }
                else if(checkPawn == 1) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_red_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn = 0;
                    checkPawn = 0;
                    color = 1;
                }
            }
        });
        two_players_leftPress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn == 0) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_blue_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_green_pawn);
                    color = 3;
                    checkPawn = 1;
                }
                else if(checkPawn == 1) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_red_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    color = 1;
                    checkPawn = 0;
                }
            }
        });
        two_players_rightPress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn == 0) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_blue_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_green_pawn);
                    color = 3;
                    checkPawn = 1;
                }
                else if(checkPawn == 1) {
                    two_players_pawn1.setImageResource(R.drawable.select_your_color_red_pawn);
                    two_players_pawn2.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    color = 1;
                    checkPawn = 0;
                }
            }
        });

        three_players_leftPress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn_3players == 0){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_red_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_blue_pawn);
                    checkPawn_3players = 1;
                    color_3players = 4;
                }
                else if(checkPawn_3players == 1){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_green_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_blue_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn_3players = 0;
                    color_3players = 2;
                }

            }
        });
        three_players_leftPress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn_3players == 0){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_red_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_blue_pawn);
                    checkPawn_3players = 1;
                    color_3players = 4;
                }
                else if(checkPawn_3players == 1){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_green_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_blue_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn_3players = 0;
                    color_3players = 2;
                }
            }
        });
        three_players_leftPress3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn_3players == 0){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_red_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_blue_pawn);
                    checkPawn_3players = 1;
                    color_3players = 4;
                }
                else if(checkPawn_3players == 1){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_green_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_blue_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn_3players = 0;
                    color_3players = 2;
                }
            }
        });
        three_players_rightPress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn_3players == 0){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_red_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_blue_pawn);
                    checkPawn_3players = 1;
                    color_3players = 4;
                }
                else if(checkPawn_3players == 1){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_green_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_blue_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn_3players = 0;
                    color_3players = 2;
                }
            }
        });
        three_players_rightPress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn_3players == 0){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_red_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_blue_pawn);
                    checkPawn_3players = 1;
                    color_3players = 4;
                }
                else if(checkPawn_3players == 1){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_green_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_blue_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn_3players = 0;
                    color_3players = 2;
                }
            }
        });
        three_players_rightPress3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPawn_3players == 0){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_red_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_blue_pawn);
                    checkPawn_3players = 1;
                    color_3players = 4;
                }
                else if(checkPawn_3players == 1){
                    three_players_pawn1.setImageResource(R.drawable.select_your_color_green_pawn);
                    three_players_pawn2.setImageResource(R.drawable.select_your_color_blue_pawn);
                    three_players_pawn3.setImageResource(R.drawable.select_your_color_yellow_pawn);
                    checkPawn_3players = 0;
                    color_3players = 2;
                }
            }
        });
    }

    void vsComputer(){
        vsComputer_check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vsComputer_check1.setImageResource(R.drawable.circle_tick);
                vsComputer_check2.setImageResource(R.drawable.dull_circle);
                vsComputer_check3.setImageResource(R.drawable.dull_circle);
                vsComputer_check4.setImageResource(R.drawable.dull_circle);
                vsComputer_color = 1;
            }
        });
        vsComputer_check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vsComputer_check1.setImageResource(R.drawable.dull_circle);
                vsComputer_check2.setImageResource(R.drawable.circle_tick);
                vsComputer_check3.setImageResource(R.drawable.dull_circle);
                vsComputer_check4.setImageResource(R.drawable.dull_circle);
                vsComputer_color = 3;
            }
        });
        vsComputer_check3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vsComputer_check1.setImageResource(R.drawable.dull_circle);
                vsComputer_check2.setImageResource(R.drawable.dull_circle);
                vsComputer_check3.setImageResource(R.drawable.circle_tick);
                vsComputer_check4.setImageResource(R.drawable.dull_circle);
                vsComputer_color = 2;
            }
        });
        vsComputer_check4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vsComputer_check1.setImageResource(R.drawable.dull_circle);
                vsComputer_check2.setImageResource(R.drawable.dull_circle);
                vsComputer_check3.setImageResource(R.drawable.dull_circle);
                vsComputer_check4.setImageResource(R.drawable.circle_tick);
                vsComputer_color = 0;
            }
        });
    }
}
