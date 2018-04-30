package com.example.apple.ludochallenge.networking;

public class Users {

    public String name;
    public String image;
    public String thumb_image;
    public String flag_image;
    public String ID;

    public Users(){

    }
    public Users(String name, String image, String thumb_image, String flag_image) {
        this.name = name;
        this.image = image;
        this.flag_image = flag_image;
        this.thumb_image = thumb_image;
    }

    public String getFlag_image() {
        return flag_image;
    }

    public void setFlag_image(String flag_image) {
        this.flag_image = flag_image;
    }

    public String getName() {

        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
