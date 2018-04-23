package com.example.apple.ludochallenge;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.widget.FrameLayout;

import java.util.ArrayList;

import static android.view.View.VISIBLE;
import static com.example.apple.ludochallenge.SALGame.translateAnimation;

/**
 * Created by Taha Malik on 4/14/2018.
 **/

public class Player {

    public final static int PLAYER_1 = 1;
    public final static int PLAYER_2 = 2;
    public final static int PLAYER_3 = 3;
    public final static int PLAYER_4 = 4;

    private SALGame mGame;
    private int player;
    private Piece mPiece;
    PlayerType type;

    Player(SALGame game, int player, Piece piece, PlayerType playerType) {
        mPiece = piece;
        this.player = player;
        mGame = game;
        type = playerType;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public Piece getmPiece() {
        return mPiece;
    }

    public void setmPiece(Piece mPiece) {
        this.mPiece = mPiece;
    }

    public void move(final int num) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                ((Activity) mGame.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPiece.mBox.removePiece(mPiece);
                    }
                });

                final int fromBox = mPiece.mBox.getmBoxNum();

                Point to;
                Point from;
                for (int i = 0; i < num; i++) {
                    from = mGame.getBoxAtIndex(fromBox + i).firstPiece;
                    to = mGame.getBoxAtIndex(fromBox + i + 1).firstPiece;
                    animatePiece(mPiece, from, to);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ((Activity) mGame.context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPiece.invalidate();
                        }
                    });

                }
                final Box[] finalBox = new Box[1];
                finalBox[0] = mGame.getBoxAtIndex(fromBox + num);
                if (finalBox[0].transition) {
                    boolean ladder = finalBox[0].getmBoxNum() <= finalBox[0].getmToBox();
                    ArrayList<Float> xValues = ladder ? mGame.getLaddersX().get(finalBox[0].snakeOrLadderNo) : mGame.getSnakesX().get(finalBox[0].snakeOrLadderNo);
                    ArrayList<Float> yValues = ladder ? mGame.getLaddersY().get(finalBox[0].snakeOrLadderNo) : mGame.getSnakesY().get(finalBox[0].snakeOrLadderNo);

                    final float[] floatsX = new float[xValues.size()];
                    final float[] floatsY = new float[yValues.size()];

                    int i = 0;

                    float error = 1024 - mGame.size;
                    error = mGame.size / error;

                    float gameY = mGame.getY();

                    for (Float f : xValues) {
                        float e = (f / (error + 1));
                        floatsX[i] = f - e;
                        floatsX[i++] -= finalBox[0].getPieceWidth() / 2;
                    }
                    i = 0;

                    for (Float f : yValues) {
                        float e = (f / (error + 1));
                        floatsY[i] = f - e + gameY;
                        floatsY[i++] -= finalBox[0].getPieceHeight();
                    }

                    if (ladder) {
                        for (int j = 0; (j < floatsX.length - 1 && j < floatsY.length - 1); j++) {
                            final int finalJ = j;
                            ((Activity) mGame.context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    climbLadder(mPiece, floatsX[finalJ], floatsX[finalJ + 1], floatsY[finalJ], floatsY[finalJ + 1]);
                                }
                            });
                            try {
                                Thread.sleep(170);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        final int[] time = new int[1];
                        float distX = (floatsX[0] - floatsX[floatsX.length - 1]) < 0 ? (floatsX[0] - floatsX[floatsX.length - 1]) * -1 : (floatsX[0] - floatsX[floatsX.length - 1]);
                        float distY = (floatsY[0] - floatsY[floatsY.length - 1]) < 0 ? (floatsY[0] - floatsY[floatsY.length - 1]) * -1 : (floatsY[0] - floatsY[floatsY.length - 1]);

                        float maxDist = distX < distY ? distY : distX;

                        time[0] = (int) (maxDist / 100);

                        ((Activity) mGame.context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                snakeBite(mPiece, floatsX, floatsY, time[0]);
                            }
                        });
                        try {
                            Thread.sleep(400 * time[0]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    finalBox[0] = mGame.getBoxAtIndex(finalBox[0].getmToBox());
                }

                ((Activity) mGame.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGame.currentPlayer++;
                        mGame.currentPlayer %=mGame.numberOfPlayers;
                        finalBox[0].addPiece(mPiece);
                        mGame.getDiceImage().setX(mGame.dicePoints[mGame.currentPlayer].x);
                        mGame.getDiceImage().setY(mGame.dicePoints[mGame.currentPlayer].y);
                        mGame.getDiceImage().setEnabled(true);
//                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setAnimation(null);
//                                arrows[(currentPlayer+numberOfPlayers-1)%numberOfPlayers].setVisibility(INVISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setVisibility(VISIBLE);
                        mGame.getmArrows()[mGame.currentPlayer].setAnimation(translateAnimation);

                        if(mGame.players.get(mGame.currentPlayer).type == PlayerType.CPU)
                        {
                            mGame.getDiceImage().performClick();
                        }

                    }
                });


            }
        });
        thread.start();

    }

    private void animatePiece(final Piece piece, final Point from, final Point to) {

        ValueAnimator animator = null;
        ValueAnimator animator1 = null;

        final int pieceWidth = piece.mBox.getPieceWidth();
        final int pieceHeight = piece.mBox.getPieceHeight();

        if (from.x != to.x) {
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

    private void climbLadder(final Piece piece, float fromX, final float toX, float fromY, final float toY) {

        final int width = piece.mBox.getPieceWidth() / 4;
        final int height = piece.mBox.getPieceHeight() / 4;

        final float intermidiateX = (fromX - (fromX - toX));


        final ValueAnimator animatorX = ValueAnimator.ofFloat(fromX, toX);
        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                piece.setTranslationX((float) animation.getAnimatedValue());
            }
        });
        ValueAnimator animatorY = ValueAnimator.ofFloat(fromY, toY);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                piece.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        animatorX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                piece.setLayoutParams(new FrameLayout.LayoutParams(piece.mBox.getPieceWidth() + piece.mBox.getPieceWidth()/4, piece.mBox.getPieceHeight() + piece.mBox.getPieceHeight()/4));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                piece.setLayoutParams(new FrameLayout.LayoutParams(piece.mBox.getPieceWidth() - piece.mBox.getPieceWidth()/4, piece.mBox.getPieceHeight() - piece.mBox.getPieceHeight()/4));
