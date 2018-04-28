package com.example.apple.ludochallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class SpinWheelActivity extends AppCompatActivity {

    private ImageView spin_wheel;
    private Button spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);

        spin_wheel = (ImageView) findViewById(R.id.spin_wheel);
        spin = (Button) findViewById(R.id.spin_button);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Random rand = new Random();

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long  n = rand.nextInt(1620) + 1080;
                final RotateAnimation rotate = new RotateAnimation(0, n, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(4000);
                rotate.setInterpolator(new LinearInterpolator());
                spin_wheel.startAnimation(rotate);
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        spin_wheel.setRotation(n);
                        Toast.makeText(getApplicationContext(),spin_wheel.getRotation()+"",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });






//        spin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                long  n = rand.nextInt(10000) + 4000;
//                animation.setDuration(n);
//                animation.setInterpolator(new BounceInterpolator());
//                spin_wheel.startAnimation(animation);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//            }
//        });
    }
}
