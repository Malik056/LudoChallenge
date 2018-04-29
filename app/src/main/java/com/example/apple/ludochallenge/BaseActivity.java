package com.example.apple.ludochallenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.apple.ludochallenge.networking.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.Set;

public class BaseActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Intent intent = getIntent();
                String activityName = intent.getStringExtra("activity");

                if (activityName == null) {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                } else {
                    try {
                        Class<?> c = Class.forName(activityName);

                        Bundle bundle = intent.getExtras();
                        Intent intent1 = new Intent(getApplicationContext(), c);

                        for(String s : Objects.requireNonNull(bundle).keySet())
                        {
                            intent1.putExtra(s, bundle.getString(s) == null ? bundle.getInt(s) : bundle.getString(s));
                        }

                        startActivity(intent1);

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        thread.start();

    }
}