//                piece.setTranslationX(toX-width);
//                piece.setTranslationY(toY-height);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                piece.setLayoutParams(new FrameLayout.LayoutParams(piece.mBox.getPieceWidth() - piece.mBox.getPieceWidth()/4, piece.mBox.getPieceHeight() - piece.mBox.getPieceHeight()/4));
//                piece.setTranslationX(toX-width);
//                piece.setTranslationY(toY-height);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        ValueAnimator animatorZ1 = ValueAnimator.ofFloat(width+width/4, 2*width + width/2);
//        animatorZ1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                piece.setTranslationX(piece.getX() - (float)animation.getAnimatedValue());
//            }
//        });
//
//        animatorZ1.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                ValueAnimator animatorZ2 = ValueAnimator.ofFloat(2*width + width/2, width + width/4);
//                animatorZ2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        piece.setTranslationX(piece.getX() - (float)animation.getAnimatedValue());
//                    }
//                });
//                animatorZ2.setDuration(100);
//                animatorZ2.start();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//
//        ValueAnimator animatorZ3 = ValueAnimator.ofFloat(width + width/4,2*width + width/2);
//        animatorZ3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                piece.setTranslationX(piece.getX() + (float)animation.getAnimatedValue());
//            }
//        });

//        animatorZ3.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                ValueAnimator animatorZ4 = ValueAnimator.ofFloat(2*width + width/2,width + width/4);
//                animatorZ4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        piece.setTranslationX(piece.getX() + (float)animation.getAnimatedValue());
//                    }
//                });
//                animatorZ4.setDuration(100);
//                animatorZ4.start();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        animatorZ1.setDuration(100);
//        animatorZ3.setDuration(100);
        animatorX.setDuration(130);
        animatorY.setDuration(130);
        animatorX.start();
        animatorY.start();
//        if(fromX < toX )animatorZ1.start();
//        else animatorZ3.start();

    }

    private void snakeBite(final Piece piece, float[] xFloats, float[] yFloats, int time) {
        ValueAnimator animatorX = ValueAnimator.ofFloat(xFloats);

        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                piece.setTranslationX((float) animation.getAnimatedValue());
            }
        });

        ValueAnimator animatorY = ValueAnimator.ofFloat(yFloats);
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                piece.setTranslationY((float) animation.getAnimatedValue());
            }
        });

        animatorX.setDuration(350 * time);
        animatorY.setDuration(350 * time);
        animatorX.start();
        animatorY.start();

    }
}
