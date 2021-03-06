package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Taha Malik on 4/18/2018.
 **/

public class LudoGame extends FrameLayout {

    private final AnimationDrawable[] animationDrawables;
    int mBoxWidth;
    int pieceSize;
    float scaleIncrease = 1.3f;
    boolean gameStarted = false;
    ArrayList<LudoBox> boxes = new ArrayList<>();
    boolean firstupdate = true;
    int updateNum = 3;
    LudoBox[] oneP;
    LudoBox[] twoP;
    LudoBox[] threeP;
    LudoBox[] fourP;

    static boolean turnChange = true;
    public ArrayList<TrackingListeners> listeners = new ArrayList<>();

    Context context;
    public int currentPlayer = 0;
    public DatabaseReference gameRef = null;
    int numberOfPlayers;
    int pieceIndex = -1;
    int num = 1;
    int boardStartX;

    ImageView[] dicePoints;
    ImageView[] mArrows;
    ArrayList<LudoPlayer> players = new ArrayList<>();
    ArrayList<LudoPiece> pieces = new ArrayList<>();
    public String[] uids = null;
    int width;
    LudoGame game = this;
    int boardStart;
    public static TranslateAnimation translateAnimation;
    public static AlphaAnimation alphaAnimation;
    public boolean done = false;
    ConstraintLayout baseLayout;
    private int validPieces = 0;

    public LudoGame(@NonNull Context context, float width, int y, float x, Color[] colors, int numberOfPlayers, ImageView[] dicePoints, ImageView[] arrows, PlayerType[] playerTypes) {

        super(context);
        mBoxWidth = (int) (width / 15);
        this.context = context;
//        initializeGif();
        this.width = (int) width;
        baseLayout = ((Activity)context).findViewById(R.id.ludoBaseLayout);
//        baseLayout.setWillNotDraw(true);
        this.numberOfPlayers = numberOfPlayers;
        mArrows = arrows;
        this.dicePoints = dicePoints;
        boardStart = y;
        boardStartX = (int) x;
        pieceSize = mBoxWidth;
        animationDrawables = new AnimationDrawable[6];

        for(int i = 0; i < 6; i++)
        {
            animationDrawables[i] = new AnimationDrawable();
            animationDrawables[i] = (AnimationDrawable) getResources().getDrawable(getResources().getIdentifier("dice_roll" + (i+1),"drawable",context.getPackageName()));
            animationDrawables[i].start();
        }
        setY(y);
        initializeBox(y, (int)x, (int)width);
//        setWillNotDraw(true);
        initializePieces(numberOfPlayers, colors, playerTypes);

//        diceImage = new ImageView(context);
//        diceImage.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));
//        diceImage.setX(dicePoints[currentPlayer].x);
//        diceImage.setY(dicePoints[currentPlayer].y);
        setVisibility(VISIBLE);
        setLayoutParams(new LayoutParams((int)width, (int)width));
//        diceImage.setImageDrawable(getResources().getDrawable(R.drawable.dice_2));
//        diceImage.setOnClickListener(getDiceClickListener());
//        diceImage.setScaleType(ImageView.ScaleType.FIT_XY);
//        diceImage.setEnabled(false);
    }

//    private void initializeGif() {
//
//        diceGifBitmaps = new Bitmap[24];
//
//        for(int i = 0; i < 24; i++)
//        {
//            int id = getResources().getIdentifier("dice_gif" + i + ".png","drawable", context.getPackageName());
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
//            diceGifBitmaps[i] = bitmap;
//        }
//
//    }

    private OnClickListener pieceClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            final Handler handler = new Handler(Looper.getMainLooper());
            for(LudoPiece l : players.get(currentPlayer).getmPiece())
            {
                l.setEnabled(false);
            }

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    Runnable runnable2 = this;
                    int originalNum = num;
                    int num = game.num;
                    if (!((LudoPiece) v).open) {
                        ((LudoPiece) v).open = true;
                        num -= 5;
                    }

