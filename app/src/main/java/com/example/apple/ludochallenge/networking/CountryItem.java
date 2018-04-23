package com.example.apple.ludochallenge.networking;

/**
 * Created by Apple on 25/03/2018.
 */

public class CountryItem {
    private String mCountryName;
    private int mFlagImage;

    public CountryItem(String countryName, int flagImage){
        mCountryName = countryName;
        mFlagImage = flagImage;
    }

    public String getCountryName(){
        return mCountryName;
    }
    public int getFlagImage(){
        return mFlagImage;
    }

}
