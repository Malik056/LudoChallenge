package com.example.apple.ludochallenge.Networking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apple.ludochallenge.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private ImageView logo;
    private CustomEditText login_username;
    private CustomEditText login_password;
    private Button loginBtn;
    private FirebaseAuth mAUth;
    private ProgressDialog loadingBar;
    private ImageView createOne;
    private ImageView playAsGuest;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAUth = FirebaseAuth.getInstance();
        logo = (ImageView) findViewById(R.id.register_logo);
        Glide.with(getApplicationContext()).load(R.raw.logo).into(logo);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        login_username = (CustomEditText) findViewById(R.id.login_username);
        login_password = (CustomEditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_login_button);
        createOne = (ImageView) findViewById(R.id.login_dont_have_an_account_createOne);
        playAsGuest = (ImageView) findViewById(R.id.loginActivity_playAsGuest);

        loadingBar = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_username.getText().toString();
                email = email+"@ludochallenge.com";
                String password = login_password.getText().toString();
                LoginUserAccount(email, password);
            }
        });

        createOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        playAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void LoginUserAccount(String email, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please write your username!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please write your password!", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait, while we are logging into your account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAUth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String current_userId = mAUth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        mUserDatabase.child(current_userId).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(LoginActivity.this, MainMenu.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(), "Error Occured, Try Again", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }
}