                    final int pieceIndex = ((LudoPiece)v).pieceNum;
                    final AnimatorSet[] animatorSets = new AnimatorSet[validPieces];
                    int duration = 1000;
                    int i = 0;
                    for (final LudoPiece l : players.get(currentPlayer).getmPiece()) {

                        if(l.isValid(originalNum)) {

                            ObjectAnimator scaleX = ObjectAnimator.ofFloat(l,"scaleX", l.getScaleX()/scaleIncrease);
                            ObjectAnimator scaleY = ObjectAnimator.ofFloat(l,"scaleY", l.getScaleY()/scaleIncrease);
                            scaleX.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
//                                    l.setEnabled(false);
                                    l.alphaAnimator.cancel();
                                    l.circleAnimator.cancel();
                                    l.setAlpha(1f);

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {

                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            ObjectAnimator scaleXTag = ObjectAnimator.ofFloat(l.getTag(),"scaleX", l.getTag().getScaleX()/scaleIncrease);
                            ObjectAnimator scaleYTag = ObjectAnimator.ofFloat(l.getTag(),"scaleY", l.getTag().getScaleY()/scaleIncrease);
                            scaleXTag.setDuration(duration);
                            scaleXTag.setDuration(duration);
                            ObjectAnimator translationY = ObjectAnimator.ofFloat(l,"translationY", l.realY);
                            translationY.setDuration(duration);
                            scaleX.setDuration(duration);
                            scaleY.setDuration(duration);
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.setInterpolator(new BounceInterpolator());
                            animatorSet.playTogether(scaleX,scaleY,translationY,scaleXTag,scaleYTag);
                            animatorSets[i++] = (animatorSet);
        //                    animatorSet.start();
        //                    l.setScaleX(l.getScaleX() / scaleIncrease);
        //                    l.setScaleY(l.getScaleY() / scaleIncrease);
        //                    l.setY(l.realY);
        //                    if (l.pieceNum != ((LudoPiece) v).pieceNum) {
        //                        l.invalidate();
        //                    }

        //                    ((ImageView) l.getTag()).setScaleX(getScaleX() / scaleIncrease);
        //                    ((ImageView) l.getTag()).setScaleY(getScaleY() / scaleIncrease);
        //                    ((ImageView) l.getTag()).setVisibility(INVISIBLE);
        //                    ((ImageView) l.getTag()).setAnimation(null);
        //                    ((ImageView) l.getTag()).invalidate();
                        }
                    }

                    Runnable runnable3 = new Runnable() {
                        @Override
                        public void run() {
                            final Runnable runnable4 = this;
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(animatorSets);
                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    synchronized (runnable4)
                                    {
                                        runnable4.notify();
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    synchronized (runnable4)
                                    {
                                        runnable4.notify();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            animatorSet.start();
                        }
                    };

                    synchronized (runnable3) {
                        handler.postAtFrontOfQueue(runnable3);
                        try {
                            runnable3.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (gameRef != null) {
                        final int finalNum = num;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final OnCompleteListener<Void> onCompleteListener2 = new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(!task.isSuccessful() || !task.isComplete())
                                        {
                                            gameRef.child("updateUI").setValue((++updateNum)%26).addOnCompleteListener(this);
                                        }
                                        else
                                        {
                                            players.get(currentPlayer).move(finalNum, (LudoPiece) v);
                                        }
                                    }
                                };
                                final OnCompleteListener<Void> onCompleteListener1 = new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful() || !task.isComplete())
                                        {
                                            gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(this);
                                        }
                                        else
                                        {
                                            gameRef.child("updateUI").setValue((++updateNum%26)).addOnCompleteListener(onCompleteListener2);

                                        }
                                    }
                                };
                                OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful() || !task.isComplete())
                                        {
                                            gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(this);

                                        }
                                        else
                                        {
                                            gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(onCompleteListener1);
                                        }

                                    }
                                };

                                gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(onCompleteListener);

                            }
                        });
                        thread.start();
                    }
                    else
                        players.get(currentPlayer).move(num, (LudoPiece)v);

                }
            };
            new Thread(runnable).start();
        }
    };



    private OnClickListener getDiceClickListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final Handler handler = new Handler(Looper.getMainLooper());
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                v.setEnabled(false);
                                mArrows[currentPlayer].setVisibility(INVISIBLE);

                                synchronized (this)
                                {
                                    this.notify();
                                }
                            }
                        };
                        synchronized (runnable) {
                            handler.post(runnable);
                            try {
                                runnable.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        int num1;

                        if (players.get(currentPlayer).type != PlayerType.ONLINE) {
                            Random random = new Random();
                            num1 = Integer.parseInt(((LudoActivity) context).getTextNum()) == 0 ? random.nextInt(6) : Integer.parseInt(((LudoActivity) context).getTextNum()) - 1;
                            num1++;
                            game.num = num1;
                        } else if (uids[currentPlayer].equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Random random = new Random();
                            num1 = Integer.parseInt(((LudoActivity) context).getTextNum()) == 0 ? random.nextInt(6) : Integer.parseInt(((LudoActivity) context).getTextNum()) - 1;
                            num1++;
                            game.num = num1;

                        } else {
                            num1 = game.num;
                        }

                        final int finalNum2 = num1 > 6 ? 6 : num1;
                        final int finalNum = num1;
//                int id = getResources().getIdentifier("dice_roll"+ finalNum2,"drawable", context.getPackageName());
                        final Runnable runnable1 = new Runnable() {
                            @Override
                            public void run() {
                                if (animationDrawables[finalNum2 - 1].isRunning()) {
                                    animationDrawables[finalNum2 - 1].stop();
                                }
                                ((ImageView) v).setImageDrawable(animationDrawables[finalNum2 - 1]);
                                animationDrawables[finalNum2 - 1].start();
                                animateDice(v);
                                synchronized (this)
                                {
                                    this.notify();
                                }
                            }
                        };
                        synchronized (runnable1) {
                            handler.post(runnable1);
                            try {
                                runnable1.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        int duration = 600;
                        for(int i = 0; i < animationDrawables[finalNum2-1].getNumberOfFrames();i++)
                        {
                            duration+=animationDrawables[finalNum-1].getDuration(i);
                        }
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (finalNum == 6)
                                    turnChange = false;
                                Runnable runnable2 = new Runnable() {
                                    @Override
                                    public void run() {
                                        if(animationDrawables[finalNum2 - 1].getCurrent() != animationDrawables[finalNum2 - 1].getFrame(animationDrawables[finalNum2-1].getNumberOfFrames() - 1)) {
                                            handler.postDelayed(this,40);
                                        }
                                        else {
                                            animationDrawables[finalNum2 - 1].stop();
                                            ((ImageView) v).setImageDrawable(getResources().getDrawable(getResources().getIdentifier("dice_" + finalNum2, "drawable", context.getPackageName())));
                                            synchronized (this) {
                                                this.notify();
                                            }
                                        }
                                    }
                                };
                                synchronized (runnable2) {
                                    handler.post(runnable2);
                                    try {
                                        runnable2.wait(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                makeMove(finalNum);
                            }
                        },duration);//animationDrawables[finalNum2-1].getNumberOfFrames() * animationDrawables[finalNum2-1].getDuration(0));
                    }
                }).start();
            }
        };
    }


    public void start()
    {
//        Handler handler = new Handler(context.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mArrows[currentPlayer].setVisibility(VISIBLE);
//                mArrows[currentPlayer].setAnimation(translateAnimation);
//                diceImage.setX(dicePoints[currentPlayer].x);
//                diceImage.setY(dicePoints[currentPlayer].y);
                for(ImageView view : dicePoints)
                {
                    view.setOnClickListener(getDiceClickListener());
//                    ((AnimationDrawable)view.getBackground()).start();
                }
                dicePoints[currentPlayer].setEnabled(true);
            }
        };
        ((Activity)context).runOnUiThread(runnable);

    }

    public void startGame() {

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {

                mArrows[currentPlayer].setVisibility(VISIBLE);
                mArrows[currentPlayer].setAnimation(translateAnimation);

//                diceImage.setX(dicePoints[currentPlayer].x);
//                diceImage.setY(dicePoints[currentPlayer].y);

                if(currentPlayer == 0)
                {
                    dicePoints[currentPlayer].setEnabled(true);
                }
                else dicePoints[currentPlayer].setEnabled(false);

                addOnDataChangeListener();
            }
        };

        ((Activity) context).runOnUiThread(runnable1);

    }

    public void addOnDataChangeListener() {

        final Query query = gameRef.child("updateUI");
        query.keepSynced(true);
        ValueEventListener valueEventListener1 = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (gameStarted) {
                            final Query query1 = gameRef;
                            query1.keepSynced(true);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    updateNum = (++updateNum)%26;
                                    if(dataSnapshot.child("dice_value").getValue() != null && dataSnapshot.child("piece_number").getValue() != null) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        assert user != null;
                                        if (!user.getUid().equals(dataSnapshot.child("turn").getValue(String.class))) {
                                            int num = ((Long) dataSnapshot.child("dice_value").getValue()).intValue();
                                            int pieceNum = ((Long) dataSnapshot.child("piece_number").getValue()).intValue();
                                            game.num = num;
                                            game.pieceIndex = pieceNum;
                                            dicePoints[currentPlayer].performClick();
                                        }
//                                        else {
//                                            diceImage.setX(dicePoints[currentPlayer].x);
//                                            diceImage.setY(dicePoints[currentPlayer].y);
//                                            mArrows[currentPlayer].setVisibility(VISIBLE);
//                                            mArrows[currentPlayer].setAnimation(translateAnimation);
//                                            diceImage.setEnabled(true);
//                                        }
                                    }
                                    else {
                                        query1.addListenerForSingleValueEvent(this);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {

//                            diceImage.setX(dicePoints[currentPlayer].x);
//                            diceImage.setY(dicePoints[currentPlayer].y);
//                            mArrows[currentPlayer].setVisibility(VISIBLE);
//                            mArrows[currentPlayer].setAnimation(translateAnimation);
//                            if(currentPlayer == 0)
//                            {
//                                diceImage.setEnabled(true);
//                            }
//                            else diceImage.setEnabled(false);
                            gameStarted = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        TrackingListeners trackingListeners = new TrackingListeners(query,valueEventListener1);
        listeners.add(trackingListeners);

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Query query1 = gameRef;
                query1.keepSynced(true);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(currentPlayer == 0) {

                            final Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean updated = true;

                                    for (int i = 0; i < numberOfPlayers && updated; i++) {
                                        updated = dataSnapshot.child(uids[i]).getValue(Boolean.class) != null && (boolean) dataSnapshot.child(uids[i]).getValue();
                                    }

                                    if (updated) {
                                        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful() || !task.isComplete()) {
                                                    gameRef.child("updated").setValue(true).addOnCompleteListener(this);
                                                } else {
                                                    Toast.makeText(context, "UPDATED EVERY PIECE", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        };

                                        gameRef.child("updated").setValue(true).addOnCompleteListener(onCompleteListener);

                                    }
                                }
                            });
                            thread.start();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        for(int i = 0; i < numberOfPlayers; i++) {
            Query query1 = gameRef.child(uids[i]);
            query1.keepSynced(true);
            query1.addValueEventListener(valueEventListener);
            listeners.add(new TrackingListeners(query1,valueEventListener));
        }
        ValueEventListener updatedValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!firstupdate) {
                            if (!(boolean) dataSnapshot.getValue()) {
//                                if (uids[currentPlayer].equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                                    ValueEventListener valueEventListener1 = new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(final DataSnapshot dataSnapshot) {
//                                            Thread thread1 = new Thread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    for (int i = 0; i < numberOfPlayers; i++) {
//                                                        final int finalI = i;
//                                                        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if (!task.isComplete() || !task.isSuccessful()) {
//                                                                    Query query1 = dataSnapshot.getRef().child(uids[finalI]);
//                                                                    query1.keepSynced(true);
//                                                                    query1.getRef().setValue(false).addOnCompleteListener(this);
//                                                                }
//                                                            }
//                                                        };
//                                                        Query query1 = dataSnapshot.getRef().child(uids[i]);
//                                                        query1.keepSynced(true);
//                                                        query1.getRef().setValue(false).addOnCompleteListener(onCompleteListener);
//                                                    }
//                                                    falsed = true;
//                                                }
//                                            });
//                                            thread1.start();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    };
//                                    Query query1 = gameRef;
//                                    query1.keepSynced(true);
//                                    query1.addListenerForSingleValueEvent(valueEventListener1);
//                            }
                            } else {
                                final Query query1 = gameRef;
                                query1.keepSynced(true);
                                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {


                                        if (currentPlayer != 0) {
                                            Toast.makeText(context, "currentPlayer not zero its " + currentPlayer, Toast.LENGTH_SHORT).show();
                                            mArrows[currentPlayer].setVisibility(VISIBLE);
                                            mArrows[currentPlayer].setAnimation(translateAnimation);

                                        } else {
                                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful() && task.isComplete()) {

                                                        final int[] i = {1};
                                                        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Query query1 = dataSnapshot.getRef().child(uids[i[0]]);
                                                                    query1.getRef().setValue(false).addOnCompleteListener(this);
                                                                } else {
                                                                    i[0]++;
                                                                    if(i[0] < numberOfPlayers) {
                                                                        Query query1 = dataSnapshot.getRef().child(uids[i[0]]);
                                                                        query1.getRef().setValue(false).addOnCompleteListener(this);
                                                                    }
                                                                    else
                                                                    {
                                                                        OnCompleteListener<Void> onCompleteListener1 = new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (!task.isComplete() || !task.isSuccessful()) {
                                                                                    Query query1 = dataSnapshot.getRef().child(uids[0]);
                                                                                    query1.getRef().setValue(false).addOnCompleteListener(this);
                                                                                } else {
                                                                                    OnCompleteListener<Void> onCompleteListener1 = new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (!task.isSuccessful() || !task.isComplete()) {
                                                                                                dataSnapshot.getRef().child("updated").setValue(false).addOnCompleteListener(this);
                                                                                            } else {
                                                                                                mArrows[currentPlayer].setVisibility(VISIBLE);
                                                                                                mArrows[currentPlayer].setAnimation(translateAnimation);
                                                                                                dicePoints[currentPlayer].setEnabled(true);
                                                                                            }
                                                                                        }
                                                                                    };
                                                                                    Query query2 = dataSnapshot.getRef().child("updated");
                                                                                    query2.keepSynced(true);
                                                                                    query2.getRef().setValue(false).addOnCompleteListener(onCompleteListener1);
                                                                                }
                                                                            }
                                                                        };

                                                                        Query query2 = dataSnapshot.getRef().child(uids[0]);
                                                                        query2.keepSynced(true);
                                                                        query2.getRef().setValue(false).addOnCompleteListener(onCompleteListener1);

                                                                    }
                                                                }
                                                            }
                                                        };
                                                        Query query1 = dataSnapshot.getRef().child(uids[i[0]]);
                                                        query1.getRef().setValue(false).addOnCompleteListener(onCompleteListener);

                                                    } else {
                                                        Query query2 = dataSnapshot.getRef().child("turn");
                                                        query2.keepSynced(true);
                                                        query2.getRef().setValue(uids[currentPlayer]).addOnCompleteListener(this);
                                                    }
                                                }
                                            };

                                            Query query2 = dataSnapshot.getRef().child("turn");
                                            query2.keepSynced(true);
                                            query2.getRef().setValue(uids[currentPlayer]).addOnCompleteListener(onCompleteListener);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else firstupdate = false;
                    }
                });
                thread.start();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        Query query1 = gameRef.child("updated");
        query1.keepSynced(true);
        query1.addValueEventListener(updatedValueListener);

        listeners.add(new TrackingListeners(query1, updatedValueListener));
    }

    private void initializePieces(int numberOfPlayers, Color[] colors, PlayerType[] playerTypes) {

        LudoBox.TransitionPlayer[] transitionPlayers = {LudoBox.TransitionPlayer.ONE, LudoBox.TransitionPlayer.TWO, LudoBox.TransitionPlayer.THREE, LudoBox.TransitionPlayer.FOUR};
        final LudoBox[][] ludoBoxes = {oneP, twoP, threeP, fourP};

        for (int i = 0; i < numberOfPlayers; i++) {
            LudoPlayer player = new LudoPlayer(game, transitionPlayers[i], null, playerTypes[i], ludoBoxes[i][0].nextBox);
            LudoPiece[] ludoPieces = new LudoPiece[4];

            for (int j = 0; j < 4; j++) {
                ImageView circle = new ImageView(context.getApplicationContext());
                circle.setImageResource(R.drawable.circle);
//                circle.setWillNotDraw(false);
                circle.setVisibility(INVISIBLE);
//                circle.setLayoutParams(new LayoutParams(4 * pieceSize / 4, 4 * pieceSize / 4));
                circle.setLayoutParams(new LayoutParams(pieceSize, pieceSize));
                final LudoPiece ludoPiece = new LudoPiece(context.getApplicationContext(), player, game, colors[i], pieceSize, ludoBoxes[i][j], circle);
                ludoPiece.pieceNum = j;
                //                ludoPiece.setTag(circle);
                pieces.add(ludoPiece);
                ludoPiece.setOnClickListener(pieceClickListener);
                final int finalJ = j;
                final int finalI = i;

//                ludoPiece.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ludoBoxes[finalI][finalJ].addPieceInitially(ludoPiece);
                            }
                        });
                        thread.start();
