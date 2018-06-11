package com.example.apple.ludochallenge;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public class LudoPiece extends android.support.v7.widget.AppCompatImageView {

    boolean open = false;
    LudoBox mBox;
    int mSize;
    public float realY;
    Color color;
    Paint paint;
    Bitmap image;
    float x;
    float y;
    LudoPlayer player;
    LudoGame mGame;

    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0.5f);
    ObjectAnimator circleAnimator;

    float previousScaleX;
    float previousScaleY;

    LudoBox startPosition;
    int pieceNum;

    @Override
    public void setX(float x) {
        super.setX(x);
        this.x = x;
//        ((View)getTag()).setX(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.y = y;
//        ((View)getTag()).setY(y - ((View) getTag()).getHeight() + getHeight());
    }

    View tag;



    @Override
    public View getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = (View) tag;
    }



    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    public LudoPiece(Context context, LudoPlayer player, LudoGame game, Color c, int size, LudoBox box, View tag) {
        super(context);
        mGame = game;
        color = c;
        paint = new Paint();
        paint.setColor(android.graphics.Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(50);
        startPosition = box;
        mBox = box;
        this.player = player;
        setTag(tag);

        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);
        alphaAnimator.setDuration(300);
        alphaAnimator.setInterpolator(new LinearOutSlowInInterpolator());

        circleAnimator = ObjectAnimator.ofFloat(tag,"rotation", 360);
        circleAnimator.setDuration(300);
        circleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        circleAnimator.setRepeatMode(ValueAnimator.RESTART);
        circleAnimator.setInterpolator(new LinearInterpolator());

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
//        setWillNotDraw(false);
//        setLayoutParams(new FrameLayout.LayoutParams(3 * size/5,size));
        setLayoutParams(new FrameLayout.LayoutParams(size,size));
        setScaleType(ScaleType.FIT_XY);
        setScaleX(1f);
        setScaleY(1f);
    }

    public void setSize(int size)
    {
        int width = size;
        setLayoutParams(new FrameLayout.LayoutParams(width,size));
//        ((View)getTag()).setLayoutParams(new FrameLayout.LayoutParams(4*width/4,4*width/4));
//        ((View)getTag()).invalidate();
//        invalidate();
    }

    public void setSize(int width, int height)
    {
        setLayoutParams(new FrameLayout.LayoutParams(width,height));
//        ((View)getTag()).setLayoutParams(new FrameLayout.LayoutParams(4*width/4,4*width/4));
//        ((View)getTag()).invalidate();
//        invalidate();
    }

    public boolean isValid(int num)
    {
        LudoBox box = mBox;

        if(num != 6 && !open)
        {
            return false;
        }

        for(int i = 0; i < num; i++)
        {
            if(this.player.player == box.transitionPlayer)
            {
                box = box.transitionBox;
            }
            else
                box = box.nextBox;

            if(box == null)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
//        ((View)getTag()).requestLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

}
