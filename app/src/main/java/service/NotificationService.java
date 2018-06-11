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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.apple.ludochallenge.LudoGame;
import com.example.apple.ludochallenge.R;
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
    Context context;
    private int notificationId;

    public NotificationService(final Context context) {

        this.context = context;
        notificationId = 1;
        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myNotificationRef = mDatabase.getReference().child("notifications").child(mUser.getUid());
        myNotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if((Boolean) dataSnapshot.child("challenge").getValue())
                {
                    String challenger = (String) dataSnapshot.child("from").getValue();
                    int numberofPlayers = (int) dataSnapshot.child("noOfPlayers").getValue();

                    String challengeType = "One on One with him";

                    String[] userids = new String[0];

                    if(numberofPlayers == 2)
                    {
                        userids = new String[]{
                                mUser.getUid(), challenger
                        };
                    }
                    else if(numberofPlayers == 3)
                    {
                        challengeType = "3 player game";
                        userids = new String[]{
                                mUser.getUid(), challenger,
                                String.valueOf(dataSnapshot.child("player3"))
                        };

                    }
                    else if(numberofPlayers == 4)
                    {
                        challengeType = "4 player game";

                        userids = new String[]{
                                mUser.getUid(), challenger,
                                String.valueOf(dataSnapshot.child("player3")),
                                String.valueOf(dataSnapshot.child("player4"))
                        };

                    }

                    final String[] names = new String[numberofPlayers];
                    final ArrayList<byte[]> bytes = new ArrayList<>();

                    for(int i = 0; i < numberofPlayers; i++)
                    {
                        final int finalI = i;
                        mDatabase.getReference().child("Users").child(userids[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
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

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    Intent intent = new Intent(context, LudoGame.class);
                    intent.putExtra("uids", userids);
                    intent.putExtra("names", names);
                    intent.putExtra("pics", bytes);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),getResources().getString(R.string.CHANNEL_ID))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(challenger + " challenged you")
                            .setContentText(challenger + " challenged you to play " + challengeType)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    createNotificationChannel();

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(notificationId, builder.build());
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
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