//                        ludoPiece.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    }
//                });
                ludoPieces[j] = ludoPiece;
                ludoPiece.setEnabled(false);
            }
            player.setmPiece(ludoPieces);
            players.add(player);
        }
    }

    private void makeMove(final int num) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final LudoPiece[] ludoPieces = players.get(currentPlayer).getmPiece();
                final int[] foundNum = {0};
                final LudoPiece[] temp = {null};
                final boolean[] sameCoordinates = {true};
                if (players.get(currentPlayer).type == PlayerType.HUMAN) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            final Runnable runnable1 = this;
                            for (final LudoPiece l : ludoPieces) {
                                if (l.isValid(num)) {
                                    foundNum[0]++;
                                    l.setEnabled(true);
                                    l.realY = l.getY();
//                                    l.setAnimation(alphaAnimation);
                                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(l,"scaleX", l.getScaleX()*scaleIncrease);
                                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(l,"scaleY", l.getScaleY()*scaleIncrease);
                                    ObjectAnimator translateY = ObjectAnimator.ofFloat(l, "translationY", l.getY() - l.getHeight()*(scaleIncrease*0.25f/1.5f));
                                    translateY.setDuration(1000);
                                    scaleX.setDuration(1000);
                                    scaleY.setDuration(1000);
                                    translateY.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            synchronized (runnable1) {
                                                runnable1.notify();
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                            synchronized (runnable1) {
                                                runnable1.notify();
                                            }
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    animatorSet.setInterpolator(new BounceInterpolator());
                                    animatorSet.playTogether(scaleX,scaleY, l.alphaAnimator, l.circleAnimator, translateY);
//                                    scaleY.addListener(new Animator.AnimatorListener() {
//                                        @Override
//                                        public void onAnimationStart(Animator animation) {
//
//                                        }
//
//                                        @Override
//                                        public void onAnimationEnd(Animator animation) {
//                                            ((ImageView) l.getTag()).setVisibility(VISIBLE);
//                                            ((ImageView) l.getTag()).setScaleX(getScaleX()*scaleIncrease);
//                                            ((ImageView) l.getTag()).setScaleY(getScaleY()*scaleIncrease);
//                                        }
//
//                                        @Override
//                                        public void onAnimationCancel(Animator animation) {
//                                            ((ImageView) l.getTag()).setVisibility(VISIBLE);
//                                            ((ImageView) l.getTag()).setScaleX(getScaleX()*scaleIncrease);
//                                            ((ImageView) l.getTag()).setScaleY(getScaleY()*scaleIncrease);
//                                        }
//
//                                        @Override
//                                        public void onAnimationRepeat(Animator animation) {
//
//                                        }
//                                    });
                                    animatorSet.start();
//                                    l.setScaleY(l.getScaleY()*scaleIncrease);
//                                    l.setScaleX(getScaleX()*scaleIncrease);
//                                    l.setY(l.getY() - l.getHeight()*(scaleIncrease*0.25f/1.5f));

//                                    startRotation((ImageView) l.getTag());

                                    if (temp[0] != null) {
                                        if (temp[0].mBox.mCenterPoint != l.mBox.mCenterPoint) {
                                            sameCoordinates[0] = false;
                                        }
                                    }
                                    temp[0] = l;
                                }
                                validPieces = foundNum[0];
                            }
                            if (foundNum[0] == 1 || (sameCoordinates[0] && temp[0] != null)) {
                                temp[0].performClick();
                            } else if (foundNum[0] == 0) {

                                android.os.Handler handler = new android.os.Handler();

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        turnChange = true;
                                        currentPlayer++;
                                        currentPlayer %= numberOfPlayers;
//                                        baseLayout.postInvalidate();
//                                        baseLayout.refreshDrawableState();
//                                        baseLayout.requestLayout();
//                                        getDiceImage().setX(dicePoints[currentPlayer].x);
//                                        getDiceImage().setY(dicePoints[currentPlayer].y);
                                        getDiceImage().setEnabled(true);
                                        getmArrows()[currentPlayer].setVisibility(VISIBLE);
                                        getmArrows()[currentPlayer].setAnimation(translateAnimation);
                                        if (players.get(currentPlayer).type == PlayerType.CPU) {
                                            getDiceImage().performClick();
                                        }
                                    }
                                }, 500);
                            }

                        }
                    };
                    synchronized (runnable) {
                        Handler handler = new Handler(Looper.getMainLooper());
//                        ((Activity) context).runOnUiThread(runnable);
                        handler.post(runnable);
                        try {
                            runnable.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (players.get(currentPlayer).type == PlayerType.CPU) {

                    final LudoPiece[] validPieces = new LudoPiece[4];

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            for (LudoPiece l : ludoPieces) {
                                if (l.isValid(num)) {
                                    validPieces[foundNum[0]] = l;
                                    foundNum[0]++;
                                    l.setEnabled(true);
                                    l.setAnimation(alphaAnimation);
                                    l.getTag().setVisibility(VISIBLE);
                                    startRotation(l.getTag());
                                    if (temp[0] != null) {
                                        if (temp[0].mBox.mCenterPoint != l.mBox.mCenterPoint) {
                                            sameCoordinates[0] = false;
                                        }
                                    }
                                    temp[0] = l;
                                }
                            }
                        }
                    };

                    synchronized (runnable)
                    {
                        ((Activity)context).runOnUiThread(runnable);
                        try {
                            runnable.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (foundNum[0] == 1 || (sameCoordinates[0] && temp[0] != null)) {
                        temp[0].performClick();
                    } else if (foundNum[0] == 0) {
                        android.os.Handler handler = new android.os.Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                turnChange = true;
                                currentPlayer++;
                                currentPlayer %= numberOfPlayers;
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

//                                        getDiceImage().setX(dicePoints[currentPlayer].x);
//                                        getDiceImage().setY(dicePoints[currentPlayer].y);
//                                        baseLayout.postInvalidate();
//                                        baseLayout.refreshDrawableState();
//                                        baseLayout.requestLayout();
                                        getDiceImage().setEnabled(true);
                                        getmArrows()[currentPlayer].setVisibility(VISIBLE);
                                        getmArrows()[currentPlayer].setAnimation(translateAnimation);
                                    }
                                });

                                if (players.get(currentPlayer).type == PlayerType.CPU) {
                                    getDiceImage().performClick();
                                }
                            }
                        }, 1000);
                    } else {
                        LudoPiece piece;
                        LudoPiece ludoPiece = validPieces[0];
                        int currentLoss = currentLoss(ludoPiece);
                        int moveLoss = moveProfit(ludoPiece);
                        piece = ludoPiece;
                        int min = moveLoss - currentLoss;
                        for(int i = 1; i < validPieces.length; i++)
                        {
                            ludoPiece = validPieces[i];
                            currentLoss = currentLoss(ludoPiece);
                            moveLoss = moveProfit(ludoPiece);
                            int max = moveLoss - currentLoss;
                            if(max > min)
                            {
                                piece = ludoPiece;
                                min = max;
                            }
                        }
                        players.get(currentPlayer).move(num, piece);
                    }
                } else if (players.get(currentPlayer).type == PlayerType.ONLINE) {

                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uids[currentPlayer])) {
                        if (pieceIndex != -1) {
                            LudoPiece piece = players.get(currentPlayer).getmPiece()[pieceIndex];
                            int sendNum = num;

                            if(!piece.open && num == 6)
                            {
                                sendNum -= 5;
                            }

                            players.get(currentPlayer).move(sendNum, piece);

                        } else {
                            android.os.Handler handler = new android.os.Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {

                                                    currentPlayer++;
                                                    currentPlayer %= numberOfPlayers;
//                                                    baseLayout.postInvalidate();
//                                                    baseLayout.requestLayout();
//                                                    baseLayout.refreshDrawableState();
//                                                    diceImage.setX(dicePoints[currentPlayer].x);
//                                                    diceImage.setY(dicePoints[currentPlayer].y);
                                                    synchronized (this)
                                                    {
                                                        this.notify();
                                                    }
                                                }
                                            };

                                            synchronized (runnable) {
                                                ((Activity) context).runOnUiThread(runnable);

                                                try {
                                                    runnable.wait();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(!task.isSuccessful() || !task.isComplete())
                                                    {
                                                        gameRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(this);
                                                    }
                                                }
                                            };
                                            gameRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(onCompleteListener);

                                            done = false;

                                        }
                                    });
                                    thread.start();
                                }
                            }, 1000);
                        }
                    }
                    else{
                        for (final LudoPiece l : ludoPieces) {
                            if (l.isValid(num)) {
                                foundNum[0]++;
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        l.setEnabled(true);
                                        l.setAnimation(alphaAnimation);
                                        synchronized (this)
                                        {
                                            this.notify();
                                        }
                                    }
                                };
                                synchronized (runnable) {
                                    ((Activity) context).runOnUiThread(runnable);
                                    synchronized (runnable)
                                    {
                                        try {
                                            runnable.wait();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                Runnable runnable1 = new Runnable() {
                                    @Override
                                    public void run() {
                                        l.getTag().setVisibility(VISIBLE);
                                        startRotation(l.getTag());
                                        synchronized (this)
                                        {
                                            this.notify();
                                        }
                                    }
                                };

                                synchronized (runnable1) {
                                    ((Activity) context).runOnUiThread(runnable1);
                                    try {
                                        runnable1.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (temp[0] != null) {
                                    if (temp[0].mBox.mCenterPoint != l.mBox.mCenterPoint) {
                                        sameCoordinates[0] = false;
                                    }
                                }
                                temp[0] = l;
                            }
                        }
                        if (foundNum[0] == 1 || (sameCoordinates[0] && temp[0] != null)) {

                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    temp[0].performClick();
                                }
                            });
                        }
                        else if(foundNum[0] == 0)
                        {

                            turnChange = true;

                            final OnCompleteListener<Void> onCompleteListener3 = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful() || !task.isComplete())
                                    {
                                        gameRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid())).setValue(true).addOnCompleteListener(this);
                                    }
                                }
                            };
                            final OnCompleteListener<Void> onCompleteListener2 = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful() || !task.isComplete())
                                    {
                                        gameRef.child("updateUI").setValue((++updateNum)%26).addOnCompleteListener(this);
                                    }else
                                    {
                                        Toast.makeText(context, "updated UI Success", Toast.LENGTH_SHORT).show();
                                        gameRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(onCompleteListener3);
                                    }
                                }
                            };
                            final OnCompleteListener<Void> onCompleteListener1 = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful() || !task.isComplete())
                                    {
                                        gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(this);
                                    }
                                    else{
                                        gameRef.child("updateUI").setValue((++updateNum)%26).addOnCompleteListener(onCompleteListener2);
                                    }
                                }
                            };
                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful() || !task.isComplete())
                                    {
                                        gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(this);
                                    }
                                    else
                                    {
                                        gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(onCompleteListener1);
                                    }
                                }
                            };

                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {

                                    done = false;
                                    currentPlayer++;
//                                    baseLayout.postInvalidate();
//                                    baseLayout.refreshDrawableState();
//                                    baseLayout.requestLayout();
                                    currentPlayer %= numberOfPlayers;
//                                    diceImage.setX(dicePoints[currentPlayer].x);
//                                    diceImage.setY(dicePoints[currentPlayer].y);

                                    synchronized (this)
                                    {
                                        this.notify();
                                    }
                                }
                            };
                            synchronized (runnable) {
                                ((Activity) context).runOnUiThread(runnable);

                                try {
                                    runnable.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(onCompleteListener);

                        }
                    }
                }
                synchronized (this)
                {
                    this.notify();
                }
            }
        };
        synchronized (runnable) {
            Thread thread = new Thread(runnable);
            thread.start();
            try {
                runnable.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public ImageView getDiceImage() {
        return dicePoints[currentPlayer];
    }

    public ArrayList<LudoPiece> getPieces() {
        return pieces;
    }

    public ImageView[] getmArrows() {
        return mArrows;
    }

    private void initializeBox(int y, int x, int width) {
        Point firstPoint = new Point(x + mBoxWidth / 2, y + width - (mBoxWidth / 2));
        firstPoint.x += 6 * mBoxWidth;
        LudoBox previousBox = null;
        LudoBox firstBox = null;

        for (int i = 0; i < 6; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);

            firstPoint.y -= mBoxWidth;

            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            } else {
                firstBox = ludoBox;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.x -= mBoxWidth;

        for (int i = 0; i < 6; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x -= mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.x += mBoxWidth;
        firstPoint.y -= mBoxWidth;

        for (int i = 0; i < 2; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y -= mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y += mBoxWidth;
        firstPoint.x += mBoxWidth;
        for (int i = 0; i < 5; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y -= mBoxWidth;
        for (int i = 0; i < 6; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y -= mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y += mBoxWidth;
        firstPoint.x += mBoxWidth;
        for (int i = 0; i < 2; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.x -= mBoxWidth;
        firstPoint.y += mBoxWidth;
        for (int i = 0; i < 5; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.x += mBoxWidth;

        for (int i = 0; i < 6; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.x -= mBoxWidth;
        firstPoint.y += mBoxWidth;

        for (int i = 0; i < 2; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }
        firstPoint.y -= mBoxWidth;
        firstPoint.x -= mBoxWidth;
        for (int i = 0; i < 5; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x -= mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.y += mBoxWidth;

        for (int i = 0; i < 6; i++) {
            LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;
            if (previousBox != null) {
                previousBox.nextBox = ludoBox;
            }
            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        firstPoint.y -= mBoxWidth;
        firstPoint.x -= mBoxWidth;

        LudoBox ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
        previousBox.nextBox = ludoBox;
        ludoBox.nextBox = firstBox;
        firstBox.previousBox = ludoBox;
        boxes.add(ludoBox);

        previousBox = boxes.get(51);
        firstPoint = new Point(boxes.get(51).mCenterPoint);
        firstPoint.y -= mBoxWidth;

        boolean transitionUpdated = false;

        for (int i = 0; i < 6; i++) {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y -= mBoxWidth;

            if (previousBox != null) {
                if (transitionUpdated)
                    previousBox.nextBox = ludoBox;
                else {
                    previousBox.transitionBox = ludoBox;
                    transitionUpdated = true;
                    previousBox.transitionPlayer = LudoBox.TransitionPlayer.ONE;
                }
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;

        previousBox = boxes.get(12);
        firstPoint = new Point(boxes.get(12).mCenterPoint);
        firstPoint.x += mBoxWidth;
        transitionUpdated = false;

        for (int i = 0; i < 6; i++) {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x += mBoxWidth;

            if (transitionUpdated)
                previousBox.nextBox = ludoBox;
            else {
                previousBox.transitionBox = ludoBox;
                transitionUpdated = true;
                previousBox.transitionPlayer = LudoBox.TransitionPlayer.TWO;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;

        firstPoint = new Point(boxes.get(25).mCenterPoint);
        firstPoint.y += mBoxWidth;
        previousBox = boxes.get(25);
        transitionUpdated = false;

        for (int i = 0; i < 6; i++) {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.y += mBoxWidth;

            if (transitionUpdated)
                previousBox.nextBox = ludoBox;
            else {
                previousBox.transitionBox = ludoBox;
                transitionUpdated = true;
                previousBox.transitionPlayer = LudoBox.TransitionPlayer.THREE;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;

        firstPoint = new Point(boxes.get(38).mCenterPoint);
        firstPoint.x -= mBoxWidth;
        previousBox = boxes.get(38);
        transitionUpdated = false;

        for (int i = 0; i < 6; i++) {
            ludoBox = new LudoBox(firstPoint, mBoxWidth, this, LudoBox.TransitionPlayer.NULL, null, null, previousBox);
            firstPoint.x -= mBoxWidth;

            if (transitionUpdated)
                previousBox.nextBox = ludoBox;
            else {
                previousBox.transitionBox = ludoBox;
                transitionUpdated = true;
                previousBox.transitionPlayer = LudoBox.TransitionPlayer.FOUR;
            }

            boxes.add(ludoBox);
            previousBox = ludoBox;
        }

        ludoBox.winBox = true;
        setStops();
        setStartingBoxes(numberOfPlayers, x);

    }

    private void setStartingBoxes(int numberOfPlayers, int x) {

        Point point = new Point(x, 0);
        point.x += 2 * mBoxWidth;
        point.y += boardStart + width - (3 * mBoxWidth);
        oneP = getFourStartingBoxes(point, boxes.get(1));

        if (numberOfPlayers == 2) {
            point.x += 9 * mBoxWidth;
            point.y -= 9 * mBoxWidth;
            twoP = getFourStartingBoxes(point, boxes.get(27));
        } else if (numberOfPlayers == 3) {
            point.x += 9 * mBoxWidth;
            twoP = getFourStartingBoxes(point, boxes.get(40));
            point.y -= 9 * mBoxWidth;
            threeP = getFourStartingBoxes(point, boxes.get(27));
        } else if (numberOfPlayers == 4) {
            point.x += 9 * mBoxWidth;
            twoP = getFourStartingBoxes(point, boxes.get(40));
            point.y -= 9 * mBoxWidth;
            threeP = getFourStartingBoxes(point, boxes.get(27));
            point.x -= 9 * mBoxWidth;
            fourP = getFourStartingBoxes(point, boxes.get(14));
        }

    }

    LudoBox[] getFourStartingBoxes(Point point, LudoBox nextBox) {
        Point first = new Point(point);
        LudoBox[] group = new LudoBox[4];
        group[0] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox, null, null);
        first.x += 2 * mBoxWidth;
        group[1] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox, null, null);
        first.x -= mBoxWidth;
        first.y -= mBoxWidth;
        group[2] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox, null, null);
        first.y += 2 * mBoxWidth;
        group[3] = new LudoBox(first, mBoxWidth, game, LudoBox.TransitionPlayer.NULL, nextBox, null, null);
        return group;
    }

    private void setStops() {
        int first = 1;
        for (int i = 0; i < 8; i++) {
            boxes.get(first).stop = true;

            if (i % 2 == 0)
                first += 8;
            else
                first += 5;
        }

    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    private void startRotation(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        animator.setDuration(200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    private int currentLoss(LudoPiece ludoPiece) {

        int profit = 0;

        if (ludoPiece.mBox.stop) {
            profit += 5;
        } else {
            profit -= 3;
            LudoBox box = ludoPiece.mBox.previousBox;

            for (int i = 0; i < 6; i++) {

                for (int j = 0; j < box.mPieceCount; j++) {
                    if (box.mPieces.get(j).color != ludoPiece.color) {
                        profit -= 10;
                    }
                }

            }
        }
        return profit;
    }

    private int moveProfit(LudoPiece ludoPiece) {
        int profit = 0;
        LudoBox toBox = ludoPiece.mBox;

        if (toBox.stop) {
            profit += 10;
        } else if(toBox.home){
            profit += 20;
        }else {
            for (int i = 0; i < num; i++) {
                toBox = toBox.nextBox;
            }
            if (toBox.mPieceCount > 0) {
                int piecesCount = toBox.mPieceCount;

                int myPieces = 1;
                int opponent = 0;
                Color color = null;
                int redPieces = 0;
                int bluePieces = 0;
                int greenPieces = 0;
                int yellowPieces = 0;


                for (int i = toBox.mPieceCount; i > 0; i++) {
                    if (toBox.mPieces.get(i).player.getPlayer() == players.get(currentPlayer).getPlayer()) {
                        myPieces++;
                    } else if (color == null || toBox.mPieces.get(i).color == color) {
                        color = toBox.mPieces.get(i).color;
                        opponent++;
                    } else {
                        break;
                    }
                }

                if (myPieces == opponent) {
                    profit+= 12;
                }

            }
            else
            {
                profit -= 3;
                LudoBox box = ludoPiece.mBox.previousBox;

                for (int i = 0; i < 6; i++) {

                    for (int j = 0; j < box.mPieceCount; j++) {
                        if (box.mPieces.get(j).color != ludoPiece.color) {
                            profit -= 10;
                        }
                    }
                }
            }
        }
        return profit;
    }

    private void animateDice(final View view)
    {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view,"scaleX",2.5f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view,"scaleY",2.5f);
        final ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view,"scaleX",1f);
        final ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view,"scaleY",1f);

        scaleUpX.setDuration(200);
        scaleUpY.setDuration(200);
        scaleDownX.setDuration(400);
        scaleDownY.setDuration(400);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX,scaleUpY);
        scaleUp.setInterpolator(new FastOutSlowInInterpolator());
        scaleUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.playTogether(scaleDownX,scaleDownY);
                scaleDown.setInterpolator(new BounceInterpolator());
                scaleDown.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleUp.start();

    }

}
