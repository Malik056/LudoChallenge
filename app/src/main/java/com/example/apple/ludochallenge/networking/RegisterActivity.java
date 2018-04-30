 package com.example.apple.ludochallenge.networking;

 import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.apple.ludochallenge.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

 public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "FACELOG";
    private ArrayList<CountryItem> mCountryList;
    private  CountryAdapter mAdapter;
    private int screen_Height;
    private int screen_Width;
    private ImageView countryFlag;
    private TextView countryName;
    private Button selectYourCountry;
    private ImageView logo;
    private FirebaseAuth mAuth;
    private CustomEditText register_name;
    private CustomEditText register_email;
    private CustomEditText register_password;
    private Button registerBtn;
    private ProgressDialog loadingBar;
    private ImageView alreadyHaveAnAccount;
    private ImageView playAsGuest;
    private DatabaseReference mDatabase;
    private StorageReference mImageStorage;
    private FirebaseUser mCurrentUser;
    private String current_uid;
    private Bitmap thumb_bitmap;
    private DatabaseReference mTokenDatabase;
    boolean checkCountryClick = false;
    private CallbackManager mCallbackManager;
    private ImageView facebookLogin;
    private MySQLDatabase sqlDatabase;
    FirebaseUser currentUser;
    String facebook_uid;
    String finalId;
    String name;
    String email;
    Uri photoUrl;

    ArrayList<Bitmap> bitmapArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        bitmapArrayList = new ArrayList<>();

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        sqlDatabase = MySQLDatabase.getInstance(getApplicationContext());
//        current_uid = mCurrentUser.getUid();
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        selectYourCountry = (Button) findViewById(R.id.selectYourCountry);
        countryName = (TextView) findViewById(R.id.country_name);
        countryFlag = (ImageView) findViewById(R.id.country_flag);
        logo = (ImageView) findViewById(R.id.register_logo);
        Glide.with(getApplicationContext()).load(R.raw.logo).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(logo);

        register_name = (CustomEditText) findViewById(R.id.register_name);
        register_email = (CustomEditText) findViewById(R.id.register_email);
        register_password = (CustomEditText) findViewById(R.id.register_password);
        registerBtn = (Button) findViewById(R.id.register_register_button);
        loadingBar = new ProgressDialog(this);
        alreadyHaveAnAccount = (ImageView) findViewById(R.id.register_already_have_an_account_login);
        playAsGuest = (ImageView) findViewById(R.id.registerActivity_playAsGuest);
        facebookLogin = (ImageView) findViewById(R.id.registerActivity_loginWithFacebook);



        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
//        LoginButton loginButton = (LoginButton) findViewById(R.id.registerActivity_loginWithFacebook);

        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkCountryClick){
                    TastyToast.makeText(getApplicationContext(),"Please select your country!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                    return;
                }

                loadingBar.setTitle("Connecting with Facebook");
                loadingBar.setMessage("Please wait while we connect Ludo Challenge with Facebook");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                facebookLogin.setEnabled(false);

                LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        handleFacebookAccessToken(loginResult.getAccessToken());

                        GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                                loginResult.getAccessToken(),
                                "/me/friends",
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        final MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
                                        final Bitmap[] bitmap = new Bitmap[1];
                                        final String[] name = new String[1];
                                        final String[] id = new String[1];
                                        try{
                                            final JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                            Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                            for(int i = 0; i < rawName.length(); i++) {
                                                try {
                                                    name[0] = rawName.getJSONObject(i).getString("name");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    id[0] = rawName.getJSONObject(i).getString("id");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                final String finalName = name[0];
                                                finalId = id[0];
                                                bitmap[0] = getFriendBitmap(finalId);
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap[0].compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                                byte[] bytes = baos.toByteArray();

                                                mySQLDatabase.insertData(finalName, bytes, finalId, MySQLDatabase.FACEBOKK_FRIENDS_DATA_TABLE);
                                                bitmapArrayList.add(bitmap[0]);
                                            }

                                                    synchronized (this)
                                                    {
                                                        this.notify();
                                                    }
                                                }
                                            };
                                                synchronized (runnable) {
                                                    Thread thread = new Thread(runnable);
                                                    thread.start();
                                                    try {
                                                        runnable.wait();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                Log.d(TAG, "facebook:onSuccess:" + loginResult);


                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        ).executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });






        GetScreen_Width_Height();
        initList();

        final Spinner spinnerCountries = findViewById(R.id.spinner_countries);
