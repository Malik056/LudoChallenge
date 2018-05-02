package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Taha Malik on 4/18/2018.
 **/

public class LudoGame extends FrameLayout {


    int mBoxWidth;
    int pieceSize;
    boolean gameStarted = false;
    ArrayList<LudoBox> boxes = new ArrayList<>();
    boolean firstupdate = true;
    int updateNum = 0;
    LudoBox[] oneP;
    LudoBox[] twoP;
    LudoBox[] threeP;
    LudoBox[] fourP;

    static boolean turnChange = true;

    Context context;
    public int currentPlayer = 0;
    public DatabaseReference gameRef = null;
    int numberOfPlayers;
    int pieceIndex = 0;
    int num = 1;
    Point[] dicePoints;
    ImageView[] mArrows;
    ArrayList<LudoPlayer> players = new ArrayList<>();
    ArrayList<LudoPiece> pieces = new ArrayList<>();
    public String[] uids = null;

    public static TextView textView;
    ImageView diceImage;
    int width;
    LudoGame game = this;
    int boardStart;
    public static TranslateAnimation translateAnimation;
    public static AlphaAnimation alphaAnimation;


    public LudoGame(@NonNull Context context, int width, int y, Color[] colors, int numberOfPlayers, Point[] dicePoints, ImageView[] arrows, PlayerType[] playerTypes) {

        super(context);
        mBoxWidth = width / 15;
        this.context = context;
        this.width = width;
        this.numberOfPlayers = numberOfPlayers;
        mArrows = arrows;
        this.dicePoints = dicePoints;
        boardStart = y;
        pieceSize = mBoxWidth / 4;
        setY(y);
        initializeBox(y, width);
        setWillNotDraw(false);
        initializePieces(numberOfPlayers, colors, playerTypes);

        diceImage = new ImageView(context);
        diceImage.setLayoutParams(new LinearLayout.LayoutParams(width / 10, width / 10));
//        diceImage.setX(dicePoints[currentPlayer].x);
//        diceImage.setY(dicePoints[currentPlayer].y);
        setVisibility(VISIBLE);
        setLayoutParams(new LayoutParams(width, width));
        diceImage.setImageDrawable(getResources().getDrawable(R.drawable.dice_2));
        diceImage.setOnClickListener(getDiceClickListener());
        diceImage.setScaleType(ImageView.ScaleType.FIT_XY);
        diceImage.setEnabled(false);
    }

    private OnClickListener pieceClickListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            int num = game.num;
            if (!((LudoPiece) v).open) {
                ((LudoPiece) v).open = true;
                num -= 5;
            }
//            int i = 0;
            final int pieceIndex = ((LudoPiece)v).pieceNum;

            for (LudoPiece l : players.get(currentPlayer).getmPiece()) {
//                if (l.startPosition.mCenterPoint == ((LudoPiece) v).startPosition.mCenterPoint) {
//                    pieceIndex = i;
//                }
                l.setEnabled(false);
                l.clearAnimation();
                ((ImageView) l.getTag()).setVisibility(INVISIBLE);
                ((ImageView) l.getTag()).clearAnimation();
//                i++;
            }

