package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.VISIBLE;

/**
 * Created by Taha Malik on 4/14/2018.
 **/

public class LudoPlayer {

    private LudoGame mGame;
    private LudoPiece[] mPiece;
    PlayerType type;
    LudoBox startingPosition;
    LudoBox.TransitionPlayer player;

    LudoPlayer(LudoGame game, LudoBox.TransitionPlayer player, LudoPiece[] piece, PlayerType playerType, LudoBox firstBox) {
        mPiece = piece;
        this.player = player;
        mGame = game;
        type = playerType;
        startingPosition = firstBox;
    }

    public LudoBox.TransitionPlayer getPlayer() {
        return player;
    }

    public void setPlayer(LudoBox.TransitionPlayer player) {
        this.player = player;
    }

    public LudoPiece[] getmPiece() {
        return mPiece;
    }

    public void setmPiece(LudoPiece[] mPiece) {
        this.mPiece = mPiece;
    }

    public void move(final int num, final LudoPiece piece) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                LudoBox currentBox = piece.mBox;
                currentBox.removePiece(piece);
                Point from = new Point(currentBox.firstX, currentBox.firstY);
                Point to = new Point(currentBox.nextBox.firstX, currentBox.nextBox.firstY);
                for (int i = 0; i < num; i++) {

                    LudoBox nextBox;
                    if (player == currentBox.transitionPlayer) {
                        to.y = currentBox.transitionBox.firstY;
                        to.x = currentBox.transitionBox.firstX;
                        nextBox = currentBox.transitionBox;
                    } else {
                        to.y = currentBox.nextBox.firstY;
                        to.x = currentBox.nextBox.firstX;
                        nextBox = currentBox.nextBox;
                    }

                    animatePiece(piece, from, to);

                    currentBox = nextBox;
                    from.x = to.x;
                    from.y = to.y;

                }

                if (currentBox.mPieceCount > 0) {

                    if(currentBox.winBox && currentBox.mPieceCount == 4)
                    {
                        if(mGame.currentPlayer == 0) {
                            player_won();
                            return;
                        }else
                        {
                            player_lost();
                            return;
                        }
                    }
                    else if (!currentBox.stop) {
                        int opponent = 0;
                        int mine = 1;
                        final LudoPiece[] ludoPieces = {null, null, null, null};
                        for (LudoPiece l : currentBox.mPieces) {
                            if (l.player.player != piece.player.player) {
                                ludoPieces[opponent++] = l;
                            } else {
                                mine++;
                            }
                        }
                        if (mine == opponent) {

                            LudoGame.turnChange = false;
//                            for (int i = 0; i < opponent; i++) {
                                killPieces(ludoPieces, opponent);
//                            }
                        }
                    }
                }
                currentBox.addPiece(piece);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (mGame.players.get(mGame.currentPlayer).type != PlayerType.ONLINE) {
                            mGame.getmArrows()[mGame.currentPlayer].setVisibility(View.INVISIBLE);
//                            mGame.getmArrows()[mGame.currentPlayer].setAnimation(null);

                            if (LudoGame.turnChange) {
                                mGame.currentPlayer++;
                                mGame.currentPlayer %= mGame.numberOfPlayers;
                            } else LudoGame.turnChange = true;
//                            mGame.baseLayout.postInvalidate();
//                            mGame.baseLayout.refreshDrawableState();
//                            mGame.baseLayout.requestLayout();
                            mGame.getmArrows()[mGame.currentPlayer].setVisibility(VISIBLE);
//                            mGame.getmArrows()[mGame.currentPlayer].setAnimation(translateAnimation);
//                            mGame.getDiceImage().setY(mGame.dicePoints[mGame.currentPlayer].y);
//                            mGame.getDiceImage().setX(mGame.dicePoints[mGame.currentPlayer].x);
                            mGame.getDiceImage().setEnabled(true);
                            if (mGame.players.get(mGame.currentPlayer).type == PlayerType.CPU) {
                                mGame.getDiceImage().performClick();

                            }
                        } else {
                            if (LudoGame.turnChange) {
                                mGame.currentPlayer++;
                                mGame.currentPlayer %= mGame.numberOfPlayers;
                            }
                            else LudoGame.turnChange = true;
//                            mGame.baseLayout.postInvalidate();
//                            mGame.baseLayout.refreshDrawableState();
//
//                            mGame.baseLayout.requestLayout();
////                            mGame.diceImage.setX(mGame.dicePoints[mGame.currentPlayer].x);
////                            mGame.diceImage.setY(mGame.dicePoints[mGame.currentPlayer].y);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful() || !task.isComplete()) {
                                                mGame.gameRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(this);
                                            }
                                            else{
                                                mGame.done = false;
                                            }
                                        }
                                    };
                                    mGame.gameRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(onCompleteListener);
                                }
                            }).start();
                        }

                        synchronized (this) {
                            this.notify();
                        }

                    }
                };
                synchronized (runnable) {
                    Handler handler = new Handler(Looper.getMainLooper());
//                    ((Activity) mGame.context).runOnUiThread(runnable);
                    handler.post(runnable);
                    try {
                        runnable.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }

    private void player_won() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Toast.makeText(mGame.context, "You WON", Toast.LENGTH_SHORT).show();
//                View layout = ((LudoActivity)mGame.context).findViewById(R.id.you_won_dialogue);
//                layout.setVisibility(VISIBLE);

//                ObjectAnimator scaleX = ObjectAnimator.ofFloat(layout,"scaleX",1f);
//                scaleX.setDuration(1000);
//                ObjectAnimator scaleY = ObjectAnimator.ofFloat(layout,"scaleY",1f);
//                scaleY.setDuration(1000);

//                AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.setInterpolator(new BounceInterpolator());
//                animatorSet.start();


                mGame.getDiceImage().setVisibility(View.GONE);
            }
        };
