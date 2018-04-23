package com.example.apple.ludochallenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.widget.FrameLayout;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public class Piece extends android.support.v7.widget.AppCompatImageView {

    boolean open = false;
    Box mBox;
    int mSize;
    Color color;
    Paint paint;
    Bitmap image;
    float x;
    float y;
    SALGame mGame;

    @Override
    public void setX(float x) {
        super.setX(x);
        this.x = x;
    }
    @Override
    public void setY(float y) {
        super.setY(y);
        this.y = y;
    }

    public Piece(Context context, SALGame game, Color c, int size, Box box) {
        super(context);
        mGame = game;
        color = c;
        paint = new Paint();
        paint.setColor(android.graphics.Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(50);

        mBox = box;

        if(c == Color.BLUE) {
            setImageDrawable(getResources().getDrawable(R.drawable.marker_blue));
            image = BitmapFactory.decodeResource(getResources(), R.drawable.marker_blue);
        }
        else if(c == Color.YELLOW) {
            setImageDrawable(getResources().getDrawable(R.drawable.marker_yellow));
            image = BitmapFactory.decodeResource(getResources(), R.drawable.marker_yellow);
        }else if(c == Color.GREEN) {
            setImageDrawable(getResources().getDrawable(R.drawable.marker_green));
            image = BitmapFactory.decodeResource(getResources(), R.drawable.marker_green);
        }else if(c == Color.RED) {
            setImageDrawable(getResources().getDrawable(R.drawable.marker_red));
            image = BitmapFactory.decodeResource(getResources(), R.drawable.marker_red);
        }

        mSize = size;
        setWillNotDraw(false);
        setLayoutParams(new FrameLayout.LayoutParams(3*size/5,size));
    }

    public void setSize(int size)
    {
        setLayoutParams(new FrameLayout.LayoutParams(3*size/5,size));
        invalidate();
    }

    public void setSize(int width, int height)
    {
        setLayoutParams(new FrameLayout.LayoutParams(width,height));
        invalidate();
    }



}
