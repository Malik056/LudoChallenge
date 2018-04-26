package com.example.apple.ludochallenge;

import android.content.Context;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public class Box {

    Point mCenterPoint;
    Point firstPiece;
    private SALGame mGame;
    private int mToBox;
    boolean transition;

    private int pieceHeight;
    private int pieceWidth;

    public int getPieceHeight() {
        return pieceHeight;
    }

    public int getPieceWidth() {
        return pieceWidth;
    }

    public int getmBoxNum() {
        return mBoxNum;
    }

    int mSize;
    private int mBoxNum;
    int mPieceCount;
    private Context context;
    int snakeOrLadderNo;

    ArrayList<Piece> mPieces = new ArrayList<>();

    Box(Point point, int size, int boxNum, Context context, SALGame game, int toBox, boolean transition, int num)
    {
        mCenterPoint = point;
        mPieceCount = 0;
        mSize = size;
        mBoxNum = boxNum;
        this.context = context;
        pieceHeight = size;
        pieceWidth = pieceHeight/2;
        firstPiece = new Point(point.x - pieceWidth/2, point.y + pieceHeight/8 - pieceHeight);
        mGame = game;
        mToBox = toBox;
        this.transition = transition;
        snakeOrLadderNo = num;
    }

    public int getmToBox() {
        return mToBox;
    }

    public void setmToBox(int mToBox) {
        this.mToBox = mToBox;
    }

    public boolean isTransition() {
        return transition;
    }

    public void setTransition(boolean transition) {
        this.transition = transition;
    }

    void addPiece(final Piece piece) {

        Point oldCoordinate = new Point();
        oldCoordinate.x = (int) piece.x;
        oldCoordinate.y = (int) piece.y;

        mPieces.add(piece);

        piece.mBox = this;
        mPieceCount++;
        int pieceHeight = mSize;
        int pieceWidth = pieceHeight / 2;

        for (int i = 1; i < mPieceCount; i++) {
            pieceHeight = pieceHeight - pieceHeight / 6;
            pieceWidth = pieceHeight / 2;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y + pieceHeight / 8;
        x -= pieceWidth / 2;
        y -= pieceHeight;

        x += (mPieceCount - 1) * (pieceWidth / 2);
        int startingPointX = x;

        for (int i = 0; i < mPieceCount; i++) {

            Piece piece1 = mPieces.get(i);
            piece1.setX(startingPointX);
            piece1.setSize(pieceWidth, pieceHeight);
            piece1.setY(y);
            startingPointX -= pieceWidth;

        }
    }

    void removePiece(Piece piece)
    {
        mPieces.remove(piece);
        mPieceCount--;
        int pieceHeight = mSize;
        int pieceWidth = pieceHeight / 2;

        for (int i = 1; i < mPieceCount; i++) {
            pieceHeight = pieceHeight - pieceHeight / 6;
            pieceWidth = pieceHeight / 2;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y + pieceHeight / 8;
        x -= pieceWidth / 2;
        y -= pieceHeight;

        x += (mPieceCount - 1) * (pieceWidth / 2);
        int startingPointX = x;

        for (int i = 0; i < mPieceCount; i++) {
            Piece piece1 = mPieces.get(i);
            piece1.setX(startingPointX);
            piece1.setSize(pieceWidth, pieceHeight);
            piece1.setY(y);
            startingPointX -= pieceWidth;
        }

    }

}
