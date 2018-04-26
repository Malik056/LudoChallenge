package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import static android.view.View.VISIBLE;
import static android.view.View.combineMeasuredStates;
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
                if (ludoBox.mPieceCount > 2) {

                    if (!ludoBox.stop) {

                        int opponent = 0;
                        int mine = -1;
                        final LudoPiece[] ludoPieces = new LudoPiece[4];
                        for (LudoPiece l : ludoBox.mPieces) {
                            if (l.player.player != piece.player.player) {
                                opponent++;
                            } else {
                                if (l != piece)
                                    ludoPieces[++mine] = l;
                            }
                        }

                        if (opponent == mine) {
                            for (int i = 0; i < mine; i++) {

                                final int finalI = i;

                                Thread thread1 = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LudoPiece ludoPiece = ludoPieces[finalI];
                                        killPiece(ludoPiece);
                                    }
                                });
                                thread1.start();

                            }
                            LudoGame.turnChange = false;
                        }
                    }
                }

                ludoBox.removePiece(piece);

                LudoBox fromBox = piece.mBox;
                Point to = new Point();
                Point from = new Point();
                final LudoBox[] finalBox = new LudoBox[1];

                for (int i = 0; i < num; i++) {

                    from.x = fromBox.firstX;
                    from.y = fromBox.firstY;

                    if (player == fromBox.transitionPlayer) {
                        to.y = fromBox.transitionBox.firstY;
                        to.x = fromBox.transitionBox.firstX;
                        finalBox[0] = fromBox.transitionBox;

                    } else {
                        to.y = fromBox.nextBox.firstY;
                        to.x = fromBox.nextBox.firstX;
                        finalBox[0] = fromBox.nextBox;
                    }

                    animatePiece(piece, from, to);

                    try {
                        Thread.sleep(350);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    fromBox = finalBox[0];
                }

                if (finalBox[0].winBox) {
                    LudoGame.turnChange = false;

                    if (finalBox[0].mPieceCount == 4) {
                        player_won();
                    }
                } else if (finalBox[0].mPieceCount > 0) {
                    if (!finalBox[0].stop) {
                        int opponent = 0;
                        int mine = 1;
                        final LudoPiece[] ludoPieces = {null, null, null, null};
                        for (LudoPiece l : finalBox[0].mPieces) {
                            if (l.player.player != piece.player.player) {
                                ludoPieces[opponent++] = l;
                            } else {
                                mine++;
                            }
                        }

                        if (opponent == mine) {

                            final LudoPiece[] ludoPiece = {ludoPieces[0]};

                            ludoBox = finalBox[0];

                            int toIndex = mGame.boxes.indexOf(ludoPiece[0].startPosition.nextBox);
                            int fromIndex = mGame.boxes.indexOf(ludoBox);

                            int n = (52 + (fromIndex - toIndex)) % 52;
                            n += 2;

                            for (int i = 0; i < opponent; i++) {

                                ludoPiece[0] = ludoPieces[i];
                                finalBox[0].removePiece(ludoPiece[0]);

                                Thread thread1 = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        killPiece(ludoPiece[0]);
                                    }
                                });
                                thread1.start();
                            }
                            LudoGame.turnChange = false;

                            try {
                                Thread.sleep(n * 103 + (opponent * 10));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finalBox[0].addPiece(piece);

                            ((Activity) mGame.context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGame.invalidate();
                                }
                            });
                        } else {
                            finalBox[0].addPiece(piece);
                        }
                    } else {
                        finalBox[0].addPiece(piece);
                    }
                } else {
                    finalBox[0].addPiece(piece);
                }

                ((Activity) mGame.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (LudoGame.turnChange) {
                            mGame.currentPlayer++;
                            mGame.currentPlayer %= mGame.numberOfPlayers;
                        } else LudoGame.turnChange = true;

                        mGame.getDiceImage().setX(mGame.dicePoints[mGame.currentPlayer].x);
                        mGame.getDiceImage().setY(mGame.dicePoints[mGame.currentPlayer].y);
                        mGame.getDiceImage().setEnabled(true);
//                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setAnimation(null);
//                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setVisibility(INVISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setVisibility(VISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setAnimation(translateAnimation);

                        if (mGame.players.get(mGame.currentPlayer).type == PlayerType.CPU) {
                            mGame.getDiceImage().performClick();
                        } else if (mGame.players.get(mGame.currentPlayer).type == PlayerType.ONLINE) {
                        }
                    }
                });

            }
        });
        thread.start();
    }

    public void move1(final int num, final LudoPiece piece) {
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

                currentBox.addPiece(piece);

                if (currentBox.mPieceCount > 0) {
                    if (!currentBox.stop) {
                        int opponent = 0;
                        int mine = 0;
                        final LudoPiece[] ludoPieces = {null, null, null, null};
                        for (LudoPiece l : currentBox.mPieces) {
                            if (l.player.player != piece.player.player) {
                                ludoPieces[opponent++] = l;
                            } else {
                                mine++;
                            }
                        }
                        if (mine == opponent) {
                            for (int i = 0; i < opponent; i++) {
                                killPieces(ludoPieces);
                            }
                        }
                    }
                }

                ((Activity) mGame.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(mGame.context, "animated", Toast.LENGTH_SHORT).show();
                        if (LudoGame.turnChange) {
                            mGame.currentPlayer++;
                            mGame.currentPlayer %= mGame.numberOfPlayers;
                        } else LudoGame.turnChange = true;

                        mGame.getDiceImage().setX(mGame.dicePoints[mGame.currentPlayer].x);
                        mGame.getDiceImage().setY(mGame.dicePoints[mGame.currentPlayer].y);
                        mGame.getDiceImage().setEnabled(true);
                        //                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setAnimation(null);
                        //                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setVisibility(INVISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setVisibility(VISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setAnimation(translateAnimation);
                    }
                });
            }

        });
        thread.start();
    }

    private void player_won() {
        ((Activity) mGame.context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mGame.context, "Player Won", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void animatePiece(final LudoPiece piece, final Point from, final Point to) {

        ValueAnimator animator = null;
        ValueAnimator animator1 = null;

        final int pieceWidth = piece.mBox.getPieceWidth();
        final int pieceHeight = piece.mBox.getPieceHeight();

        if (from.x != to.x && from.y != to.y) {

            final Runnable runnable = new Runnable() {
                final Runnable runnable1 = this;

                @Override
                public void run() {
                    piece.animate().translationX(to.x).setDuration(160);
                    piece.animate().translationY(to.y).setDuration(160).setListener(new Animator.AnimatorListener() {
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

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            };
            synchronized (runnable) {
                ((Activity) mGame.context).runOnUiThread(runnable);
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (from.x != to.x) {
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

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    AnimatorSet animatorSet = new AnimatorSet();
                    finalAnimator1.setDuration(160);
                    finalAnimator.setDuration(75);
                    final Runnable runnable1 = this;
                    animatorSet.addListener(new Animator.AnimatorListener() {
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
                    animatorSet.playTogether(finalAnimator, finalAnimator1);
                    animatorSet.start();

                }
            };
            synchronized (runnable) {
                ((Activity) mGame.context).runOnUiThread(runnable);
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void killPiece(final LudoPiece piece) {
        LudoBox ludoBox = piece.mBox;
        ludoBox.removePiece(piece);
        final LudoBox finalLudoBox = ludoBox;

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                piece.setLayoutParams(new FrameLayout.LayoutParams(finalLudoBox.defaultWidth, finalLudoBox.defaultHeight));
                piece.open = false;
                synchronized (this) {
                    this.notify();
                }
            }
        };

        synchronized (runnable) {
            ((Activity) mGame.context).runOnUiThread(runnable);
            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Point from = new Point();
        final Point to = new Point();

        while (ludoBox != piece.startPosition.nextBox) {

            from.x = ludoBox.firstX;
            from.y = ludoBox.firstY;
            to.x = ludoBox.previousBox.firstX;
            to.y = ludoBox.previousBox.firstY;

            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {

                    final Runnable runnable2 = this;
                    piece.animate().translationY(to.y).setDuration(100).start();
                    piece.animate().translationX(to.x).setDuration(100).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            synchronized (runnable2) {
                                runnable2.notify();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }
            };
            synchronized (runnable1) {
                ((Activity) mGame.context).runOnUiThread(runnable1);
                try {
                    runnable1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ludoBox = ludoBox.previousBox;

        }

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                final Runnable runnable2 = this;
                piece.animate().translationX(piece.startPosition.firstX).setDuration(200).start();
                piece.animate().translationY(piece.startPosition.firstY).setDuration(200).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        synchronized (runnable2) {
                            runnable2.notify();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }
        };
        synchronized (runnable1) {
            ((Activity) mGame.context).runOnUiThread(runnable1);
            try {
                runnable1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        piece.startPosition.addPiece(piece);
    }

    private void killPieces(final LudoPiece[] pieces) {
        final LudoPiece piece = pieces[0];

        LudoBox ludoBox = piece.mBox;

        for(LudoPiece p:pieces)
            if(p!=null)
                ludoBox.removePiece(p);

        final LudoBox finalLudoBox = ludoBox;

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for (LudoPiece piece1 : pieces) {
                    if (piece1 != null) {
                        piece1.setLayoutParams(new FrameLayout.LayoutParams(finalLudoBox.defaultWidth, finalLudoBox.defaultHeight));
                        piece1.open = false;
                    }
                }
                synchronized (this) {
                    this.notify();
                }
            }
        };

        synchronized (runnable) {
            ((Activity) mGame.context).runOnUiThread(runnable);
            try {
                runnable.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Point from = new Point();
        final Point to = new Point();

        while (ludoBox != piece.startPosition.nextBox) {
            if (ludoBox.previousBox != null) {
                from.x = ludoBox.firstX;
                from.y = ludoBox.firstY;
                to.x = ludoBox.previousBox.firstX;
                to.y = ludoBox.previousBox.firstY;
                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {

                        final Runnable runnable2 = this;
                        for (int i = 1; i < pieces.length; i++) {
                            if (pieces[i] != null) {
                                pieces[i].animate().translationY(to.y).setDuration(100).start();
                                pieces[i].animate().translationX(to.x).setDuration(100).start();
                            }
                        }
                        pieces[0].animate().translationY(to.y).setDuration(100).start();
                        pieces[0].animate().translationX(to.x).setDuration(100).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                synchronized (runnable2) {
                                    runnable2.notify();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                    }
                };
                synchronized (runnable1) {
                    ((Activity) mGame.context).runOnUiThread(runnable1);
                    try {
                        runnable1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ludoBox = ludoBox.previousBox;
            }
        }

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                final Runnable runnable2 = this;

                for (int i = 1; i < pieces.length; i++) {
                    if (pieces[i] != null) {
                        pieces[i].animate().translationX(pieces[i].startPosition.firstX).setDuration(200).start();
                        pieces[i].animate().translationY(pieces[i].startPosition.firstY).setDuration(200).start();
                    }
                }

                pieces[0].animate().translationX(pieces[0].startPosition.firstX).setDuration(200).start();
                pieces[0].animate().translationY(pieces[0].startPosition.firstY).setDuration(200).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        synchronized (runnable2) {
                            runnable2.notify();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();

            }
        };
        synchronized (runnable1) {
            ((Activity) mGame.context).runOnUiThread(runnable1);
            try {
                runnable1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        for (LudoPiece piece1 : pieces)
            if (piece1 != null) {
                piece1.startPosition.addPiece(piece1);
            }
    }
}
