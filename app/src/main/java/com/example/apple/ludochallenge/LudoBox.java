package com.example.apple.ludochallenge;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Vector;

/**
 * Created by Taha Malik on 4/18/2018.
 **/
public class LudoBox {


    int defaultWidth;
    int defaultHeight;
    LudoGame mGame;
    Point mCenterPoint;
    int firstX;
    boolean home = false;
    int firstY;
    int mSize;
    Vector<LudoPiece> mPieces = new Vector<>();
    int mPieceCount = 0;
    LudoBox nextBox;
    LudoBox transitionBox;
    LudoBox previousBox;
    boolean stop = false;
    boolean winBox = false;
    TransitionPlayer transitionPlayer;


    LudoBox(Point mPoints, int boxSize, LudoGame game, TransitionPlayer player, LudoBox nextBox, LudoBox transitionBox, LudoBox previousBox) {
        mGame = game;
        mSize = boxSize;
        mCenterPoint = new Point(mPoints);
        this.nextBox = nextBox;
        this.previousBox = previousBox;
        this.transitionBox = transitionBox;
        transitionPlayer = player;
        defaultWidth = boxSize;// / 2;
        defaultHeight = boxSize;
        firstX = mCenterPoint.x - defaultWidth / 2;
        firstY = mCenterPoint.y - defaultHeight;
    }

//    void addPiece(final LudoPiece piece) {
//
////        Point oldCoordinate = new Point();
////        oldCoordinate.x = (int) piece.x;
////        oldCoordinate.y = (int) piece.y;
//        piece.mBox = this;
//        mPieces.add(piece);
//        mPieceCount++;
//
//        int pieceHeight = mSize;
//        int pieceWidth = pieceHeight / 2;
//
//        for (int i = 1; i < mPieceCount; i++) {
//            pieceHeight = pieceHeight - pieceHeight / 6;
//            pieceWidth = pieceHeight / 2;
//        }
//
//        int x = mCenterPoint.x;
//        int y = mCenterPoint.y + pieceHeight / 8;
//        x -= pieceWidth / 2;
//        y -= pieceHeight;
//
//        x += (mPieceCount - 1) * (pieceWidth / 2);
//        int startingPointX = x;
//
//        for (int i = 0; i < mPieceCount; i++) {
//            final LudoPiece piece1 = mPieces.get(i);
//
//            final int finalStartingPointX = startingPointX;
//            final int finalPieceWidth = pieceWidth;
//            final int finalPieceHeight = pieceHeight;
//            final int finalY = y;
//
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//
//                    piece1.setX(finalStartingPointX);
//                    piece1.setY(finalY);
//                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
//                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
//                    piece1.setSize(finalPieceWidth, finalPieceHeight);
//                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
//                    piece1.invalidate();
//                    piece1.requestLayout();
//                    synchronized (this) {
//                        this.notify();
//                    }
//                }
//            };
//            synchronized (runnable) {
//
////                Handler handler = new Handler(mGame.context.getMainLooper());
//                ((Activity) mGame.context).runOnUiThread(runnable);
////                ((Activity) mGame.context).runOnUiThread(runnable);
//                startingPointX -= pieceWidth;
//                try {
//                    runnable.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


//    void addPiece(final LudoPiece piece) {
//
////        Point oldCoordinate = new Point();
////        oldCoordinate.x = (int) piece.x;
////        oldCoordinate.y = (int) piece.y;
//        piece.mBox = this;
//        mPieces.add(piece);
//        mPieceCount++;
//        float scale = 1f;
//
//
//        for (int i = 1; i < mPieceCount; i++) {
////            pieceHeight = pieceHeight - pieceHeight / 6;
////            pieceWidth = pieceHeight / 2;
//            scale = scale * (5f/6f);
//        }
//
//        final int pieceHeight = (int) (mSize*scale);
//        int pieceWidth = pieceHeight / 2;
//
//        int x = mCenterPoint.x;
//        int y = (int) (mCenterPoint.y + (mSize*scale) / 8);
//        x -= (pieceWidth) / 2;
//        y -= pieceHeight;
//
//        x += (mPieceCount - 1) * (pieceWidth) / 2;
//        int startingPointX = x;
//
//        for (int i = 0; i < mPieceCount; i++) {
//            final LudoPiece piece1 = mPieces.get(i);
//
//            final int finalStartingPointX = startingPointX;
//            final int finalPieceWidth = pieceWidth;
//            final int finalPieceHeight = pieceHeight;
//            final int finalY = y;
//            final float finalScale = scale;
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//
////                    piece1.setTranslationX(finalStartingPointX);
////                    piece1.setTranslationY(finalY);
//                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
//                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
////                    piece1.setSize(finalPieceWidth, finalPieceHeight);
//                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", finalScale).setDuration(100);
//                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", finalScale).setDuration(100);
//                    ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX).setDuration(100);
//                    ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY).setDuration(100);
//
//                    AnimatorSet animatorSet = new AnimatorSet();
//                    animatorSet.setInterpolator(new LinearInterpolator());
//                    animatorSet.playTogether(translationX, translationY, scaleX, scaleY);
//                    animatorSet.start();
//
//                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
////                    piece1.invalidate();
////                    piece1.requestLayout();
//                    synchronized (this) {
//                        this.notify();
//                    }
//                }
//            };
//            synchronized (runnable) {
//
////                Handler handler = new Handler(mGame.context.getMainLooper());
//                ((Activity) mGame.context).runOnUiThread(runnable);
////                ((Activity) mGame.context).runOnUiThread(runnable);
//                startingPointX -= pieceWidth;
//                try {
//                    runnable.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    void addPieceInitially(final LudoPiece piece) {

//        Point oldCoordinate = new Point();
//        oldCoordinate.x = (int) piece.x;
//        oldCoordinate.y = (int) piece.y;
        piece.mBox = this;
        mPieces.add(piece);
        mPieceCount++;

