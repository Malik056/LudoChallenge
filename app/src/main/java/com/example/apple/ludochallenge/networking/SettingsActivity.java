package com.example.apple.ludochallenge.networking;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.ludochallenge.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private ArrayList<CountryItem> mCountryList;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    TextView textView;
    ImageView imageView;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog mLoadingBar;
    private Bitmap thumb_bitmap;
    private Button temp_users;
    private int screen_Height;
    private int screen_Width;
    private ImageView selectYourCountry;
    private  CountryAdapter mAdapter;
    private ImageView edit_profile_flagImage;
    private EditText edit_profileUserName;
    String userNamefromPreviousActivity;
    String userNamefromPreviousActivity_register;
    private int profileImage_id;
    private ImageView edit_profile_continue;
    private ImageView edit_profile_profileImage;
    String clickedCountryName;
    private DatabaseReference mTokenDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference mDatabase;
    int check_activity = 0;
    String thumb_image;
    String flagImage = null;
    Bitmap registerActivity_flagBitmap;
    Bundle extras;
    ImageView registerActivity_flagView;
    int check_performClick = 0;
    String countryName_toMain;
    public static MySQLDatabase mySQLDatabase;
    final int REQUEST_CODE_GALLERY = 999;
    String editProfileDialog_toSetting = "0";
    String download_url;
    String thumb_downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ActivityCompat.requestPermissions(
               SettingsActivity.this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY

        );

        local_databse();

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);
        mImageStorage = FirebaseStorage.getInstance().getReference();
//        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.image);
//        temp_users = (Button) findViewById(R.id.temp_users);
        selectYourCountry = (ImageView) findViewById(R.id.edit_profile_selectYourCountryBtn);
        final Spinner spinnerCountries = findViewById(R.id.edit_profile_spinnerCountries);
        edit_profile_flagImage = (ImageView) findViewById(R.id.edit_profile_flagImage);
        edit_profileUserName  = (EditText) findViewById(R.id.edit_profileUserName);
        edit_profile_continue = (ImageView) findViewById(R.id.edit_profile_continue);
        edit_profile_profileImage = (ImageView) findViewById(R.id.edit_profile_profileImage);


        Intent intent = getIntent();

        userNamefromPreviousActivity = intent.getStringExtra("userName");
        check_activity = intent.getIntExtra("check", 0);
        clickedCountryName = intent.getStringExtra("country_name");
        countryName_toMain = clickedCountryName;


        loadingBar = new ProgressDialog(this);
        GetScreen_Width_Height();
        initList();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        if(check_activity == 1) {
            edit_profile_flagImage.setImageResource(getResources().getIdentifier(clickedCountryName.toLowerCase(),"drawable",getPackageName()));
            userNamefromPreviousActivity_register = intent.getStringExtra("userName_register");
            edit_profileUserName.setText(userNamefromPreviousActivity_register);
        }
        else{
//            editProfileDialog_toSetting = intent.getStringExtra("editProfileDialog_toSetting");
            edit_profileUserName.setText(userNamefromPreviousActivity);

            mySQLDatabase = MySQLDatabase.getInstance(this);
            byte[] d_profilePic = (byte[]) mySQLDatabase.getData(current_uid, MySQLDatabase.IMAGE_PROFILE_COL, MySQLDatabase.TABLE_NAME);
            byte[] d_flagPic = (byte[]) mySQLDatabase.getData(current_uid, MySQLDatabase.PIC_FLAG, MySQLDatabase.TABLE_NAME);
            String d_userName = (String) mySQLDatabase.getData(current_uid, MySQLDatabase.NAME_USER, MySQLDatabase.TABLE_NAME);
            String d_countryName = (String) mySQLDatabase.getData(current_uid, MySQLDatabase.NAME_FLAG, MySQLDatabase.TABLE_NAME);


            Bitmap bitmap = BitmapFactory.decodeByteArray(d_profilePic,0,d_profilePic.length);
            edit_profile_profileImage.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeByteArray(d_flagPic,0,d_flagPic.length);
            edit_profile_flagImage.setImageBitmap(bitmap);
            edit_profileUserName.setText(d_userName);
            countryName_toMain = d_countryName;
        }

