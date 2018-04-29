package com.example.apple.ludochallenge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.apple.ludochallenge.networking.MainMenu;
import com.example.apple.ludochallenge.networking.MySQLDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class SpinWheelActivity extends AppCompatActivity {

    private ImageView spin_wheel;
    private Button spin;
    String textResult = "";
    private FrameLayout spin_win_dialog;
    private RelativeLayout spin_root;
    private ImageView dialog_coins_text;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);


        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        spin_root = (RelativeLayout) findViewById(R.id.spin_root);
        spin_win_dialog = (FrameLayout) findViewById(R.id.spin_win_dialog);
        spin_wheel = (ImageView) findViewById(R.id.spin_wheel);
        spin = (Button) findViewById(R.id.spin_button);
        dialog_coins_text = (ImageView) findViewById(R.id.dialog_coins_text);
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

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        spin_wheel.setRotation(n);
                        long result = n;
                        while (true){
                            if(result <=360){
                                break;
                            }
                            result = result -360;
                        }
                        getWinCoins(result);
                        setWinCoinText(textResult);

                        spin_win_dialog.setVisibility(View.VISIBLE);
//                        Toast.makeText(getApplicationContext(),textResult,Toast.LENGTH_LONG).show();
                        spin_root.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
//                                MySQLDatabase mySQLDatabase;
//                                ArrayList<String> list = new ArrayList<String>();
                                spin_win_dialog.setVisibility(View.GONE);
                                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                mySQLDatabase = MySQLDatabase.getInstance(getApplicationContext());
//                                String coins = mySQLDatabase.getUserProgressData(mySQLDatabase.fetchCurrentLoggedInID()).get(0).getCoins();

//                                mySQLDatabase.insertGameProgressData(mySQLDatabase.fetchCurrentLoggedInID(), MySQLDatabase.LUDO_CHALLENGE, MySQLDatabase.VS_COMPUTER, MySQLDatabase.WINS_COL, MySQLDatabase.LOSES_COL, MySQLDatabase.COINS_COL + textResult);
                                startActivity(intent);
                                overridePendingTransition(R.anim.goup, R.anim.godown);
                                finish();

                                return true;
                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

    }


    private void getWinCoins(long result){

        if(result >= 0 && result <=45){
            textResult = "200";
        }
        else if(result > 45 && result <=90){
            textResult = "150";
        }
        else if(result > 90 && result <=135){
            textResult = "127";
        }
        else if(result > 135 && result <= 180){
            textResult = "100";
        }
        else if(result > 180 && result <= 225){
            textResult = "75";
        }
        else if(result > 225 && result < 270){
            textResult = "50";
        }
        else if(result > 270 && result < 315){
            textResult = "25";
        }
        else if(result > 315 && result <= 360){
            textResult = "10";
        }
    }

    private void setWinCoinText(String text){
        if(text.equals("200")){
            dialog_coins_text.setImageResource(R.drawable.two_hundred);
        }
        else if(text.equals("150")){
            dialog_coins_text.setImageResource(R.drawable.one_fifty);
        }
        else if(text.equals("127")){
            dialog_coins_text.setImageResource(R.drawable.one_twenty_seven);
        }
        else if(text.equals("100")){
            dialog_coins_text.setImageResource(R.drawable.hundred);
        }
        else if(text.equals("75")){
            dialog_coins_text.setImageResource(R.drawable.seventy_five);
        }
        else if(text.equals("50")){
            dialog_coins_text.setImageResource(R.drawable.fifty);
        }
        else if(text.equals("25")){
            dialog_coins_text.setImageResource(R.drawable.twenty_five);
        }
        else if(text.equals("10")){
            dialog_coins_text.setImageResource(R.drawable.ten);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.spin_root));
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
