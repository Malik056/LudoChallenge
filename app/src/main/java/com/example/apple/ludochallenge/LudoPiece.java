package com.example.apple.ludochallenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public class LudoPiece extends android.support.v7.widget.AppCompatImageView {

    boolean open = false;
    LudoBox mBox;
    int mSize;
    Color color;
    Paint paint;
    Bitmap image;
    float x;
    float y;
    LudoPlayer player;
    LudoGame mGame;
    LudoBox startPosition;

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

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);

    }

    public LudoPiece(Context context, LudoPlayer player, LudoGame game, Color c, int size, LudoBox box, LudoBox firstPoint) {
        super(context);
        mGame = game;
        color = c;
        paint = new Paint();
        paint.setColor(android.graphics.Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(50);
        startPosition = firstPoint;
        mBox = box;
        this.player = player;

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

    public boolean isValid(int num)
    {
        LudoBox box = mBox;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void kill()
    {

    }

}
