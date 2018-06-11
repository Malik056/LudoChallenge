package com.example.apple.ludochallenge;

/**
 * Created by Taha Malik on 5/22/2018.
 **/
public class Point {
    public float x = 0;
    public float y = 0;

    public Point() {
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }


    static float[] getXArray(Point[] points)
    {
        float[] floats = new float[points.length];


        for(int i = 0; i < points.length;i++)
        {
            floats[i] = points[i].x;
        }
        return floats;
    }
    static float[] getYArray(Point[] points)
    {
        float[] floats = new float[points.length];


        for(int i = 0; i < points.length;i++)
        {
            floats[i] = points[i].y;
        }
        return floats;
    }

}