//        if(editProfileDialog_toSetting.equals("1")){
//
//        }

        //Enable Offline Capabilities
        mUserDatabase.keepSynced(true);

        edit_profile_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        selectYourCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                spinnerCountries.performClick();
                check_performClick = 1;

            }
        });


        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnerCountries);

            // Set popupWindow height to 500px
            popupWindow.setHeight(screen_Height/3);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        mAdapter = new CountryAdapter(getApplicationContext(), mCountryList);
        spinnerCountries.setAdapter(mAdapter);


        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                CountryItem clickedItem = (CountryItem) adapterView.getItemAtPosition(position);

                clickedCountryName = clickedItem.getCountryName();
                if(check_performClick == 1) {
                    edit_profile_flagImage.setImageResource(clickedItem.getFlagImage());
                    countryName_toMain = clickedCountryName;
                }


//                Toast.makeText(getApplicationContext(),clickedCountryName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCountries.setEnabled(false);

        edit_profile_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check_performClick != 1) {
                    TastyToast.makeText(getApplicationContext(),"Please select your country!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                    return;
                }
                loadingBar.setTitle("Creating New Account");
                loadingBar.setMessage("Please wait, while we are creating your account");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("name", edit_profileUserName.getText().toString());
                userMap.put("image", download_url);
                userMap.put("thumb_image", thumb_downloadUrl);
                userMap.put("flag_image", String.valueOf(edit_profile_flagImage));
                userMap.put("country", clickedCountryName);
                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            storeFlag_into_Storage();
                            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                            intent.putExtra("settings_activity_to_main", "0");
                            intent.putExtra("country_name_toMain", countryName_toMain);
                            intent.putExtra("userName_toMain",    userNamefromPreviousActivity_register);


                            Bitmap bmp = ((BitmapDrawable) edit_profile_profileImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
                            byte[] byteArray = stream.toByteArray();

                            intent.putExtra("profileImage_toMain", byteArray);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            bmp = null;
                        } else {
                            TastyToast.makeText(getApplicationContext(),"Error updating your profile!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        }
                        loadingBar.dismiss();
                    }

                });

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                Bitmap bitmap = ((BitmapDrawable) edit_profile_profileImage.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                Bitmap flag_bitmap = ((BitmapDrawable) edit_profile_flagImage.getDrawable()).getBitmap();
                flag_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos2);
                mySQLDatabase.insertData(edit_profileUserName.getText().toString(), baos.toByteArray(), baos2.toByteArray(),countryName_toMain, current_uid, MySQLDatabase.TABLE_NAME);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                edit_profile_flagImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mLoadingBar.setTitle("Uploading Image");
                mLoadingBar.setMessage("Please wait, while we are uploading your image");
                mLoadingBar.setCanceledOnTouchOutside(false);
                mLoadingBar.show();
                Uri resultUri = result.getUri();
                final File thumb_filePath = new File(resultUri.getPath());
                String current_uid = mCurrentUser.getUid();
                try {
                   thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(60)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
                final byte[] thumb_byte = baos.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(thumb_byte, 0, thumb_byte.length);
                edit_profile_profileImage.setImageBitmap(bitmap);
                StorageReference filepath = mImageStorage.child("profile_images").child(current_uid + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_uid + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            download_url = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    if(task.isSuccessful()){
                                        Map updateHashMap = new HashMap<>();
                                        updateHashMap.put("image", download_url);
                                        updateHashMap.put("thumb_image",thumb_downloadUrl);
                                        mUserDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mLoadingBar.dismiss();
                                                    TastyToast.makeText(getApplicationContext(),"Profile pic uploaded successfully!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                                 }
                                            }
                                        });
                                    }
                                    else{
                                        TastyToast.makeText(getApplicationContext(),"Error uploading profile pic!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                        mLoadingBar.dismiss();
                                    }
                                }
                            });
                        }
                        else{
                            TastyToast.makeText(getApplicationContext(),"Error uploading profile pic!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            mLoadingBar.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void GetScreen_Width_Height() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_Height = displayMetrics.heightPixels;
        screen_Width = displayMetrics.widthPixels;

    }

    private void initList(){
        mCountryList = new ArrayList<>();
        mCountryList.add(new CountryItem("Afghanistan", R.drawable.afghnistan));
        mCountryList.add(new CountryItem("Albania", R.drawable.albania));
        mCountryList.add(new CountryItem("Algeria", R.drawable.algeria));
        mCountryList.add(new CountryItem("America", R.drawable.america));
        mCountryList.add(new CountryItem("Andorra", R.drawable.andorra));
        mCountryList.add(new CountryItem("Antigua", R.drawable.antigua));
        mCountryList.add(new CountryItem("Argentina", R.drawable.argentina));
        mCountryList.add(new CountryItem("Australia", R.drawable.australia));
        mCountryList.add(new CountryItem("Austria", R.drawable.austria));
        mCountryList.add(new CountryItem("Azerbaijan", R.drawable.azerbaijan));
        mCountryList.add(new CountryItem("Bahamas", R.drawable.bahamas));
        mCountryList.add(new CountryItem("Bahrain", R.drawable.bahrain));
        mCountryList.add(new CountryItem("Belarus", R.drawable.balarus));
        mCountryList.add(new CountryItem("Bangladesh", R.drawable.bangladesh));
        mCountryList.add(new CountryItem("Barbados", R.drawable.barbados));
        mCountryList.add(new CountryItem("Belgium", R.drawable.belgium));
        mCountryList.add(new CountryItem("Belize", R.drawable.belize));
        mCountryList.add(new CountryItem("Benin", R.drawable.benin));
        mCountryList.add(new CountryItem("Bhutan", R.drawable.bhutan));
        mCountryList.add(new CountryItem("Bolivia", R.drawable.bolivia));
        mCountryList.add(new CountryItem("Bosnia", R.drawable.bosnia));
        mCountryList.add(new CountryItem("Botswana", R.drawable.bostwana));
        mCountryList.add(new CountryItem("Brazil", R.drawable.brazil));
        mCountryList.add(new CountryItem("Brunei", R.drawable.brunei));
        mCountryList.add(new CountryItem("Bulgaria", R.drawable.bulgaria));
        mCountryList.add(new CountryItem("Burkina Faso", R.drawable.burkina));
        mCountryList.add(new CountryItem("Burundi", R.drawable.burundi));
        mCountryList.add(new CountryItem("Cambodia", R.drawable.camboadia));
        mCountryList.add(new CountryItem("Cameroon", R.drawable.cameroon));
        mCountryList.add(new CountryItem("Canada", R.drawable.canada));
        mCountryList.add(new CountryItem("Cape-Verde", R.drawable.cape_verde));
        mCountryList.add(new CountryItem("Central African", R.drawable.central_african));
        mCountryList.add(new CountryItem("Chad", R.drawable.chad));
        mCountryList.add(new CountryItem("Chile", R.drawable.chile));
        mCountryList.add(new CountryItem("China", R.drawable.china));
        mCountryList.add(new CountryItem("Colombia", R.drawable.colombia));
        mCountryList.add(new CountryItem("Comoros", R.drawable.comoros));
        mCountryList.add(new CountryItem("Congo-Democratic", R.drawable.congo_democratic));
        mCountryList.add(new CountryItem("Congo-Republic", R.drawable.congo_republic));
        mCountryList.add(new CountryItem("Costa Rica", R.drawable.costa_rica));
        mCountryList.add(new CountryItem("Cote-d'ivoire", R.drawable.cote_dvoire));
        mCountryList.add(new CountryItem("Croatia", R.drawable.croatia));
        mCountryList.add(new CountryItem("Cuba", R.drawable.cuba));
        mCountryList.add(new CountryItem("Cyprus", R.drawable.cyprus));
        mCountryList.add(new CountryItem("Czech-Republic", R.drawable.czech_republic));
        mCountryList.add(new CountryItem("Denmark", R.drawable.denmark));
        mCountryList.add(new CountryItem("Djibouti", R.drawable.djibout));
        mCountryList.add(new CountryItem("Dominica", R.drawable.dominica));
        mCountryList.add(new CountryItem("Dominican-Republic", R.drawable.dominican_republic));
        mCountryList.add(new CountryItem("East Timor", R.drawable.east_timor));
        mCountryList.add(new CountryItem("Ecuador", R.drawable.ecuador));
        mCountryList.add(new CountryItem("Egypt", R.drawable.egypt));
        mCountryList.add(new CountryItem("El-Salvador", R.drawable.el_salvador));
        mCountryList.add(new CountryItem("Eq-Guinea", R.drawable.equatorial_guinea));
        mCountryList.add(new CountryItem("Eritrea", R.drawable.eritrea));
        mCountryList.add(new CountryItem("Estonia", R.drawable.estonia));
        mCountryList.add(new CountryItem("Ethiopia", R.drawable.ethiopia));
        mCountryList.add(new CountryItem("Fiji", R.drawable.fiji));
        mCountryList.add(new CountryItem("Finland", R.drawable.finland));
        mCountryList.add(new CountryItem("France", R.drawable.france));
        mCountryList.add(new CountryItem("Gabon", R.drawable.gabon));
        mCountryList.add(new CountryItem("Gambia", R.drawable.gambia));
        mCountryList.add(new CountryItem("Georgia", R.drawable.georgia));
        mCountryList.add(new CountryItem("Germany", R.drawable.germany));
        mCountryList.add(new CountryItem("Ghana", R.drawable.ghana));
        mCountryList.add(new CountryItem("Greece", R.drawable.grecee));
        mCountryList.add(new CountryItem("Grenade", R.drawable.grenade));
        mCountryList.add(new CountryItem("Guatemala", R.drawable.guatemala));
        mCountryList.add(new CountryItem("Guinea-Bissau", R.drawable.guinea_bissau));
        mCountryList.add(new CountryItem("Guinea", R.drawable.guinea));
        mCountryList.add(new CountryItem("Guyana", R.drawable.guyana));
        mCountryList.add(new CountryItem("Haiti", R.drawable.haiti));
        mCountryList.add(new CountryItem("Honduras", R.drawable.honduras));
        mCountryList.add(new CountryItem("Hungary", R.drawable.hungary));
        mCountryList.add(new CountryItem("Iceland", R.drawable.iceland));
        mCountryList.add(new CountryItem("India", R.drawable.india));
        mCountryList.add(new CountryItem("Indonesia", R.drawable.indonesia));
        mCountryList.add(new CountryItem("Iran", R.drawable.iran));
        mCountryList.add(new CountryItem("Iraq", R.drawable.iraq));
        mCountryList.add(new CountryItem("Ireland", R.drawable.ireland));
        mCountryList.add(new CountryItem("Israel", R.drawable.israel));
        mCountryList.add(new CountryItem("Italy", R.drawable.italy));
        mCountryList.add(new CountryItem("Jamaica", R.drawable.jamaica));
        mCountryList.add(new CountryItem("Japan", R.drawable.japan));
        mCountryList.add(new CountryItem("Jordan", R.drawable.jordan));
        mCountryList.add(new CountryItem("Kazakhstan", R.drawable.kazakhstan));
        mCountryList.add(new CountryItem("Kenya", R.drawable.kenya));
        mCountryList.add(new CountryItem("Kiribati", R.drawable.kiribati));
        mCountryList.add(new CountryItem("Kosovo", R.drawable.kosovo));
        mCountryList.add(new CountryItem("Kuwait", R.drawable.kuwait));
        mCountryList.add(new CountryItem("Kyrgyzstan", R.drawable.kyrgyzstan));
        mCountryList.add(new CountryItem("Laos", R.drawable.laos));
        mCountryList.add(new CountryItem("Latvia", R.drawable.latvia));
        mCountryList.add(new CountryItem("Lebanon", R.drawable.lebanon));
        mCountryList.add(new CountryItem("Lesotho", R.drawable.lesotho));
        mCountryList.add(new CountryItem("Liberia", R.drawable.liberia));
        mCountryList.add(new CountryItem("Libya", R.drawable.libya));
        mCountryList.add(new CountryItem("Liechtenstein", R.drawable.liechtenstein));
        mCountryList.add(new CountryItem("Lithuania", R.drawable.lithuania));
        mCountryList.add(new CountryItem("Luxembourg", R.drawable.luxembourg));
        mCountryList.add(new CountryItem("Macedonia", R.drawable.macedonia));
        mCountryList.add(new CountryItem("Madagascar", R.drawable.madagascar));
        mCountryList.add(new CountryItem("Malawi", R.drawable.malawi));
        mCountryList.add(new CountryItem("Malaysia", R.drawable.malaysia));
        mCountryList.add(new CountryItem("Maldives", R.drawable.maldives));
        mCountryList.add(new CountryItem("Mali", R.drawable.mali));
        mCountryList.add(new CountryItem("Malta", R.drawable.malta));
        mCountryList.add(new CountryItem("Marshall", R.drawable.marshall));
        mCountryList.add(new CountryItem("Mauritania", R.drawable.mauritania));
        mCountryList.add(new CountryItem("Mauritius", R.drawable.mauritius));
        mCountryList.add(new CountryItem("Mexico", R.drawable.mexico));
        mCountryList.add(new CountryItem("Micronesia", R.drawable.micronesia_federated));
        mCountryList.add(new CountryItem("Moldova", R.drawable.moldova));
        mCountryList.add(new CountryItem("Monaco", R.drawable.monaco));
        mCountryList.add(new CountryItem("Mongolia", R.drawable.mongolia));
        mCountryList.add(new CountryItem("Montenegro", R.drawable.montenegro));
        mCountryList.add(new CountryItem("Morocco", R.drawable.morocco));
        mCountryList.add(new CountryItem("Mozambique", R.drawable.mozambique));
        mCountryList.add(new CountryItem("Myanmar", R.drawable.myanmar));
        mCountryList.add(new CountryItem("Namibia", R.drawable.namibia));
        mCountryList.add(new CountryItem("Nauru", R.drawable.nauru));
        mCountryList.add(new CountryItem("Nepal", R.drawable.nepal));
        mCountryList.add(new CountryItem("Netherlands", R.drawable.netherlands));
        mCountryList.add(new CountryItem("New Guinea", R.drawable.papua_new_guinea));
        mCountryList.add(new CountryItem("New Zealand", R.drawable.new_zealand));
        mCountryList.add(new CountryItem("Nicaragua", R.drawable.nicaragua));
        mCountryList.add(new CountryItem("Niger", R.drawable.niger));
        mCountryList.add(new CountryItem("Nigeria", R.drawable.nigeria));
        mCountryList.add(new CountryItem("North Korea", R.drawable.north_korea));
        mCountryList.add(new CountryItem("Norway", R.drawable.norway));
        mCountryList.add(new CountryItem("Oman", R.drawable.oman));
        mCountryList.add(new CountryItem("Pakistan", R.drawable.pakistan));
        mCountryList.add(new CountryItem("Palau", R.drawable.palau));
        mCountryList.add(new CountryItem("Panama", R.drawable.panama));
        mCountryList.add(new CountryItem("Paraguay", R.drawable.paraguay));
        mCountryList.add(new CountryItem("Peru", R.drawable.peru));
        mCountryList.add(new CountryItem("Philippines", R.drawable.philippines));
        mCountryList.add(new CountryItem("Poland", R.drawable.poland));
        mCountryList.add(new CountryItem("Portugal", R.drawable.potugal));
        mCountryList.add(new CountryItem("Qatar", R.drawable.qatar));
        mCountryList.add(new CountryItem("Romania", R.drawable.romania));
        mCountryList.add(new CountryItem("Russia", R.drawable.russia));
        mCountryList.add(new CountryItem("Rwanda", R.drawable.rwanda));
        mCountryList.add(new CountryItem("Saint Kitts", R.drawable.saint_kitts));
        mCountryList.add(new CountryItem("Saint Lucia", R.drawable.saint_lucia));
        mCountryList.add(new CountryItem("Saint Vincent", R.drawable.saint_vincent298526));
        mCountryList.add(new CountryItem("Samoa", R.drawable.samoa));
        mCountryList.add(new CountryItem("San Marino", R.drawable.san_marino));
        mCountryList.add(new CountryItem("Sao Tome", R.drawable.sao_tome));
        mCountryList.add(new CountryItem("Saudi Arabia", R.drawable.saudi_arabia));
        mCountryList.add(new CountryItem("Senegal", R.drawable.senegal));
        mCountryList.add(new CountryItem("Serbia", R.drawable.serbia));
        mCountryList.add(new CountryItem("Sierra Leone", R.drawable.sierra_leone));
        mCountryList.add(new CountryItem("Singapore", R.drawable.singapore));
        mCountryList.add(new CountryItem("Slovakia", R.drawable.slovakia));
        mCountryList.add(new CountryItem("Slovenia", R.drawable.slovenia));
        mCountryList.add(new CountryItem("Solomon", R.drawable.solomon_islands));
        mCountryList.add(new CountryItem("Somalia", R.drawable.somalia));
        mCountryList.add(new CountryItem("South Africa", R.drawable.south_africa));
        mCountryList.add(new CountryItem("South Korea", R.drawable.south_korea));
        mCountryList.add(new CountryItem("South Sudan", R.drawable.south_sudan));
        mCountryList.add(new CountryItem("Spain", R.drawable.spain));
        mCountryList.add(new CountryItem("Sri Lanka", R.drawable.srilanka));
        mCountryList.add(new CountryItem("Sudan", R.drawable.sudan));
        mCountryList.add(new CountryItem("Suriname", R.drawable.suriname));
        mCountryList.add(new CountryItem("Swaziland", R.drawable.swaziland));
        mCountryList.add(new CountryItem("Sweden", R.drawable.sweden));
        mCountryList.add(new CountryItem("Switzerland", R.drawable.switzerland));
        mCountryList.add(new CountryItem("Syria", R.drawable.syria));
        mCountryList.add(new CountryItem("Taiwan", R.drawable.taiwan));
        mCountryList.add(new CountryItem("Tajikistan", R.drawable.tajikistan));
        mCountryList.add(new CountryItem("Tanzania", R.drawable.tanzania));
        mCountryList.add(new CountryItem("Thailand", R.drawable.thailand));
        mCountryList.add(new CountryItem("Togo", R.drawable.togo));
        mCountryList.add(new CountryItem("Tonga", R.drawable.tonga));
        mCountryList.add(new CountryItem("Trinidad", R.drawable.trinidad));
        mCountryList.add(new CountryItem("Tunisia", R.drawable.tunisia));
        mCountryList.add(new CountryItem("Turkey", R.drawable.turkey));
        mCountryList.add(new CountryItem("Turkmenistan", R.drawable.turkmenistan));
        mCountryList.add(new CountryItem("Tuvalu", R.drawable.tuvalu));
        mCountryList.add(new CountryItem("UAE", R.drawable.uae));
        mCountryList.add(new CountryItem("Uganda", R.drawable.uganda));
        mCountryList.add(new CountryItem("UK", R.drawable.uk));
        mCountryList.add(new CountryItem("Ukraine", R.drawable.ukraine));
        mCountryList.add(new CountryItem("Uruguay", R.drawable.uruguay));
        mCountryList.add(new CountryItem("USA", R.drawable.usa));
        mCountryList.add(new CountryItem("Uzbekistan", R.drawable.uzbekistan));
        mCountryList.add(new CountryItem("Vanuatu", R.drawable.vanuatu));
        mCountryList.add(new CountryItem("Vatican City", R.drawable.vatican_city));
        mCountryList.add(new CountryItem("Venezuela", R.drawable.venezuela));
        mCountryList.add(new CountryItem("Vietnam", R.drawable.vietnam));
        mCountryList.add(new CountryItem("Yemen", R.drawable.yemen));
        mCountryList.add(new CountryItem("Zambia", R.drawable.zambia));
        mCountryList.add(new CountryItem("Zimbabwe", R.drawable.zimbabwe));



    }

    void storeFlag_into_Storage() {

        Uri imageUri = Uri.parse("android.resource://com.example.apple.ludoking/" + getResources().getIdentifier(clickedCountryName.toString().toLowerCase(), "drawable", getApplicationContext().getPackageName()));
        CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
//         CropImage.ActivityResult result = CropImage.
        final File thumb_filePath = new File(imageUri.getPath());
        String current_uid = mCurrentUser.getUid();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                 thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        final byte[] thumb_byte = baos.toByteArray();
        StorageReference filepath = mImageStorage.child("flag_images").child(current_uid + ".jpg");
        final StorageReference thumb_filepath = mImageStorage.child("flag_images").child("flag_thumbs").child(current_uid + ".jpg");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    final String download_url = task.getResult().getDownloadUrl().toString();
                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                            thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                            if (task.isSuccessful()) {
                                String token = FirebaseInstanceId.getInstance().getToken();
                                Map updateHashMap = new HashMap<>();
                                updateHashMap.put("flag_image", download_url);
                                updateHashMap.put("thumb_flag_image", thumb_downloadUrl);
                                updateHashMap.put("device_token", token);
                                mDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            TastyToast.makeText(getApplicationContext(),"Country uploaded successfully!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                        }
                                    }
                                });
                            } else {
                                TastyToast.makeText(getApplicationContext(),"Error uploading your country!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            }
                        }
                    });
                } else {
                    TastyToast.makeText(getApplicationContext(),"Error uploading your country!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                TastyToast.makeText(getApplicationContext(),"You don't have permission to access files", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void local_databse(){
        mySQLDatabase = mySQLDatabase.getInstance(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.settingsActivityRoot));
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
