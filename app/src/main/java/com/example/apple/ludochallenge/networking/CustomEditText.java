package com.example.apple.ludochallenge.networking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by Apple on 26/03/2018.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    Paint paint = new Paint();
    public CustomEditText(Context context) {
        super(context);
        setBackground(null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(null);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(15);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawLine(0,(getBaseline()+getBottom())/2, getWidth(),(getBaseline()+getBottom())/2,paint);
    }
}
