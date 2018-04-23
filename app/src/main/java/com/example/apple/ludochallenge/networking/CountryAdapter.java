package com.example.apple.ludochallenge.networking;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.ludochallenge.R;

import java.util.ArrayList;

/**
 * Created by Apple on 25/03/2018.
 */

public class CountryAdapter extends ArrayAdapter<CountryItem> {
    private int screen_Height;
    private int screen_Width;
    public CountryAdapter(Context context, ArrayList<CountryItem> countryList){
        super(context, 0, countryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.country_spinner_row, parent, false
            );
            FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.drop_down_framLayout);
//            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(screen_Width/3,));
//            frameLayout.setBackground(getContext().getResources().getDrawable(R.drawable.drop_down_menu_background));
        }
        ImageView imageViewFlag = convertView.findViewById(R.id.image_view_flag);
        TextView textViewName = convertView.findViewById(R.id.text_view_name);

        CountryItem currentItem = getItem(position);
        if(currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getFlagImage());
            textViewName.setText(currentItem.getCountryName());
        }

        return convertView;
    }
    void GetScreen_Width_Height() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_Height = displayMetrics.heightPixels;
        screen_Width = displayMetrics.widthPixels;

    }
}