//        Handler handler = new Handler(mGame.context.getMainLooper());
        ((Activity)mGame.context).runOnUiThread(runnable);
    }

    private void player_lost() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mGame.context, "You LOST", Toast.LENGTH_SHORT).show();
                View layout =((LudoActivity)mGame.context).findViewById(R.id.you_lost_dialogue);
                layout.setVisibility(VISIBLE);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(layout,"scaleX",1f);
                scaleX.setDuration(1000);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(layout,"scaleY",1f);
                scaleY.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setInterpolator(new BounceInterpolator());
                animatorSet.start();

                mGame.getDiceImage().setVisibility(View.GONE);
            }
        };
//        Handler handler = new Handler(mGame.context.getMainLooper());
        ((Activity)mGame.context).runOnUiThread(runnable);
    }

    private void animatePiece(final LudoPiece piece, final Point from, final Point to)
    {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

//                int greaterX;
//                int greaterY;
//                int smallerX;
//                int smallerY;
//
//                if(from.x > to.x)
//                {
//                    greaterX = from.x;
//                    smallerX = to.x;
//                }
//                else
//                {
//                    greaterX = to.x;
//                    smallerX = from.x;
//                }
//
//                if(from.y > to.y)
//                {
//                    greaterY = from.y;
//                    smallerY = to.y;
//                }
//                else
//                {
//                    greaterY = to.y;
//                    smallerY = from.y;
//
                int duration = 120;
                ObjectAnimator straightTranslatorStart = null;
                ObjectAnimator straightTranslatorEnd = null;
                ObjectAnimator sideTranslatorUp = null;
                ObjectAnimator sideTranslatorDown = null;
                ObjectAnimator scaleTranslatorXUp = null;
                ObjectAnimator scaleTranslatorYUp = null;
                ObjectAnimator scaleTranslatorXDown = null;
                ObjectAnimator scaleTranslatorYDown = null;

                if(from.x != to.x && from.y != to.y)
                {

                    int xDiff = to.x - from.x;
//                    xDiff = xDiff < 0 ? xDiff * -1 : xDiff;
                    int yDiff = to.y - from.y;
//                    yDiff = yDiff < 0 ? yDiff * -1 : yDiff;

                    straightTranslatorStart = ObjectAnimator.ofFloat(piece, "translationX", from.x,from.x + xDiff/2);
                    sideTranslatorUp = ObjectAnimator.ofFloat(piece, "translationY", from.y, from.y + yDiff/2);

                    scaleTranslatorXUp = ObjectAnimator.ofFloat(piece, "scaleX", 1.3f);
                    scaleTranslatorYUp = ObjectAnimator.ofFloat(piece, "scaleY", 1.3f);

                    straightTranslatorEnd = ObjectAnimator.ofFloat(piece, "translationX", from.x + xDiff/2, to.x);
                    sideTranslatorDown = ObjectAnimator.ofFloat(piece, "translationY", from.y + yDiff/2 , to.y);

                    scaleTranslatorXDown = ObjectAnimator.ofFloat(piece, "scaleX", 1f);
                    scaleTranslatorYDown = ObjectAnimator.ofFloat(piece, "scaleY", 1f);

                    scaleTranslatorYUp.setDuration(duration);
                    scaleTranslatorXUp.setDuration(duration);
                    sideTranslatorUp.setDuration(duration);
                    straightTranslatorStart.setDuration(duration);
                    straightTranslatorEnd.setDuration(duration);
                    sideTranslatorDown.setDuration(duration);
                    scaleTranslatorXDown.setDuration(duration);
                    scaleTranslatorYDown.setDuration(duration);

                }
                else if(from.x != to.x)
                {

                    int xDiff = to.x - from.x;
                    int yDiff = xDiff > 0 ? xDiff * -1 : xDiff;
//                    int yDiff = to.y - from.y;
//                    yDiff = yDiff < 0 ? yDiff * -1 : yDiff;

                    straightTranslatorStart = ObjectAnimator.ofFloat(piece, "translationX", from.x,from.x + xDiff/2);
                    sideTranslatorUp = ObjectAnimator.ofFloat(piece, "translationY", from.y, from.y + yDiff/1.4f);

                    scaleTranslatorXUp = ObjectAnimator.ofFloat(piece, "scaleX", 1.4f);
                    scaleTranslatorYUp = ObjectAnimator.ofFloat(piece, "scaleY", 1.4f);

                    straightTranslatorEnd = ObjectAnimator.ofFloat(piece, "translationX", from.x + xDiff/2, to.x);
                    sideTranslatorDown = ObjectAnimator.ofFloat(piece, "translationY", from.y + yDiff/1.4f , to.y);

                    scaleTranslatorXDown = ObjectAnimator.ofFloat(piece, "scaleX", 1f);
                    scaleTranslatorYDown = ObjectAnimator.ofFloat(piece, "scaleY", 1f);

                    scaleTranslatorYUp.setDuration(duration);
                    scaleTranslatorXUp.setDuration(duration);
                    sideTranslatorUp.setDuration(duration);
                    straightTranslatorStart.setDuration(duration);
                    straightTranslatorEnd.setDuration(duration);
                    sideTranslatorDown.setDuration(duration);
                    scaleTranslatorXDown.setDuration(duration);
                    scaleTranslatorYDown.setDuration(duration);


                }
                else if(from.y != to.y)
                {
//                    int xDiff = to.x - from.x;
//                    xDiff = xDiff < 0 ? xDiff * -1 : xDiff;
                    int yDiff = to.y - from.y;
//                    int xDiff = yDiff > 0 ? yDiff * -1 : yDiff;

//                    straightTranslatorStart = ObjectAnimator.ofFloat(piece, "translationY", from.y,from.y + yDiff/2);
                    straightTranslatorStart = ObjectAnimator.ofFloat(piece, "translationY", from.y,from.y + yDiff);
//                    sideTranslatorUp = ObjectAnimator.ofFloat(piece, "translationX", from.x, from.x + yDiff/1.4f);
                    sideTranslatorUp = ObjectAnimator.ofFloat(piece, "translationX", from.x, from.x + yDiff/1.4f);
                    scaleTranslatorXUp = ObjectAnimator.ofFloat(piece, "scaleX", 1.4f);
                    scaleTranslatorYUp = ObjectAnimator.ofFloat(piece, "scaleY", 1.4f);

//                    straightTranslatorEnd = ObjectAnimator.ofFloat(piece, "translationY", from.y + yDiff/2, to.y);
                    straightTranslatorEnd = ObjectAnimator.ofFloat(piece, "translationY", from.y + yDiff/2, to.y);
                    sideTranslatorDown = ObjectAnimator.ofFloat(piece, "translationX", from.x + yDiff/1.4f , to.x);
                    sideTranslatorDown = ObjectAnimator.ofFloat(piece, "translationX", from.x + yDiff/1.4f , to.x);

                    scaleTranslatorXDown = ObjectAnimator.ofFloat(piece, "scaleX", 1f);
                    scaleTranslatorYDown = ObjectAnimator.ofFloat(piece, "scaleY", 1f);

                    scaleTranslatorYUp.setDuration(duration);
                    scaleTranslatorXUp.setDuration(duration);
                    sideTranslatorUp.setDuration(duration);
                    straightTranslatorStart.setDuration(duration);
                    straightTranslatorEnd.setDuration(duration);
                    sideTranslatorDown.setDuration(duration);
                    scaleTranslatorXDown.setDuration(duration);
                    scaleTranslatorYDown.setDuration(duration);


                }

                if(straightTranslatorStart != null)
                {
                    AnimatorSet firstHalf = new AnimatorSet();
                    firstHalf.playTogether(straightTranslatorStart,sideTranslatorUp, scaleTranslatorXUp, scaleTranslatorYUp);
                    firstHalf.setInterpolator(new LinearInterpolator());
                    final AnimatorSet secondHalf = new AnimatorSet();
                    secondHalf.playTogether(straightTranslatorEnd, sideTranslatorDown, scaleTranslatorXDown,scaleTranslatorYDown);
                    secondHalf.setInterpolator(new BounceInterpolator());
                    final Runnable runnable1 = this;
                    secondHalf.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            synchronized (runnable1)
                            {
                                runnable1.notify();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                            synchronized (runnable1)
                            {
                                runnable1.notify();
                            }

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
//                    firstHalf.play(firstHalf).before(secondHalf);
                    firstHalf.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            secondHalf.start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    firstHalf.start();
                }
                else
                {
                    synchronized (this)
                    {
                        this.notify();
                    }
                }
            }
        };

        synchronized (runnable)
        {
            ((Activity)mGame.context).runOnUiThread(runnable);

            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

//    private void killPieces(final LudoPiece[] pieces, final int p_size)
//    {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                final int size = p_size;
//
//                LudoBox fromBox = pieces[0].mBox;
//                LudoBox toBox = pieces[0].mBox.previousBox;
//
//                while(fromBox != pieces[0].startPosition.nextBox)
//                {
//
//                    final LudoBox finalToBox = toBox;
//                    final Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//
//                            ObjectAnimator[] animators = new ObjectAnimator[size*2];
//
//                            for(int i = 0; i < size; i++)
//                            {
//                                animators[i] = ObjectAnimator.ofFloat(pieces[i], "translationX", finalToBox.firstX);
//                                animators[i].setDuration(50);
//                                animators[i+size] = ObjectAnimator.ofFloat(pieces[i], "translationY", finalToBox.firstY);
//                                animators[i+size].setDuration(50);
//                            }
//
//                            final Runnable runnable1 = this;
//
//                            AnimatorSet translation = new AnimatorSet();
//                            translation.playTogether(animators);
//                            translation.setInterpolator(new LinearInterpolator());
//                            translation.addListener(new Animator.AnimatorListener() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    synchronized (runnable1)
//                                    {
//                                        runnable1.notify();
//                                    }
//                                }
//
//                                @Override
//                                public void onAnimationCancel(Animator animation) {
//                                    synchronized (runnable1)
//                                    {
//                                        runnable1.notify();
//                                    }
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animator animation) {
//
//                                }
//                            });
//                            translation.start();
//
//                        }
//                    };
//
//                    synchronized (runnable)
//                    {
//                        ((Activity)mGame.context).runOnUiThread(runnable);
//                        try {
//                            runnable.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        finally {
//                            fromBox = toBox;
//                            toBox = toBox.previousBox;
//                        }
//                    }
//                }
//
//
//                final Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//
//                        ObjectAnimator[] animators = new ObjectAnimator[size*2];
//
//                        for(int i = 0; i < size; i++)
//                        {
//                            animators[i] = ObjectAnimator.ofFloat(pieces[i], "translationX", pieces[i].startPosition.firstX);
//                            animators[i].setDuration(50);
//                            animators[i+size] = ObjectAnimator.ofFloat(pieces[i], "translationY", pieces[i].startPosition.firstY);
//                            animators[i+size].setDuration(50);
//
//                            final int finalI = i;
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pieces[finalI].startPosition.addPiece(pieces[finalI]);
//                                }
//                            }).start();
//
//                        }
//
//                        final Runnable runnable1 = this;
//
//                        AnimatorSet translation = new AnimatorSet();
//                        translation.playTogether(animators);
//                        translation.setInterpolator(new LinearInterpolator());
//                        translation.addListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                synchronized (runnable1)
//                                {
//                                    runnable1.notify();
//                                }
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//                                synchronized (runnable1)
//                                {
//                                    runnable1.notify();
//                                }
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        });
//                        translation.start();
//
//                    }
//                };
//                synchronized (runnable) {
//                    ((Activity) mGame.context).runOnUiThread(runnable);
//                    try {
//                        runnable.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

    private void killPieces(final LudoPiece[] pieces, final int size) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Runnable runnable1 = this;
                for (int i = 0; i < size; i++) {

                    final LudoPiece piece = pieces[i];
                    piece.open = false;
                    com.example.apple.ludochallenge.Point[] points = getPath(piece);
                    float[] pathX = com.example.apple.ludochallenge.Point.getXArray(points);
                    float[] pathY = com.example.apple.ludochallenge.Point.getYArray(points);

                    final ObjectAnimator translationX = ObjectAnimator.ofFloat(piece, "translationX", pathX);
                    translationX.setDuration(pathX.length*100);
                    ObjectAnimator translationY = ObjectAnimator.ofFloat(piece, "translationY", pathY);
                    translationY.setDuration(pathY.length*100);
                    final AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(translationX, translationY);
                    animatorSet.setInterpolator(new FastOutSlowInInterpolator());
                    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {

                                    piece.mBox.removePiece(piece);
                                    piece.startPosition.addPiece(piece);
                                    synchronized (runnable1) {
                                        runnable1.notify();
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                    piece.mBox.removePiece(piece);
                                    piece.startPosition.addPiece(piece);
                                    synchronized (runnable1) {
                                        runnable1.notify();
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            animatorSet.start();

                        }
                    });
                }
            }
        };
        synchronized (runnable) {
            Thread thread = new Thread(runnable);
            thread.start();
            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    private com.example.apple.ludochallenge.Point[] getPath(LudoPiece ludoPiece)
    {
        LudoBox currentBox = ludoPiece.mBox;
        int diff = 0;

        while (currentBox != ludoPiece.startPosition.nextBox)
        {
            diff++;
            currentBox = currentBox.previousBox;
        }
        diff++;
        currentBox = ludoPiece.mBox;
        com.example.apple.ludochallenge.Point[] points = new com.example.apple.ludochallenge.Point[diff+1];
        int i = 0;
        while(currentBox != ludoPiece.startPosition.nextBox)
        {
            points[i] = new com.example.apple.ludochallenge.Point();
            points[i].x = currentBox.firstX;
            points[i].y = currentBox.firstY;
            i++;
            currentBox = currentBox.previousBox;
        }
        points[i] = new com.example.apple.ludochallenge.Point();
        points[i].x = currentBox.firstX;
        points[i++].y = currentBox.firstY;
        currentBox = ludoPiece.startPosition;
        points[i] = new com.example.apple.ludochallenge.Point();
        points[i].x = currentBox.firstX;
        points[i].y = currentBox.firstY;
        return points;
    }

}
