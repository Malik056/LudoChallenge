package com.example.apple.ludochallenge;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.ludochallenge.networking.FacebookUser;

import java.util.ArrayList;

public class FacebookFriendAdapter extends RecyclerView.Adapter {

    ArrayList<FacebookUser> facebookUsers;

    public FacebookFriendAdapter(ArrayList<FacebookUser> facebookUsers) {
        this.facebookUsers = facebookUsers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

        return new FacebookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        FacebookViewHolder viewHolder = (FacebookViewHolder) holder;
        TextView textView = viewHolder.view.findViewById(R.id.users_name);
        ImageView imageView = viewHolder.view.findViewById(R.id.user_singleImage);
        ImageView flag = viewHolder.view.findViewById(R.id.user_single_flag);

        textView.setText(facebookUsers.get(position).getName());
        imageView.setImageBitmap(facebookUsers.get(position).getImage());
        flag.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return facebookUsers.size();
    }

    public class FacebookViewHolder extends RecyclerView.ViewHolder{

        View view;

        public FacebookViewHolder(View view) {

            super(view);
            this.view = view;
        }


    }
}