//        spinnerCountries.setLayoutParams(new FrameLayout.LayoutParams((screen_Width * 2)/3, ViewGroup.LayoutParams.WRAP_CONTENT));
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
//        countryFlag.setImageResource(R.drawable.afghnistan);
        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                CountryItem clickedItem = (CountryItem) adapterView.getItemAtPosition(position);
                String clickedCountryName = clickedItem.getCountryName();
                countryName.setText(clickedCountryName);
                countryFlag.setImageDrawable(getResources().getDrawable(clickedItem.getFlagImage()));





//                Toast.makeText(getApplicationContext(),clickedCountryName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerCountries.setEnabled(false);
        selectYourCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCountryClick = true;
                spinnerCountries.performClick();
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkCountryClick){
                    TastyToast.makeText(getApplicationContext(),"Please select your country!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                    return;
                }
                String name = register_name.getText().toString();
                String email = register_email.getText().toString();
                email = email+"@ludochallenge.com";
                String password = register_password.getText().toString();

                RegisterAccount(name, email, password);
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.goup, R.anim.godown);
                finish();
            }
        });
        playAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
                mySQLDatabase.setCurrentSession("PLAY_AS_GUEST", MySQLDatabase.LOGIN_STATUS_PLAY_AS_GUEST);
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.goup, R.anim.godown);
                finish();
            }
        });

    }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         // Pass the activity result back to the Facebook SDK
         mCallbackManager.onActivityResult(requestCode, resultCode, data);
     }


     @Override
     public void onStart() {
         super.onStart();
         // Check if user is signed in (non-null) and update UI accordingly.
//         if(currentUser != null){
//             String name = currentUser.getDisplayName();
//             String email = currentUser.getEmail();
//             Uri photoUrl= currentUser.getPhotoUrl();
//             Toast.makeText(this, "Name: "+ name + "email: "+ email, Toast.LENGTH_SHORT).show();
//             updateUI();
//         }
     }

     private void updateUI() {
         final Intent intent = new Intent(getApplicationContext(), MainMenu.class);
         final Bitmap country_flag_bitmap ;
         BitmapDrawable bitmapDrawable = (BitmapDrawable) countryFlag.getDrawable();
         country_flag_bitmap = bitmapDrawable.getBitmap();
         ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
         country_flag_bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream1);
         final byte[] byteArray1 = stream1.toByteArray();
         bitmapArrayList.add(country_flag_bitmap);


