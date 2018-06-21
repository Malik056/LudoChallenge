package service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.apple.ludochallenge.R;
import com.example.apple.ludochallenge.WaitingForOpponent2Players;
import com.example.apple.ludochallenge.WaitingForOpponent3Players;
import com.example.apple.ludochallenge.WaitingForOpponent4Players;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class NotificationService extends Service {

    FirebaseAuth firebaseAuth;
    FirebaseUser mUser;
    FirebaseDatabase mDatabase;
    DatabaseReference myNotificationRef;
    private int notificationId;

    public NotificationService() {

        notificationId = 1;
        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myNotificationRef = mDatabase.getReference().child("notifications").child(mUser.getUid());
        final Context context = this;
        myNotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if((Boolean) dataSnapshot.child("challenge").getValue())
                {
                    final String challenger = (String) dataSnapshot.child("from").getValue();
                    final int numberofPlayers =  ((Long)dataSnapshot.child("noOfPlayers").getValue()).intValue();

                    String challengeType = "One on One with him";
                    String[] userids = new String[0];
                    Intent intent = null;

                    if(numberofPlayers == 2)
                    {
                        intent = new Intent(context, WaitingForOpponent2Players.class);
                        userids = new String[]{
                                mUser.getUid(), challenger
                        };
                    }
                    else if(numberofPlayers == 3)
                    {
                        intent = new Intent(context, WaitingForOpponent3Players.class);
                        challengeType = "3 player game";
                        userids = new String[]{
                                mUser.getUid(), challenger,
                                String.valueOf(dataSnapshot.child("player3"))
                        };

                    }

                    else if(numberofPlayers == 4)
                    {
                        intent = new Intent(context, WaitingForOpponent4Players.class);
                        challengeType = "4 player game";
                        userids = new String[]{
                                mUser.getUid(), challenger,
                                String.valueOf(dataSnapshot.child("player3")),
                                String.valueOf(dataSnapshot.child("player4"))
                        };

                    }

                    final String[] names = new String[numberofPlayers];
                    final ArrayList<byte[]> bytes = new ArrayList<>();

                    final Intent finalIntent = intent;
                    final String[] finalUserids = userids;
                    final String finalChallengeType = challengeType;
                    for(int i = 0; i < numberofPlayers; i++)
                    {
                        final int finalI = i;

                        mDatabase.getReference().child("Users").child(finalUserids[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        names[finalI] = (String) dataSnapshot.child("name").getValue();
                                        String url = (String) dataSnapshot.child("image").getValue();
                                        URL url1;
                                        try {
                                            url1 = new URL(url);
                                            Bitmap bmp = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            bmp.recycle();
                                            bytes.add(byteArray);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        finalIntent.putExtra("uids", finalUserids);
                                        finalIntent.putExtra("names", names);
                                        finalIntent.putExtra("pics", bytes);
                                        finalIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, finalIntent, 0);
                                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),getResources().getString(R.string.CHANNEL_ID))
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle(challenger + " challenged you")
                                                .setContentText(challenger + " challenged you to play " + finalChallengeType)
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setContentIntent(pendingIntent)
                                                .setTimeoutAfter(20000)
                                                .setAutoCancel(true);

                                        createNotificationChannel();
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                                // notificationId is a unique int for each notification that you must define
                                                notificationManager.notify(notificationId, builder.build());
                                            }
                                        });
                                    }
                                });
                                thread.start();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
