package com.example.apple.ludochallenge;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Taha Malik on 5/5/2018.
 **/
public class TrackingListeners {

    Query reference;
    ValueEventListener listener;

    public TrackingListeners(Query reference, ValueEventListener listener) {
        this.reference = reference;
        this.listener = listener;
    }
}
