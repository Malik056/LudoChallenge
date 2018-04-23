package com.example.apple.ludochallenge.networking;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainMenu extends AppCompatActivity {

    private ImageView logo;
    private ImageView ad;
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

    public static MySQLDatabase mySQLDatabase;

    final int REQUEST_CODE_GALLERY = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        logo = (ImageView) findViewById(R.id.register_logo);
        ad = (ImageView) findViewById(R.id.ad);
        spin = (ImageView) findViewById(R.id.spin);
        Glide.with(getApplicationContext()).load(R.raw.logo).into(logo);
        Glide.with(getApplicationContext()).load(R.raw.spin).into(spin);
        Glide.with(getApplicationContext()).load(R.raw.add).into(ad);

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


        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);




        //GET LOGIN STATUS FACEBOOK/LUDOCHALLENGE
        mySQLDatabase = MySQLDatabase.getInstance(this);
        LOGIN_STATUS = mySQLDatabase.fetchCurrentLoggedIn();

        if(LOGIN_STATUS.equals(MySQLDatabase.LOGIN_STATUS_FACEBOOK)){
//            Toast.makeText(this, "FACEBOOK LOGGED IN!", Toast.LENGTH_SHORT).show();
//            facebook_display_name = mCurrentUser.getDisplayName();
//            facebook_photo_Uri = mCurrentUser.getPhotoUrl();
            MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());

            facebook_display_name = (String) mySQLDatabase.getData(null, MySQLDatabase.NAME_USER, MySQLDatabase.FACEBOOK_USER_TABLE);
            facebook_country_name = (String) mySQLDatabase.getData(null, MySQLDatabase.NAME_FLAG, MySQLDatabase.FACEBOOK_USER_TABLE);
            facebook_photo_byte_array = (byte[]) mySQLDatabase.getData(null, MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.FACEBOOK_USER_TABLE);
            facebook_country_image = (byte[]) mySQLDatabase.getData(null, MySQLDatabase.PIC_FLAG, MySQLDatabase.FACEBOOK_USER_TABLE);

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


        Intent intent = getIntent();
        check_activity = intent.getStringExtra("settings_activity_to_main");
        clickedCountryName = intent.getStringExtra("country_name_toMain");
        getSettingsUsername = intent.getStringExtra("userName_toMain");


//        if(check_activity.equals("0")){
//            flagPic.setImageResource(getResources().getIdentifier(clickedCountryName.toLowerCase(),"drawable",getPackageName()));
//            countryName.setText(clickedCountryName);
//            userName.setText(getSettingsUsername);
//
//            Bundle extras = getIntent().getExtras();
//            byte[] byteArray = extras.getByteArray("profileImage_toMain");
//            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            profile_pic.setImageBitmap(bmp);
//        }


/*        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("flag_image")) {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String country_name = dataSnapshot.child("country").getValue().toString();
                    final String flagImage = dataSnapshot.child("flag_image").getValue().toString();
                    final String profilePic = dataSnapshot.child("thumb_image").getValue().toString();
                    userName.setText(username);
                    countryName.setText(country_name);
                    Picasso.get().load(flagImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_pic).into(flagPic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(flagImage).placeholder(R.drawable.default_pic).into(flagPic);
                        }
                    });
                    Picasso.get().load(flagImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_pic).into(edit_profile_flagImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(flagImage).placeholder(R.drawable.default_pic).into(edit_profile_flagImage);
                        }
                    });
//                    Picasso.get().load(flagImage).placeholder(R.drawable.pakistan).into(flagPic);
//                    Picasso.get().load(flagImage).placeholder(R.drawable.pakistan).into(edit_profile_flagImage);
                    if (!profilePic.equals("default")) {
//                        Picasso.get().load(profilePic).placeholder(R.drawable.default_pic).into(profile_pic);
//                        Picasso.get().load(profilePic).placeholder(R.drawable.default_pic).into(editProfile_userImage);
                        Picasso.get().load(profilePic).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_pic).into(profile_pic, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(profilePic).placeholder(R.drawable.default_pic).into(profile_pic);
                            }
                        });
                        Picasso.get().load(profilePic).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_pic).into(editProfile_userImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(profilePic).placeholder(R.drawable.default_pic).into(editProfile_userImage);
                            }
                        });
                    }
//                    editProfile_userImage.setImageDrawable(profile_pic.getDrawable());
//                    edit_profile_flagImage.setImageDrawable(flagPic.getDrawable());
                    edit_profile_userName.setText(username);
                    edit_profile_countryName.setText(country_name);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile_dialog_box.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
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
            }
        });


        profile_pic_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile_dialog_box.setVisibility(View.VISIBLE);
                play.setClickable(false);
            }
        });
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
}
