package com.example.apple.ludochallenge;

import android.graphics.Point;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Taha Malik on 4/18/2018.
 **/
public class LudoBox {


    int defaultWidth;
    int defaultHeight;
    LudoGame mGame;
    Point mCenterPoint;
    int firstX;
    int firstY;
    int mSize;
    ArrayList<LudoPiece> mPieces = new ArrayList<>();
    int mPieceCount = 0;
    LudoBox nextBox;
    LudoBox transitionBox;
    LudoBox previousBox;
    boolean stop;
    boolean winBox;
    TransitionPlayer transitionPlayer;


    LudoBox(Point mPoints, int boxSize, LudoGame game, TransitionPlayer player, LudoBox nextBox, LudoBox transitionBox, LudoBox previousBox)
    {
        mGame = game;
        mSize = boxSize;
        mCenterPoint = new Point(mPoints);
        this.nextBox = nextBox;
        this.previousBox = previousBox;
        this.transitionBox = transitionBox;
        transitionPlayer = player;
        defaultWidth = boxSize/2;
        defaultHeight = boxSize;
        firstX = mCenterPoint.x - defaultWidth/2;
        firstY = mCenterPoint.y - defaultHeight;
    }

    void addPiece(final LudoPiece piece) {

//        Point oldCoordinate = new Point();
//        oldCoordinate.x = (int) piece.x;
//        oldCoordinate.y = (int) piece.y;

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
            LudoPiece piece1 = mPieces.get(i);
            piece1.setX(startingPointX);
            piece1.setSize(pieceWidth, pieceHeight);
            piece1.setY(y);
            ((ImageView)piece1.getTag()).setX(startingPointX);
            ((ImageView)piece1.getTag()).setY(mCenterPoint.y - pieceWidth/2);
            ((ImageView)piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(pieceWidth,pieceWidth));
            startingPointX -= pieceWidth;
        }
    }

    void removePiece(LudoPiece piece)
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
            LudoPiece piece1 = mPieces.get(i);
            piece1.setX(startingPointX);
            piece1.setSize(pieceWidth, pieceHeight);
            ((ImageView)piece1.getTag()).setX(startingPointX);
            ((ImageView)piece1.getTag()).setY(mCenterPoint.y - pieceWidth/2);
            ((ImageView)piece1.getTag()).setLayoutParams(new FrameLayout.LayoutParams(pieceWidth,pieceWidth));
            piece1.setY(y);
            startingPointX -= pieceWidth;
        }
    }

    public int getPieceHeight() {
        return defaultHeight;
    }

    public int getPieceWidth() {
        return defaultWidth;
    }


    public enum TransitionPlayer
    {
        ONE, TWO, THREE, FOUR, NULL
    }

}
