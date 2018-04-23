package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import static android.view.View.VISIBLE;
import static com.example.apple.ludochallenge.SALGame.translateAnimation;

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

                LudoBox ludoBox = piece.mBox;

                ludoBox.removePiece(piece);

                if(ludoBox.mPieceCount > 1) {
                    if (!ludoBox.stop) {

                        int opponent = 0;
                        int mine = 0;
                        int[] index = {0, 0, 0, 0};
                        for (LudoPiece l : ludoBox.mPieces) {
                            if (l.player.player != piece.player.player) {
                                index[opponent++] = ludoBox.mPieces.indexOf(l);
                            } else {
                                mine++;
                            }
                        }

                        if(opponent == mine)
                        {

                            for(int i = 0; i < opponent; i++) {

                                LudoPiece ludoPiece = ludoBox.mPieces.get(index[i]);
                                killPiece(ludoPiece);
                            }

                            LudoGame.turnChange = false;

                        }
                    }
                }
                LudoBox fromBox = piece.mBox;
                Point to = new Point();
                Point from = new Point();
                final LudoBox[] finalBox = new LudoBox[1];

                for (int i = 0; i < num; i++) {

                    from.x = fromBox.firstX;
                    from.y = fromBox.firstY;

                    if(player == fromBox.transitionPlayer)
                    {
                        to.y = fromBox.transitionBox.firstY;
                        to.x = fromBox.transitionBox.firstX;
                        finalBox[0] = fromBox.transitionBox;

                    }
                    else {
                        to.y = fromBox.nextBox.firstY;
                        to.x = fromBox.nextBox.firstX;
                        finalBox[0] = fromBox.nextBox;
                    }
                    animatePiece(piece, from, to);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    fromBox = finalBox[0];
                }

                finalBox[0].addPiece(piece);
                if(finalBox[0].winBox)
                {
                    LudoGame.turnChange = false;

                    if(finalBox[0].mPieceCount == 4)
                    {
                        player_won();
                    }
                }
                else if(finalBox[0].mPieceCount > 1)
                {
                    if(!finalBox[0].stop)
                    {
                        int opponent = 0;
                        int mine = 0;
                        int[] index = {0,0,0,0};
                        for(LudoPiece l:finalBox[0].mPieces)
                        {
                            if(l.player.player != piece.player.player)
                            {
                                index[opponent++] = finalBox[0].mPieces.indexOf(l);
                            }
                            else
                            {
                                mine++;
                            }
                        }

                        if(opponent == mine)
                        {
                            LudoPiece ludoPiece = finalBox[0].mPieces.get(index[0]);
                            ludoBox = finalBox[0];
                            int toIndex = mGame.boxes.indexOf(ludoPiece.startPosition.nextBox);
                            int fromIndex = mGame.boxes.indexOf(ludoBox);

                            int n = 52 + (fromIndex - toIndex) % 52;
                            n += 2;

                            for(int i = 0; i < opponent; i++) {

                                ludoPiece = finalBox[0].mPieces.get(index[i]);
                                killPiece(ludoPiece);
                            }

                            LudoGame.turnChange = false;

                            try {
                                Thread.sleep(n * 103 + (opponent * 10));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                ((Activity) mGame.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(LudoGame.turnChange) {
                            mGame.currentPlayer++;
                            mGame.currentPlayer %= mGame.numberOfPlayers;
                        }
                        else LudoGame.turnChange = true;

                        mGame.getDiceImage().setX(mGame.dicePoints[mGame.currentPlayer].x);
                        mGame.getDiceImage().setY(mGame.dicePoints[mGame.currentPlayer].y);
                        mGame.getDiceImage().setEnabled(true);
//                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setAnimation(null);
//                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setVisibility(INVISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setVisibility(VISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setAnimation(translateAnimation);

                        if (mGame.players.get(mGame.currentPlayer).type == PlayerType.CPU) {
                            mGame.getDiceImage().performClick();
                        }
                        else if(mGame.players.get(mGame.currentPlayer).type == PlayerType.ONLINE)
                        {

                        }
                    }
                });

            }
        });
        thread.start();

    }

    private void player_won() {
        ((Activity)mGame.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mGame.context,"Player Won", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animatePiece(final LudoPiece piece, final Point from, final Point to) {

        ValueAnimator animator = null;
        ValueAnimator animator1 = null;

        final int pieceWidth = piece.mBox.getPieceWidth();
        final int pieceHeight = piece.mBox.getPieceHeight();

        if(from.x != to.x && from.y != to.y) {
            ((Activity) mGame.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    piece.animate().translationX(to.x).setDuration(160);
                    piece.animate().translationY(to.y).setDuration(160);
                }
            });
        }
        else if (from.x != to.x) {
            animator = ValueAnimator.ofFloat(from.x, to.x);

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    piece.setTranslationX(((Float) animation.getAnimatedValue()));
                }
            });

            animator1 = ValueAnimator.ofFloat(from.y, from.y - piece.mBox.getPieceHeight() / 2);

            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    piece.setTranslationY(((Float) animation.getAnimatedValue()));
                }
            });

            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ValueAnimator animator2 = ValueAnimator.ofFloat(piece.getY(), from.y);
                    animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            piece.setTranslationY((Float) animation.getAnimatedValue());
                        }
                    });
                    animator2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            piece.setLayoutParams(new FrameLayout.LayoutParams(piece.mBox.getPieceWidth(), piece.mBox.getPieceHeight()));
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            piece.setLayoutParams(new FrameLayout.LayoutParams(piece.mBox.getPieceWidth(), piece.mBox.getPieceHeight()));
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator2.setDuration(75);
                    animator2.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    piece.setLayoutParams(new FrameLayout.LayoutParams((piece.mBox.getPieceWidth() + piece.mBox.getPieceWidth() / 8), piece.mBox.getPieceHeight() + piece.mBox.getPieceHeight() / 8));
                }
            });

        } else if (from.y != to.y) {

            animator = ValueAnimator.ofFloat(from.y, to.y);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    piece.setTranslationY(((Float) animation.getAnimatedValue()));
                }
            });

            animator1 = ValueAnimator.ofFloat(from.x, from.x + piece.mBox.getPieceWidth());

            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    piece.setTranslationX(((Float) animation.getAnimatedValue()));
                }
            });

            animator1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ValueAnimator animator2 = ValueAnimator.ofFloat(piece.getX(), from.x);
                    animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            piece.setTranslationX((Float) animation.getAnimatedValue());
                        }
                    });
                    animator2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            piece.setLayoutParams(new FrameLayout.LayoutParams(pieceWidth, pieceHeight));
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            piece.setLayoutParams(new FrameLayout.LayoutParams(pieceWidth, pieceHeight));
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator2.setDuration(75);
                    animator2.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    piece.setLayoutParams(new FrameLayout.LayoutParams(pieceWidth + pieceWidth / 8, pieceHeight + pieceHeight / 8));
                }
            });

        }
        if (animator != null) {

            final ValueAnimator finalAnimator = animator1;
            final ValueAnimator finalAnimator1 = animator;

            ((Activity) mGame.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalAnimator1.setDuration(160);
                    finalAnimator.setDuration(75);
                    finalAnimator1.start();
                    finalAnimator.start();

                }
            });

        }
    }

    private void killPiece(LudoPiece piece)
    {
        LudoBox ludoBox  = piece.mBox;
        ludoBox.removePiece(piece);
        piece.setLayoutParams(new FrameLayout.LayoutParams(ludoBox.defaultWidth,ludoBox.defaultHeight));

        Point from = new Point();
        Point to = new Point();

        while(ludoBox.previousBox != null) {

            from.x = ludoBox.firstX;
            from.y = ludoBox.firstY;
            to.x = ludoBox.previousBox.firstX;
            to.y = ludoBox.previousBox.firstY;

            piece.animate().translationX(to.x).setDuration(100).start();
            piece.animate().translationY(to.y).setDuration(100).start();

            ludoBox = ludoBox.previousBox;


            try {
                Thread.sleep(103);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        piece.animate().translationX(piece.startPosition.firstX).setDuration(200);
        piece.animate().translationY(piece.startPosition.firstY).setDuration(200);

        try {
            Thread.sleep(203);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        piece.startPosition.addPiece(piece);

    }

}