        int pieceHeight = mSize;
//        int pieceWidth = pieceHeight / 2;
        int pieceWidth = mSize;

        for (int i = 1; i < mPieceCount; i++) {
            pieceHeight = pieceHeight - pieceHeight / 6;
//            pieceWidth = pieceHeight / 2;
            pieceWidth = pieceHeight;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y + pieceHeight / 8;
        x -= pieceWidth / 2;
        y -= pieceHeight;

        x += (mPieceCount - 1) * (pieceWidth / 2);
        int startingPointX = x;

        for (int i = 0; i < mPieceCount; i++) {
            final LudoPiece piece1 = mPieces.get(i);

            final int finalStartingPointX = startingPointX;
            final int finalPieceWidth = pieceWidth;
            final int finalPieceHeight = pieceHeight;
            final int finalY = y;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    piece1.setX(finalStartingPointX);
                    piece1.setY(finalY);
                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
                    piece1.setSize(finalPieceWidth, finalPieceHeight);
                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));

//                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", 5f).setDuration(100);
//                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", 5f).setDuration(100);
//                    ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX).setDuration(100);
//                    ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY).setDuration(100);

//                    AnimatorSet animatorSet = new AnimatorSet();
//                    animatorSet.setInterpolator(new LinearInterpolator());
//                    animatorSet.playTogether(translationX, translationY, scaleX, scaleY);
//                    animatorSet.start();


//                    piece1.invalidate();
//                    piece1.requestLayout();
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (runnable) {

//                Handler handler = new Handler(mGame.context.getMainLooper());
                ((Activity) mGame.context).runOnUiThread(runnable);
//                ((Activity) mGame.context).runOnUiThread(runnable);
                startingPointX -= pieceWidth;
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void addPiece(final LudoPiece piece) {

//        Point oldCoordinate = new Point();
//        oldCoordinate.x = (int) piece.x;
//        oldCoordinate.y = (int) piece.y;
        piece.mBox = this;
        mPieces.add(piece);
        mPieceCount++;

        int pieceHeight = mSize;
        int pieceWidth = pieceHeight;// / 2;

        for (int i = 1; i < mPieceCount; i++) {
            pieceHeight = pieceHeight - pieceHeight / 6;
            pieceWidth = pieceHeight;// / 2;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y;// + pieceHeight / 8;
        x -= pieceWidth / 2;
        y -= pieceHeight;

//        x += (mPieceCount - 1) * (pieceWidth / 2);
        x += (mPieceCount - 1) * ((pieceWidth * 5 / 6)/2);
        int startingPointX = x;

        final AnimatorSet[] animatorSets = new AnimatorSet[mPieceCount];

        for (int i = 0; i < mPieceCount; i++) {

            final LudoPiece piece1 = mPieces.get(i);
            final int finalStartingPointX = startingPointX;
            final int finalPieceWidth = pieceWidth;
            final int finalPieceHeight = pieceHeight;
            final int finalY = y;
            final int finalI = i;
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//
////                    piece1.setX(finalStartingPointX);
////                    piece1.setY(finalY);
////                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
////                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
//////                    piece1.setSize(finalPieceWidth, finalPieceHeight);
////                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
//
////                    float fWidth = finalPieceWidth;
////                    float fHeight = finalPieceHeight;
////                    float fSize = mSize;
////
////                    int duration = 2000;
////                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", fHeight/fSize);scaleX.setDuration(duration);
////                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", fHeight/fSize);scaleY.setDuration(duration);
////                    ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX);translationX.setDuration(duration);
////                    ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY);translationY.setDuration(duration);
////
////                    animatorSets[finalI] = new AnimatorSet();
////                    animatorSets[finalI].setInterpolator(new BounceInterpolator());
////                    animatorSets[finalI].playTogether(translationX, translationY, scaleX, scaleY);
////                    animatorSets[finalI].start();
//
//
////                    piece1.invalidate();
////                    piece1.requestLayout();
//                    synchronized (this) {
//                        this.notify();
//                    }
//                }
//            };
//            synchronized (runnable) {
//
////                Handler handler = new Handler(mGame.context.getMainLooper());
//                ((Activity) mGame.context).runOnUiThread(runnable);
////                ((Activity) mGame.context).runOnUiThread(runnable);
//                startingPointX -= (pieceWidth * 5 / 6) / 2;
//                try {
//                    runnable.wait();
//            } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            float fWidth = finalPieceWidth;
            float fHeight = finalPieceHeight;
            float fSize = mSize;
            int duration = 2000;
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", fHeight/fSize);scaleX.setDuration(duration);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", fHeight/fSize);scaleY.setDuration(duration);
            ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX);translationX.setDuration(duration);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY);translationY.setDuration(duration);
            scaleX.setInterpolator(new BounceInterpolator());
            scaleY.setInterpolator(new BounceInterpolator());
            translationX.setInterpolator(new FastOutSlowInInterpolator());
            translationY.setInterpolator(new FastOutSlowInInterpolator());
            animatorSets[finalI] = new AnimatorSet();
//            animatorSets[finalI].setInterpolator(new BounceInterpolator());
            animatorSets[finalI].playTogether(translationX, translationY, scaleX, scaleY);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    piece1.bringToFront();
                }
            });

            handler.post(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
                    //                    piece1.setSize(finalPieceWidth, finalPieceHeight);
                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
                }
            });

            startingPointX -= (pieceWidth * 5 / 6) / 2;

        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorSets);
                animatorSet.start();
            }
        });
    }

