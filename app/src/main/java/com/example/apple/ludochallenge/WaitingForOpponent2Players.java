package com.example.apple.ludochallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class WaitingForOpponent2Players extends AppCompatActivity {


    private ImageView waiting_for_opponent_gif;
    private TextView player2_name;
    private ImageView yourPic;
    private ImageView player2Pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_opponent2_players);


        waiting_for_opponent_gif = (ImageView) findViewById(R.id.waiting_for_opponent_gif_2players);
        player2_name = (TextView) findViewById(R.id.waitingForOpponent_player2_name_2players);
        yourPic = (ImageView) findViewById(R.id.waitingForOpponent_yourPic_2players);
        player2Pic = (ImageView) findViewById(R.id.waitingForOpponent_player2Pic_2players);

        Glide.with(getApplicationContext()).load(R.raw.random_playergif).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(waiting_for_opponent_gif);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.waitingForOpponent_2players));
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
