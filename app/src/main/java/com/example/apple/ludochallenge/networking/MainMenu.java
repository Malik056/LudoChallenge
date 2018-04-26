package com.example.apple.ludochallenge.networking;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apple.ludochallenge.R;
import com.example.apple.ludochallenge.UserProgressData;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements RewardedVideoAdListener {

    private ImageView logo;
    private ImageView spin;
    private Button play;
    private ImageView profile_pic;
    private TextView userName;
    private ImageView flagPic;
    private TextView countryName;
    private LinearLayout profile_pic_box;
    private FrameLayout editProfile_dialog_box;
    private ImageView editProfile_userImage;
    private DatabaseReference mUserDatabase;
    private StorageReference mImageStorage;
    private FirebaseUser mCurrentUser;
    private ImageView edit_profile_flagImage;
    private TextView edit_profile_userName;
    private TextView edit_profile_countryName;
    private ImageView editProfile_backBtn;
    private ImageView edit_profile;
    private String check_activity;
    String clickedCountryName;
    String getSettingsUsername;
    String LOGIN_STATUS;
    String facebook_display_name;
    String facebook_country_name;
    byte[] facebook_country_image;
    byte[] facebook_photo_byte_array;
    private TextView mainMenu_coins;
    private TextView mainMenu_dialog_coins;
    private TextView mainMenu_dialog_LudoChallenge_vsComputer_win;
    private TextView mainMenu_dialog_LudoChallenge_vsComputer_lose;
    private TextView mainMenu_dialog_LudoChallenge_vsMultiplayer_win;
    private TextView mainMenu_dialog_LudoChallenge_vsMultiplayer_lose;
    private TextView mainMenu_dialog_SAL_vsMultiplayer_lose;
    private TextView mainMenu_dialog_SAL_vsMultiplayer_win;
    private TextView mainMenu_dialog_SAL_vsComputer_lose;
    private TextView mainMenu_dialog_SAL_vsComputer_win;
    private TextView mainMenu_dialog_level;
    public static MySQLDatabase mySQLDatabase;
    private ImageView watchAd;
    private RewardedVideoAd rewardedVideoAd;
    private Button mainMenu_exit;
    private FrameLayout mainMenu_exit_dialog;
    private ImageView mainMenu_exit_dialog_yes;
    private ImageView mainMenu_exit_dialog_no;
    private Button mainMenu_settingsBtn;
    private FrameLayout mainMenu_settings_dialog;
    private ImageView mainMenu_settings_dialog_moreGames;
    private ImageView mainMenu_settings_dialog_registerYourAccount;
    private ImageView mainMenu_settings_dialog_signIn;
    private ImageView mainMenu_settings_dialog_logOut;
    private FirebaseAuth mAuth;
    private ImageView mainMenu_rate;
    private ImageView mainMenu_share;
    private ImageView mainMenu_like;
    private ImageView mainMenu_settings_dialog_back;
    private  String current_uid;
    private ImageView mainMenu_coinIcon;


    final int REQUEST_CODE_GALLERY = 999;


    String LudoChallenge_vsComputer_WIN;
    String LudoChallenge_vsComputer_LOSE;
    String LudoChallenge_vsMultiplayer_WIN;
    String LudoChallenge_vsMultiplayer_LOSE;
    String SAL_vsComputer_WIN;
    String SAL_vsComputer_LOSE;
    String SAL_vsMultiplayer_WIN;
    String SAL_vsMultiplayer_LOSE;
    String coins;
    private  ArrayList<UserProgressData> userProgressData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        watchAd = (ImageView) findViewById(R.id.ad);

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        logo = (ImageView) findViewById(R.id.register_logo);
        spin = (ImageView) findViewById(R.id.spin);
        Glide.with(getApplicationContext()).load(R.raw.logo).into(logo);
        Glide.with(getApplicationContext()).load(R.raw.spin).into(spin);
        Glide.with(getApplicationContext()).load(R.raw.add).into(watchAd);
        mAuth = FirebaseAuth.getInstance();

        play = (Button) findViewById(R.id.mainMenu_playBtn);
        profile_pic = (ImageView) findViewById(R.id.mainMenu_profile_pic);
        userName = (TextView) findViewById(R.id.mainMenu_username);
        flagPic = (ImageView) findViewById(R.id.mainMenu_flag_picture);
        countryName = (TextView) findViewById(R.id.mainMenu_country_name);
        profile_pic_box = (LinearLayout) findViewById(R.id.mainMenu_profile_pic_box);
        editProfile_dialog_box = (FrameLayout) findViewById(R.id.mainMenu_edit_profile_dialogBox);
        editProfile_userImage = (ImageView) findViewById(R.id.mainMenu_editProfile_userImage);
        edit_profile_userName = (TextView) findViewById(R.id.mainMen_edit_profile_userName);
        edit_profile_flagImage = (ImageView) findViewById(R.id.mainMenu_edit_profile_flagImage);
        edit_profile_countryName = (TextView) findViewById(R.id.mainMenu_edit_profile_countryName);
        editProfile_backBtn = (ImageView) findViewById(R.id.mainMenu_edit_profile_backBtn);
        edit_profile = (ImageView) findViewById(R.id.mainMenu_profileDialog_editProfile);
        mainMenu_coins = (TextView) findViewById(R.id.mainMenu_coins);
        mainMenu_dialog_coins = (TextView) findViewById(R.id.mainMenu_dialog_coins);
        mainMenu_dialog_LudoChallenge_vsComputer_win = (TextView) findViewById(R.id.mainMenu_dialog_LudoChallenge_vsComputer_win);
        mainMenu_dialog_LudoChallenge_vsComputer_lose = (TextView) findViewById(R.id.mainMenu_dialog_LudoChallenge_vsComputer_lose);
        mainMenu_dialog_LudoChallenge_vsMultiplayer_win = (TextView) findViewById(R.id.mainMenu_dialog_LudoChallenge_vsMultiplayer_win);
        mainMenu_dialog_LudoChallenge_vsMultiplayer_lose = (TextView) findViewById(R.id.mainMenu_dialog_LudoChallenge_vsMultiplayer_lose);
        mainMenu_dialog_SAL_vsMultiplayer_lose = (TextView) findViewById(R.id.mainMenu_dialog_SAL_vsMultiplayer_lose);
        mainMenu_dialog_SAL_vsMultiplayer_win = (TextView) findViewById(R.id.mainMenu_dialog_SAL_vsMultiplayer_win);
        mainMenu_dialog_SAL_vsComputer_lose = (TextView) findViewById(R.id.mainMenu_dialog_SAL_vsComputer_lose);
        mainMenu_dialog_SAL_vsComputer_win = (TextView) findViewById(R.id.mainMenu_dialog_SAL_vsComputer_win);
        mainMenu_dialog_level = (TextView) findViewById(R.id.mainMenu_dialog_level);
        mainMenu_exit = (Button) findViewById(R.id.mainMenu_exit);
        mainMenu_exit_dialog = (FrameLayout) findViewById(R.id.mainMenu_exit_dialog);
        mainMenu_exit_dialog_yes = (ImageView) findViewById(R.id.mainMenu_exit_dialog_yes);
        mainMenu_exit_dialog_no = (ImageView) findViewById(R.id.mainMenu_exit_dialog_no);
        mainMenu_settingsBtn = (Button) findViewById(R.id.mainMenu_settingsBtn);
        mainMenu_settings_dialog = (FrameLayout) findViewById(R.id.mainMenu_settings_dialog);
        mainMenu_settings_dialog_moreGames = (ImageView) findViewById(R.id.mainMenu_settings_dialog_moreGames);
        mainMenu_settings_dialog_registerYourAccount= (ImageView) findViewById(R.id.mainMenu_settings_dialog_registerYourAccount);
        mainMenu_settings_dialog_signIn = (ImageView) findViewById(R.id.mainMenu_settings_dialog_signIn);
        mainMenu_settings_dialog_logOut = (ImageView) findViewById(R.id.mainMenu_settings_dialog_logOut);
        mainMenu_settings_dialog_back = (ImageView) findViewById(R.id.mainMenu_settings_dialog_back);
        mainMenu_rate = (ImageView) findViewById(R.id.mainMenu_rate);
        mainMenu_share = (ImageView) findViewById(R.id.mainMenu_share);
        mainMenu_like = (ImageView) findViewById(R.id.mainMenu_like);
        mainMenu_coinIcon = (ImageView) findViewById(R.id.mainMenu_coinIcon);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        //GET LOGIN STATUS FACEBOOK/LUDOCHALLENGE
        mySQLDatabase = MySQLDatabase.getInstance(this);
        LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedInStatus();

        if(!LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_PLAY_AS_GUEST)) {
            current_uid = mCurrentUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
            String ID = mySQLDatabase.fetchCurrentLoggedInID();

            userProgressData = mySQLDatabase.getUserProgressData(ID);
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
                else if(user.getUserGameType().getVersus().getVersus().equals(MySQLDatabase.VS_MULTIPLAYTER)) {
                    if (user.getUserGameType().getGameType().equals(MySQLDatabase.LUDO_CHALLENGE)) {
                        LudoChallenge_vsMultiplayer_WIN = user.getUserGameType().getVersus().getWinAndLoses().getWins();
                        LudoChallenge_vsMultiplayer_LOSE = user.getUserGameType().getVersus().getWinAndLoses().getLoses();
                    } else if (user.getUserGameType().getGameType().equals(MySQLDatabase.SNAKES_AND_LADDERS)) {
                        SAL_vsMultiplayer_WIN = user.getUserGameType().getVersus().getWinAndLoses().getWins();
                        SAL_vsMultiplayer_LOSE = user.getUserGameType().getVersus().getWinAndLoses().getLoses();
                    }
                }

            }

            mainMenu_coins.setText(coins);
            mainMenu_dialog_coins.setText(coins);
            mainMenu_dialog_LudoChallenge_vsComputer_win.setText(LudoChallenge_vsComputer_WIN);
            mainMenu_dialog_LudoChallenge_vsComputer_lose.setText(LudoChallenge_vsComputer_LOSE);
            mainMenu_dialog_LudoChallenge_vsMultiplayer_win.setText(LudoChallenge_vsMultiplayer_WIN);
            mainMenu_dialog_LudoChallenge_vsMultiplayer_lose.setText(LudoChallenge_vsMultiplayer_LOSE);
            mainMenu_dialog_SAL_vsComputer_win.setText(SAL_vsComputer_WIN);
            mainMenu_dialog_SAL_vsComputer_lose.setText(SAL_vsComputer_LOSE);
            mainMenu_dialog_SAL_vsMultiplayer_win.setText(SAL_vsMultiplayer_WIN);
            mainMenu_dialog_SAL_vsMultiplayer_lose.setText(SAL_vsMultiplayer_LOSE);


        }









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
            edit_profile_userName.setText(facebook_display_name);
            countryName.setText(facebook_country_name);
            edit_profile_countryName.setText(facebook_country_name);
            Bitmap bitmap = BitmapFactory.decodeByteArray(facebook_photo_byte_array, 0, facebook_photo_byte_array.length);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(facebook_country_image, 0, facebook_country_image.length);
            editProfile_userImage.setImageBitmap(bitmap);
            profile_pic.setImageBitmap(bitmap);
            flagPic.setImageBitmap(bitmap1);
            edit_profile_flagImage.setImageBitmap(bitmap1);

        }

    if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_LUDOCHALLENGE)) {
        byte[] d_profilePic = (byte[]) mySQLDatabase.getData(current_uid, MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.TABLE_NAME);
        byte[] d_flagPic = (byte[]) mySQLDatabase.getData(current_uid, MySQLDatabase.PIC_FLAG, MySQLDatabase.TABLE_NAME);
        String d_userName = (String) mySQLDatabase.getData(current_uid, MySQLDatabase.NAME_USER, MySQLDatabase.TABLE_NAME);
        String d_countryName = (String) mySQLDatabase.getData(current_uid, MySQLDatabase.NAME_FLAG, MySQLDatabase.TABLE_NAME);

        Bitmap bitmap = BitmapFactory.decodeByteArray(d_profilePic, 0, d_profilePic.length);
        profile_pic.setImageBitmap(bitmap);
        editProfile_userImage.setImageBitmap(bitmap);
        bitmap = BitmapFactory.decodeByteArray(d_flagPic, 0, d_flagPic.length);
        flagPic.setImageBitmap(bitmap);
        edit_profile_flagImage.setImageBitmap(bitmap);
        userName.setText(d_userName);
        countryName.setText(d_countryName);
        edit_profile_userName.setText(d_userName);
        edit_profile_countryName.setText(d_countryName);

    }

    if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_PLAY_AS_GUEST)) {

        flagPic.setVisibility(View.GONE);
        edit_profile_flagImage.setVisibility(View.GONE);
        userName.setText("Guest");
        countryName.setVisibility(View.GONE);
        edit_profile_userName.setText("Guest");
        edit_profile_countryName.setVisibility(View.GONE);
        mainMenu_coins.setVisibility(View.GONE);
        mainMenu_coinIcon.setVisibility(View.GONE);

    }




        final Intent intent = getIntent();
        check_activity = intent.getStringExtra("settings_activity_to_main");
        clickedCountryName = intent.getStringExtra("country_name_toMain");
        getSettingsUsername = intent.getStringExtra("userName_toMain");







        watchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rewardedVideoAd.isLoaded()){
                    rewardedVideoAd.show();
                }
                Toast.makeText(getApplicationContext(),"Ad not available right now!",Toast.LENGTH_LONG).show();

            }
        });



        mainMenu_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mainMenu_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareSub = "Let's play Ludo Challenge! \n https://play.google.com/store/apps/details?id=com.Unity3d.XIMetodi0";
                intent.putExtra(Intent.EXTRA_TEXT, shareSub);
                startActivity(Intent.createChooser(intent, "Share Ludo Challenge"));

            }
        });


        mainMenu_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.Unity3d.XIMetodi0")));
                }catch (ActivityNotFoundException e){
                    Toast.makeText(MainMenu.this, "Error rating Ludo Challenge!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mainMenu_settings_dialog_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenu_settings_dialog.setVisibility(View.GONE);
            }
        });


        mainMenu_settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenu_settings_dialog.setVisibility(View.VISIBLE);
            }
        });

        mainMenu_settings_dialog_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_LUDOCHALLENGE)) {
                    FirebaseAuth.getInstance().signOut();
                    mainMenu_settings_dialog.setVisibility(View.GONE);
                    Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(MainMenu.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                }
                if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)) {
                    LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();
                    mainMenu_settings_dialog.setVisibility(View.GONE);
                    Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(MainMenu.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainMenu_settings_dialog_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    Toast.makeText(getApplicationContext(),"You are already signed In!", Toast.LENGTH_LONG).show();
                }
                else{
                    mainMenu_settings_dialog.setVisibility(View.GONE);
                    Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        mainMenu_settings_dialog_registerYourAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenu_settings_dialog.setVisibility(View.GONE);
                Intent intent = new Intent(MainMenu.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mainMenu_settings_dialog_moreGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainMenu.this, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });


        mainMenu_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenu_exit_dialog.setVisibility(View.VISIBLE);
            }
        });

        mainMenu_exit_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        mainMenu_exit_dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenu_exit_dialog.setVisibility(View.GONE);
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile_dialog_box.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editProfile_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile_dialog_box.setVisibility(View.GONE);
                play.setClickable(true);
            }
        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, PlayActivity.class);
                startActivity(intent);
                finish();
            }
        });


        profile_pic_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_PLAY_AS_GUEST)) {
                    editProfile_dialog_box.setVisibility(View.VISIBLE);
                    play.setClickable(false);
                }
            }
        });
    }



    private void loadRewardedVideoAd() {
        if(!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                    new AdRequest.Builder().build());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(this, "You don't have permission to ccess files", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                flagPic.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void local_databse(){
        mySQLDatabase = MySQLDatabase.getInstance(this);

    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }


    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
