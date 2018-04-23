package com.example.apple.ludochallenge;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by Taha Malik on 4/1/2018.
 **/

public class StartingBox extends Box {
    StartingBox(Point point, int size, int boxNum, Context context, SALGame game) {
        super(point, size, boxNum, context,game,-1,false, 0);
    }

    @Override
    void addPiece(Piece piece) {
//        super.addPiece(piece);
        mPieces.add(piece);
        piece.mBox = this;
        mPieceCount++;

        int pieceHeight = mSize / 2 + mSize / 3;
        int pieceWidth = 3 * mSize / 4;

        for (int i = 1; i < 4; i++) {
            pieceHeight = pieceHeight - pieceHeight / 4;
            pieceWidth = 3 * pieceHeight / 4;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y + pieceHeight / 4;
        x -= pieceWidth / 2;
        y -= pieceHeight;

        x += (mPieceCount - 1) * (pieceWidth / 2);
        int startingPointX = x;

        for (int i = 0; i < mPieceCount; i++) {
            mPieces.get(i).setX(startingPointX);
            mPieces.get(i).setSize(pieceHeight);
            mPieces.get(i).setY(y);
            startingPointX -= pieceWidth;
        }

    }

    @Override
    void removePiece(Piece piece) {
//        super.removePiece(piece);
        mPieces.remove(piece);
        mPieceCount--;
        int pieceHeight = mSize / 2 + mSize / 3;
        int pieceWidth = 3 * mSize / 4;

        for (int i = 1; i < 4; i++) {
            pieceHeight = pieceHeight - pieceHeight / 4;
            pieceWidth = 3 * pieceHeight / 4;
        }

        int x = mCenterPoint.x;
        int y = mCenterPoint.y + pieceHeight / 4;

        x -= pieceWidth / 2;
        y -= pieceHeight;

        x += (mPieceCount - 1) * (pieceWidth / 2);

        int startingPointX = x;

        for (int i = 0; i < mPieceCount; i++) {
            mPieces.get(i).setX(startingPointX);
            mPieces.get(i).setSize(pieceHeight);
            mPieces.get(i).setY(y);
            startingPointX -= pieceWidth;
        }

    }
}
