package com.example.apple.ludochallenge.networking;

import android.graphics.Bitmap;

public class FacebookUser {

    private String ID;
    private String name;
    private Bitmap image;

    public FacebookUser(String ID, String name, Bitmap image) {
        this.ID = ID;
        this.name = name;
        this.image = image;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }
}