//         facebook_uid = currentUser.getUid();

         final Bitmap[] bitmap = {BitmapFactory.decodeResource(getResources(), R.drawable.default_pic)};
         bitmapArrayList.add(bitmap[0]);

         Thread thread = new Thread(new Runnable() {
             @Override
             public void run() {
                 bitmap[0] = get_imageFrom_Uri(photoUrl);
                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 bitmap[0].compress(Bitmap.CompressFormat.PNG, 50, stream);
                 byte[] byteArray = stream.toByteArray();
                 MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
                 mySQLDatabase.insertData(name, byteArray, byteArray1, countryName.getText().toString(), email, MySQLDatabase.FACEBOOK_USER_TABLE);

                 mySQLDatabase.insertGameProgressData(email,MySQLDatabase.LUDO_CHALLENGE, MySQLDatabase.VS_COMPUTER, "0","0", "500");
                 mySQLDatabase.insertGameProgressData(email,MySQLDatabase.LUDO_CHALLENGE, MySQLDatabase.VS_MULTIPLAYTER, "0","0", "500");
                 mySQLDatabase.insertGameProgressData(email,MySQLDatabase.SNAKES_AND_LADDERS, MySQLDatabase.VS_COMPUTER, "0","0", "500");
                 mySQLDatabase.insertGameProgressData(email,MySQLDatabase.SNAKES_AND_LADDERS, MySQLDatabase.VS_MULTIPLAYTER, "0","0", "500");
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
                 overridePendingTransition(R.anim.goup, R.anim.godown);
                 loadingBar.dismiss();
                 finish();

             }
         });
         thread.start();
     }

     private void handleFacebookAccessToken(AccessToken token) {
         Log.d(TAG, "handleFacebookAccessToken:" + token);

         AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
         mAuth.signInWithCredential(credential)
                 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {
                             // Sign in success, update UI with the signed-in user's information
                              name = task.getResult().getUser().getDisplayName();
                              email =task.getResult().getUser().getEmail();
                              photoUrl = task.getResult().getUser().getPhotoUrl();
                              MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
                              mySQLDatabase.setCurrentSession(email, MySQLDatabase.LOGIN_STATUS_FACEBOOK);
                              Log.d(TAG, "signInWithCredential:success");
                             updateUI();
                             facebookLogin.setEnabled(true);
                         } else {
                             TastyToast.makeText(getApplicationContext(),"Authentication Failed!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                             loadingBar.dismiss();
                         }

                     }
                 });
     }


     @Override
     public void startActivityForResult(Intent intent, int requestCode) {
         super.startActivityForResult(intent, requestCode);
     }

     private void RegisterAccount(final String name, String email, String password) {

       if(TextUtils.isEmpty(name)){
           TastyToast.makeText(getApplicationContext(),"Please enter your name!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        }
        else if(TextUtils.isEmpty(email)){
           TastyToast.makeText(getApplicationContext(),"Please enter your username!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        }
        else if(TextUtils.isEmpty(password)){
           TastyToast.makeText(getApplicationContext(),"Please enter your password!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        }
        else{
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        currentUser = mAuth.getCurrentUser();
                        sqlDatabase.setCurrentSession(currentUser.getUid(), MySQLDatabase.LOGIN_STATUS_LUDOCHALLENGE);
                        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = current_user.getUid();
                        current_uid = current_user.getUid();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("image", "default");
                        userMap.put("thumb_image","default");
                        userMap.put("flag", String.valueOf(countryFlag));
                        userMap.put("country",countryName.getText().toString());

                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mTokenDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
                                    final String current_userId = mAuth.getCurrentUser().getUid();
                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    mTokenDatabase.child(current_userId).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            storeFlag_into_Storage();
                                            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                                            intent.putExtra("country_name", countryName.getText().toString());
                                            intent.putExtra("userName_register",name.toString());
                                            intent.putExtra("check",1);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                            MySQLDatabase mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
                                            mySQLDatabase.insertGameProgressData(current_userId,MySQLDatabase.LUDO_CHALLENGE, MySQLDatabase.VS_COMPUTER, "0","0", "500");
                                            mySQLDatabase.insertGameProgressData(current_userId,MySQLDatabase.LUDO_CHALLENGE, MySQLDatabase.VS_MULTIPLAYTER, "0","0", "500");
                                            mySQLDatabase.insertGameProgressData(current_userId,MySQLDatabase.SNAKES_AND_LADDERS, MySQLDatabase.VS_COMPUTER, "0","0", "500");
                                            mySQLDatabase.insertGameProgressData(current_userId,MySQLDatabase.SNAKES_AND_LADDERS, MySQLDatabase.VS_MULTIPLAYTER, "0","0", "500");


                                            startActivity(intent);
                                            overridePendingTransition(R.anim.goup, R.anim.godown);
                                            finish();
                                        }
                                    });
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }
                    else{
                        TastyToast.makeText(getApplicationContext(),"Error Occured Try Again!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
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


     void GetScreen_Width_Height() {
         DisplayMetrics displayMetrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         screen_Height = displayMetrics.heightPixels;
         screen_Width = displayMetrics.widthPixels;

     }

     void storeFlag_into_Storage() {

         Uri imageUri = Uri.parse("android.resource://com.example.apple.ludoking/" + getResources().getIdentifier(countryName.getText().toString().toLowerCase(), "drawable", getApplicationContext().getPackageName()));
         CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
//         CropImage.ActivityResult result = CropImage.
         final File thumb_filePath = new File(imageUri.getPath());
         current_uid = mCurrentUser.getUid();
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
                             String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                             if (task.isSuccessful()) {
                                 Map updateHashMap = new HashMap<>();
                                 updateHashMap.put("flag_image", download_url);
                                 updateHashMap.put("thumb_flag_image", thumb_downloadUrl);
                                 mDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()) {
                                             TastyToast.makeText(getApplicationContext(),"country uploaded successfully!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                         }
                                         else{
                                             TastyToast.makeText(getApplicationContext(),"error uploading your country!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                                         }
                                     }
                                 });
                             } else {
                                 TastyToast.makeText(getApplicationContext(),"Error uploading profile pic!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                             }
                         }
                     });
                 } else {
                     TastyToast.makeText(getApplicationContext(),"Error uploading profile pic!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                 }
             }
         });
     }

    public Bitmap get_imageFrom_Uri(Uri uri) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.default_pic);
        bitmapArrayList.add(bm);
        String imageURL = null;
        try {
//            imageURL = new URL(uri.toString() + "?type=large");
            imageURL = uri.toString() + "?type=large";
            bm = Picasso.get().load(imageURL).get();
//            bm = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }


     public Bitmap getFriendBitmap(String userID)
     {

         String imageURL;
         Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_pic);
         bitmapArrayList.add(bitmap);
         imageURL = "http://graph.facebook.com/"+userID+"/picture?type=large";
         try {
             bitmap = Picasso.get().load(imageURL).get();
         } catch (IOException e) {
             e.printStackTrace();
         }


         return bitmap;

     }

     @Override
     public void onDestroy() {
         super.onDestroy();
         unbindDrawables(findViewById(R.id.registerActivityRoot));
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
