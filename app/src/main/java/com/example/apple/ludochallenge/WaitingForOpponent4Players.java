package com.example.apple.ludochallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class WaitingForOpponent4Players extends AppCompatActivity {


    private ImageView waiting_for_opponent_gif;
    private TextView player2_name;
    private TextView player3_name;
    private TextView player4_name;
    private ImageView yourPic;
    private ImageView player2Pic;
    private ImageView player3Pic;
    private ImageView player4Pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_opponent);

         waiting_for_opponent_gif = (ImageView) findViewById(R.id.waiting_for_opponent_gif);
         player2_name = (TextView) findViewById(R.id.waitingForOpponent_player2_name);
         player3_name = (TextView) findViewById(R.id.waitingForOpponent_player3_name);
         player4_name = (TextView) findViewById(R.id.waitingForOpponent_player4_name);
         yourPic = (ImageView) findViewById(R.id.waitingForOpponent_yourPic);
         player2Pic = (ImageView) findViewById(R.id.waitingForOpponent_player2Pic);
         player3Pic = (ImageView) findViewById(R.id.waitingForOpponent_player3Pic);
         player4Pic = (ImageView) findViewById(R.id.waitingForOpponent_player4Pic);

        Glide.with(getApplicationContext()).load(R.raw.random_playergif).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(waiting_for_opponent_gif);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.waitingForOpponent));
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
