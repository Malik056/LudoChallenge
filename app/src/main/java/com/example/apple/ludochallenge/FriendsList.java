package com.example.apple.ludochallenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class FriendsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");

        JSONArray friendList;
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> friends_ids = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(jsondata);
            friendList = jsonArray;

            if(friendList!= null && friendList.length() > 0) {
                for (int i = 0; i < friendList.length(); i++) {
                    friends.add(friendList.getJSONObject(i).getString("name"));
                    friends_ids.add(friendList.getJSONObject(i).getString("id"));
                }

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, friends);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public Bitmap getFriendBitmap(int userID)
    {

        String imageURL;

        Bitmap bitmap = null;
        imageURL = "http://graph.facebook.com/"+userID+"/picture?type=large";
        InputStream in = null;
        try {
            in = (InputStream) new URL(imageURL).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
    }

}