//    void removePiece(LudoPiece piece) {
//        mPieces.remove(piece);
//        mPieceCount--;
//
//        int pieceHeight = mSize;
//        int pieceWidth = pieceHeight / 2;
//
//        for (int i = 1; i < mPieceCount; i++) {
//            pieceHeight = pieceHeight - pieceHeight / 6;
//            pieceWidth = pieceHeight / 2;
//        }
//
//        int x = mCenterPoint.x;
//        int y = mCenterPoint.y + pieceHeight / 8;
//        x -= pieceWidth / 2;
//        y -= pieceHeight;
//
//        x += (mPieceCount - 1) * (pieceWidth / 2);
//
//        int startingPointX = x;
//
//        for (int i = 0; i < mPieceCount; i++) {
//            final LudoPiece piece1 = mPieces.get(i);
//            final int finalPieceWidth = pieceWidth;
//            final int finalPieceHeight = pieceHeight;
//            final int finalStartingPointX = startingPointX;
//            final int finalY = y;
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    piece1.setX(finalStartingPointX);
//                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
//                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
//                    piece1.setSize(finalPieceWidth, finalPieceHeight);
//                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
//                    piece1.setY(finalY);
//                    synchronized (this) {
//                        this.notify();
//                    }
//                }
//            };
//            synchronized (runnable) {
////                Handler handler = new Handler(mGame.context.getMainLooper());
//                ((Activity) mGame.context).runOnUiThread(runnable);
////                ((Activity) mGame.context).runOnUiThread(runnable);
//                try {
//                    runnable.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            startingPointX -= pieceWidth;
//        }
//    }

    void removePiece(LudoPiece piece) {
        mPieces.remove(piece);
        mPieceCount--;

        int pieceHeight = mSize;
        int pieceWidth = pieceHeight;// / 2;

        for (int i = 1; i < mPieceCount; i++) {
            pieceHeight = pieceHeight - pieceHeight / 6;
            pieceWidth = pieceHeight;// / 2;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y;// + pieceHeight / 8;
        x -= pieceWidth / 2;
        y -= pieceHeight;

//        x += (mPieceCount - 1) * (pieceWidth / 2);
        x += (mPieceCount - 1) * ((pieceWidth* 5 / 6)/2);

        int startingPointX = x;

//        for (int i = 0; i < mPieceCount; i++) {
//            final LudoPiece piece1 = mPieces.get(i);
//            final int finalPieceWidth = pieceWidth;
//            final int finalPieceHeight = pieceHeight;
//            final int finalStartingPointX = startingPointX;
//            final int finalY = y;
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
////                    piece1.setX(finalStartingPointX);
//                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
//                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
////                    piece1.setSize(finalPieceWidth, finalPieceHeight);
//
//                    float fHeight = finalPieceHeight;
//                    float fSize = mSize;
//                    int duration = 2000;
//                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", fHeight/fSize);scaleX.setDuration(duration);
//                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", fHeight/fSize);scaleY.setDuration(duration);
//                    ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX);translationX.setDuration(duration);
//                    ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY);translationY.setDuration(duration);
//
//                    AnimatorSet animatorSet = new AnimatorSet();
//                    animatorSet.setInterpolator(new BounceInterpolator());
//                    animatorSet.playTogether(translationX, translationY, scaleX, scaleY);
//                    animatorSet.start();
//
//                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
////                    piece1.setY(finalY);
//                    synchronized (this) {
//                        this.notify();
//                    }
//                }
//            };
//            synchronized (runnable) {
////                Handler handler = new Handler(mGame.context.getMainLooper());
//                ((Activity) mGame.context).runOnUiThread(runnable);
////                ((Activity) mGame.context).runOnUiThread(runnable);
//                try {
//                    runnable.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            startingPointX -= (pieceWidth * 5 / 6)/2;
//        }

        if(mPieceCount != 0) {
            final AnimatorSet[] animatorSets = new AnimatorSet[mPieceCount];

            for (int i = 0; i < mPieceCount; i++) {

                final LudoPiece piece1 = mPieces.get(i);
                final int finalStartingPointX = startingPointX;
                final int finalPieceWidth = pieceWidth;
                final int finalPieceHeight = pieceHeight;
                final int finalY = y;
                final int finalI = i;
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//
////                    piece1.setX(finalStartingPointX);
////                    piece1.setY(finalY);
////                    ((ImageView) piece1.getTag()).setX(finalStartingPointX);
////                    ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
//////                    piece1.setSize(finalPieceWidth, finalPieceHeight);
////                    ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
//
////                    float fWidth = finalPieceWidth;
////                    float fHeight = finalPieceHeight;
////                    float fSize = mSize;
////
////                    int duration = 2000;
////                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", fHeight/fSize);scaleX.setDuration(duration);
////                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", fHeight/fSize);scaleY.setDuration(duration);
////                    ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX);translationX.setDuration(duration);
////                    ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY);translationY.setDuration(duration);
////
////                    animatorSets[finalI] = new AnimatorSet();
////                    animatorSets[finalI].setInterpolator(new BounceInterpolator());
////                    animatorSets[finalI].playTogether(translationX, translationY, scaleX, scaleY);
////                    animatorSets[finalI].start();
//
//
////                    piece1.invalidate();
////                    piece1.requestLayout();
//                    synchronized (this) {
//                        this.notify();
//                    }
//                }
//            };
//            synchronized (runnable) {
//
////                Handler handler = new Handler(mGame.context.getMainLooper());
//                ((Activity) mGame.context).runOnUiThread(runnable);
////                ((Activity) mGame.context).runOnUiThread(runnable);
//                startingPointX -= (pieceWidth * 5 / 6) / 2;
//                try {
//                    runnable.wait();
//            } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
                float fWidth = finalPieceWidth;
                float fHeight = finalPieceHeight;
                float fSize = mSize;
                int duration = 2000;
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(piece1, "scaleX", fHeight / fSize);
                scaleX.setDuration(duration);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(piece1, "scaleY", fHeight / fSize);
                scaleY.setDuration(duration);
                ObjectAnimator translationX = ObjectAnimator.ofFloat(piece1, "translationX", finalStartingPointX);
                translationX.setDuration(duration);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(piece1, "translationY", finalY);
                translationY.setDuration(duration);
                scaleX.setInterpolator(new BounceInterpolator());
                scaleY.setInterpolator(new BounceInterpolator());
                translationX.setInterpolator(new FastOutSlowInInterpolator());
                translationY.setInterpolator(new FastOutSlowInInterpolator());
                animatorSets[finalI] = new AnimatorSet();
//            animatorSets[finalI].setInterpolator(new BounceInterpolator());
                animatorSets[finalI].playTogether(translationX, translationY, scaleX, scaleY);

//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((ImageView) piece1.getTag()).setX(finalStartingPointX);
//                        ((ImageView) piece1.getTag()).setY(mCenterPoint.y - finalPieceWidth / 2);
//                        //                    piece1.setSize(finalPieceWidth, finalPieceHeight);
//                        ((ImageView) piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(finalPieceWidth, finalPieceWidth));
//                    }
//                });

                startingPointX -= (pieceWidth * 5 / 6) / 2;
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animatorSets);
                    animatorSet.start();
                }
            });

        }

    }

    public int getPieceHeight() {
        return defaultHeight;
    }

    public int getPieceWidth() {
        return defaultWidth;
    }

    public enum TransitionPlayer {
        ONE, TWO, THREE, FOUR, NULL
    }

}