            if (gameRef != null) {
                final int finalNum = num;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(this);

                                }
                            }
                        };
                        OnCompleteListener<Void> onCompleteListener1 = new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(this);
                                }
                            }
                        };
                        OnCompleteListener<Void> onCompleteListener2 = new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                {
                                    gameRef.child("updateUI").setValue(updateNum++%26).addOnCompleteListener(this);
                                }
                            }
                        };


                        gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(onCompleteListener);
                        gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(onCompleteListener1);
                        gameRef.child("updateUI").setValue(updateNum++%26).addOnCompleteListener(onCompleteListener2);

                        players.get(currentPlayer).move(finalNum, (LudoPiece) v);
                    }
                });
                thread.start();
            }
            else
                players.get(currentPlayer).move(num, (LudoPiece)v);
        }
    };



    private OnClickListener getDiceClickListener()
    {
        OnClickListener diceClickListener = new OnClickListener() {

            @Override
            public void onClick(final View v) {

                diceImage.setEnabled(false);
                mArrows[currentPlayer].setVisibility(INVISIBLE);
                mArrows[currentPlayer].setAnimation(null);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Runnable runnable1 = new Runnable() {
                            @Override
                            public void run() {
                                if (gameRef != null) {
                                    final OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(!task.isSuccessful())
                                            {
                                                gameRef.child("updated").setValue(false).addOnCompleteListener(this);
                                            }
                                        }
                                    };

                                    gameRef.child("updated").setValue(false).addOnCompleteListener(onCompleteListener);

                                    synchronized (this) {
                                        this.notify();
                                    }
                                }
                            }
                        };
                        synchronized (runnable1)
                        {
                            new Thread(runnable1).start();
                            try {
                                runnable1.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                int num;

                                //DICE ANIMATION START///////////////DICE ANIMATION START////////////////DICE ANIMATION START////////////////////
                                //DICE ANIMATION START///////////////DICE ANIMATION START////////////////DICE ANIMATION START////////////////////
                                //DICE ANIMATION START///////////////DICE ANIMATION START////////////////DICE ANIMATION START////////////////////
                                //DICE ANIMATION START///////////////DICE ANIMATION START////////////////DICE ANIMATION START////////////////////
                                //DICE ANIMATION START///////////////DICE ANIMATION START////////////////DICE ANIMATION START////////////////////
                                //DICE ANIMATION START///////////////DICE ANIMATION START////////////////DICE ANIMATION START////////////////////

                                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
                                valueAnimator.setDuration(150);
                                final int value = 9;
                                valueAnimator.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
//                dice1.setLayoutParams(new LinearLayout.LayoutParams((int)(dice1.getLayoutParams().width + boxSize), (int)(dice1.getLayoutParams().height + boxSize)));
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
//                dice1.setLayoutParams(new LinearLayout.LayoutParams((int)(dice1.getLayoutParams().width - boxSize), (int)(dice1.getLayoutParams().height - boxSize)));


                                        ValueAnimator animator1 = ValueAnimator.ofFloat(0f, 1f);
                                        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                if ((diceImage.getLayoutParams().width - value >= width / 10))
                                                    diceImage.setLayoutParams(new LayoutParams((diceImage.getLayoutParams().width - value), (diceImage.getLayoutParams().height - value)));
                                            }
                                        });

                                        animator1.setDuration(150);
                                        animator1.start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                });
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        diceImage.setLayoutParams(new LayoutParams((diceImage.getLayoutParams().width + value), (diceImage.getLayoutParams().height + value)));
                                    }
                                });
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                //DICE ANIMATED///////////////DICE ANIMATED////////////////DICE ANIMATED////////////////////
                                final View v1 = v;

                                if (players.get(currentPlayer).type != PlayerType.ONLINE) {
                                    Random random = new Random();
                                    num = Integer.parseInt(textView.getText().toString()) == 0 ? random.nextInt(6) : Integer.parseInt(textView.getText().toString()) - 1;
                                    num++;
                                    game.num = num;
                                } else {
                                    num = game.num;
                                }

                                Glide.with(context).asGif()
                                        .load(R.raw.dice_gif)
                                        .into((ImageView) v);
                                valueAnimator.start();

                                final int finalNum = num;
                                final int finalNum1 = num;

                                postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalNum <= 6)
                                            ((ImageView) v1).setImageDrawable(getResources().getDrawable(getResources().getIdentifier("dice_" + (finalNum), "drawable", getContext().getPackageName())));
                                        else {
                                            ((ImageView) v1).setImageDrawable(getResources().getDrawable(getResources().getIdentifier("dice_" + (6), "drawable", getContext().getPackageName())));
                                            Toast.makeText(context, "dice Value is" + (finalNum + 1), Toast.LENGTH_SHORT).show();
                                        }
                                        if (finalNum1 == 6) turnChange = false;
                                        makeMove(finalNum1);
                                    }
                                }, 1000);

                            }
                        };
                        ((Activity) context).runOnUiThread(runnable);
                    }
                });
                thread.start();
            }
        };



        return diceClickListener;
    }


    public void start()
    {
//        Handler handler = new Handler(context.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                mArrows[currentPlayer].setVisibility(VISIBLE);
                mArrows[currentPlayer].setAnimation(translateAnimation);
                diceImage.setX(dicePoints[currentPlayer].x);
                diceImage.setY(dicePoints[currentPlayer].y);
                diceImage.setEnabled(true);

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
                diceImage.setX(dicePoints[currentPlayer].x);
                diceImage.setY(dicePoints[currentPlayer].y);
                if(currentPlayer == 0)
                {
                    diceImage.setEnabled(true);
                }
                else diceImage.setEnabled(false);
                addOnDataChangeListener();
            }
        };

        ((Activity) context).runOnUiThread(runnable1);

    }

    public void addOnDataChangeListener() {
        gameRef.child("updateUI").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (gameStarted) {
                            gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(((Long) dataSnapshot.child("dice_value").getValue()) != null && ((Long) dataSnapshot.child("piece_number").getValue()) != null) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        assert user != null;
                                        if (!user.getUid().equals(dataSnapshot.child("turn").getValue(String.class))) {
                                            int num = ((Long) dataSnapshot.child("dice_value").getValue()).intValue();
                                            int pieceNum = ((Long) dataSnapshot.child("piece_number").getValue()).intValue();
                                            game.num = num;
                                            game.pieceIndex = pieceNum;
                                            diceImage.performClick();
                                        }
//                                        else {
//                                            diceImage.setX(dicePoints[currentPlayer].x);
//                                            diceImage.setY(dicePoints[currentPlayer].y);
//                                            mArrows[currentPlayer].setVisibility(VISIBLE);
//                                            mArrows[currentPlayer].setAnimation(translateAnimation);
//                                            diceImage.setEnabled(true);
//                                        }
                                    }
                                    dataSnapshot.getRef().getParent().removeEventListener(this);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {

                            diceImage.setX(dicePoints[currentPlayer].x);
                            diceImage.setY(dicePoints[currentPlayer].y);
                            mArrows[currentPlayer].setVisibility(VISIBLE);
                            mArrows[currentPlayer].setAnimation(translateAnimation);
                            if(currentPlayer == 0)
                            {
                                diceImage.setEnabled(true);
                            }
                            else diceImage.setEnabled(false);
                            gameStarted = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        final Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean updated = false;

                                for (int i = 0; i < numberOfPlayers; i++) {
                                    updated = dataSnapshot.child(uids[i]).getValue(Boolean.class) != null && (boolean) dataSnapshot.child(uids[i]).getValue();
                                }

                                if (updated) {
                                    final Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {

                                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    gameRef.child("updated").setValue(true).addOnCompleteListener(this);
                                                }
                                            };

                                            gameRef.child("updated").setValue(true).addOnCompleteListener(onCompleteListener);
                                            this.notify();
                                        }
                                    };
                                    synchronized (runnable) {
                                        new Thread(runnable).start();
                                        try {
                                            runnable.wait();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }
                        });
                        thread.start();
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
            gameRef.child(uids[i]).addValueEventListener(valueEventListener);
        }
        gameRef.child("updated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!firstupdate) {
                            if (!(boolean) dataSnapshot.getValue()) {
                                gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        Thread thread1 = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (int i = 0; i < numberOfPlayers; i++) {
                                                    final int finalI = i;
                                                    OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            dataSnapshot.getRef().child(uids[finalI]).setValue(false).addOnCompleteListener(this);
                                                        }
                                                    };
                                                    dataSnapshot.getRef().child(uids[i]).setValue(false).addOnCompleteListener(onCompleteListener);
                                                }
                                            }
                                        });
                                        thread1.start();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else
                                gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {

                                        if(FirebaseAuth.getInstance().getUid().equals(dataSnapshot.child("firstUID"))) {
                                            String currentUID = (String) dataSnapshot.child("turn").getValue();
                                            for (int i = 0; i < numberOfPlayers; i++) {
                                                if (uids[i].equals(currentUID)) {
                                                    currentPlayer = i;
                                                }
                                            }

                                            if ((((Long) dataSnapshot.child("dice_value").getValue())).intValue() != 6) {
                                                currentPlayer++;
                                                currentPlayer %= numberOfPlayers;
                                            }

                                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        mArrows[currentPlayer].setVisibility(VISIBLE);
                                                        mArrows[currentPlayer].setAnimation(translateAnimation);
                                                        diceImage.setX(dicePoints[currentPlayer].x);
                                                        diceImage.setY(dicePoints[currentPlayer].y);
                                                        if (currentPlayer == 0) {
                                                            diceImage.setEnabled(true);
                                                        } else diceImage.setEnabled(false);
                                                    } else {
                                                        dataSnapshot.getRef().child("turn").setValue(uids[currentPlayer]).addOnCompleteListener(this);
                                                    }
                                                }
                                            };

                                            dataSnapshot.getRef().child("turn").setValue(uids[currentPlayer]).addOnCompleteListener(onCompleteListener);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        } else firstupdate = false;
                    }
                });
                thread.start();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initializePieces(int numberOfPlayers, Color[] colors, PlayerType[] playerTypes) {

        LudoBox.TransitionPlayer[] transitionPlayers = {LudoBox.TransitionPlayer.ONE, LudoBox.TransitionPlayer.TWO, LudoBox.TransitionPlayer.THREE, LudoBox.TransitionPlayer.FOUR};
        final LudoBox[][] ludoBoxes = {oneP, twoP, threeP, fourP};

        for (int i = 0; i < numberOfPlayers; i++) {
            LudoPlayer player = new LudoPlayer(game, transitionPlayers[i], null, playerTypes[i], ludoBoxes[i][0].nextBox);
            LudoPiece[] ludoPieces = new LudoPiece[4];

            for (int j = 0; j < 4; j++) {
                final LudoPiece ludoPiece = new LudoPiece(context, player, game, colors[i], pieceSize, ludoBoxes[i][j], ludoBoxes[i][j]);
                ludoPiece.pieceNum = j;
                ImageView circle = new ImageView(context);
                circle.setImageResource(R.drawable.circle);
                circle.setWillNotDraw(false);
                circle.setVisibility(INVISIBLE);
                circle.setLayoutParams(new LayoutParams(3 * pieceSize / 5, 3 * pieceSize / 5));
                ludoPiece.setTag(circle);
                pieces.add(ludoPiece);
                ludoPiece.setOnClickListener(pieceClickListener);
                final int finalJ = j;
                final int finalI = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ludoBoxes[finalI][finalJ].addPiece(ludoPiece);
                    }
                });
                thread.start();
                ludoPieces[j] = ludoPiece;
                ludoPiece.setEnabled(false);
            }
            player.setmPiece(ludoPieces);
            players.add(player);
        }
    }

    private void makeMove(final int num) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LudoPiece[] ludoPieces = players.get(currentPlayer).getmPiece();
                int foundNum = 0;
                LudoPiece temp = null;
                boolean sameCoordinates = true;
                if (players.get(currentPlayer).type == PlayerType.HUMAN) {
                    for (LudoPiece l : ludoPieces) {
                        if (l.isValid(num)) {
                            foundNum++;
                            l.setEnabled(true);
                            l.setAnimation(alphaAnimation);
                            ((ImageView) l.getTag()).setVisibility(VISIBLE);
                            startRotation((ImageView) l.getTag());

                            if (temp != null) {
                                if (temp.mBox.mCenterPoint != l.mBox.mCenterPoint) {
                                    sameCoordinates = false;
                                }
                            }
                            temp = l;
                        }
                    }
                    if (foundNum == 1 || (sameCoordinates && temp != null)) {
                        temp.performClick();
                    } else if (foundNum == 0) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentPlayer++;
                                currentPlayer %= numberOfPlayers;
                                getDiceImage().setX(dicePoints[currentPlayer].x);
                                getDiceImage().setY(dicePoints[currentPlayer].y);
                                getDiceImage().setEnabled(true);
                                getmArrows()[currentPlayer].setVisibility(VISIBLE);
                                getmArrows()[currentPlayer].setAnimation(translateAnimation);
                                if (players.get(currentPlayer).type == PlayerType.CPU) {
                                    getDiceImage().performClick();
                                }
                            }
                        }, 1000);
                    }
                } else if (players.get(currentPlayer).type == PlayerType.CPU) {

                    LudoPiece[] validPieces = new LudoPiece[4];

                    for (LudoPiece l : ludoPieces) {
                        if (l.isValid(num)) {
                            validPieces[foundNum] = l;
                            foundNum++;
                            l.setEnabled(true);
                            l.setAnimation(alphaAnimation);
                            ((ImageView) l.getTag()).setVisibility(VISIBLE);
                            startRotation((ImageView) l.getTag());
                            if (temp != null) {
                                if (temp.mBox.mCenterPoint != l.mBox.mCenterPoint) {
                                    sameCoordinates = false;
                                }
                            }
                            temp = l;
                        }
                    }
                    if (foundNum == 1 || (sameCoordinates && temp != null)) {
                        temp.performClick();
                    } else if (foundNum == 0) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentPlayer++;
                                currentPlayer %= numberOfPlayers;
                                getDiceImage().setX(dicePoints[currentPlayer].x);
                                getDiceImage().setY(dicePoints[currentPlayer].y);
                                getDiceImage().setEnabled(true);
                                getmArrows()[currentPlayer].setVisibility(VISIBLE);
                                getmArrows()[currentPlayer].setAnimation(translateAnimation);
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

                    if(!FirebaseAuth.getInstance().getUid().equals(uids[currentPlayer])) {
                        if (pieceIndex != -1) {
                            LudoPiece piece = players.get(currentPlayer).getmPiece()[pieceIndex];
                            players.get(currentPlayer).move(num, piece);
                        } else {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(!task.isSuccessful())
                                                    {
                                                        gameRef.child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnCompleteListener(this);
                                                    }
                                                }
                                            };
                                            gameRef.child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnCompleteListener(onCompleteListener);
                                        }
                                    });
                                    thread.start();
                                }
                            }, 1000);
                        }
                    }
                    else{
                        for (LudoPiece l : ludoPieces) {
                            if (l.isValid(num)) {
                                foundNum++;
                                l.setEnabled(true);
                                l.setAnimation(alphaAnimation);
                                ((ImageView) l.getTag()).setVisibility(VISIBLE);
                                startRotation((ImageView) l.getTag());

                                if (temp != null) {
                                    if (temp.mBox.mCenterPoint != l.mBox.mCenterPoint) {
                                        sameCoordinates = false;
                                    }
                                }
                                temp = l;
                            }
                        }
                        if (foundNum == 1 || (sameCoordinates && temp != null)) {
                            temp.performClick();
                        }
                        else if(foundNum == 0)
                        {

                            OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful())
                                    {
                                        gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(this);
                                    }
                                }
                            };
                            OnCompleteListener<Void> onCompleteListener1 = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful())
                                    {
                                        gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(this);
                                    }
                                }
                            };
                            OnCompleteListener<Void> onCompleteListener2 = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful())
                                    {
                                        gameRef.child("updateUI").setValue(updateNum++%26).addOnCompleteListener(this);
                                    }
                                }
                            };
                            OnCompleteListener<Void> onCompleteListener3 = new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful())
                                    {
                                        gameRef.child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnCompleteListener(this);
                                    }
                                }
                            };

                            gameRef.child("dice_value").setValue(game.num).addOnCompleteListener(onCompleteListener);
                            gameRef.child("piece_number").setValue(pieceIndex).addOnCompleteListener(onCompleteListener1);
                            gameRef.child("updateUI").setValue(++updateNum%26).addOnCompleteListener(onCompleteListener2);
                            gameRef.child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnCompleteListener(onCompleteListener3);
                        }
                    }
                }
            }
        });
        thread.start();

    }

    public ImageView getDiceImage() {
        return diceImage;
    }

    public ArrayList<LudoPiece> getPieces() {
        return pieces;
    }

    public ImageView[] getmArrows() {
        return mArrows;
    }

    private void initializeBox(int y, int width) {
        Point firstPoint = new Point(mBoxWidth / 2, y + width - (mBoxWidth / 2));
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
        setStartingBoxes(numberOfPlayers);

    }

    private void setStartingBoxes(int numberOfPlayers) {

        Point point = new Point(0, 0);
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

}
