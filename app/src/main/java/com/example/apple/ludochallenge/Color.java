package com.example.apple.ludochallenge;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public enum Color {

    BLUE, YELLOW, RED, GREEN;

    public static String sBLUE = "BLUE";
    public static String sYELLOW = "YELLOW";
    public static String sRED = "RED";
    public static String sGREEN = "sGREEN";

    public static Color getColor(int i)
    {
        if(i == 0)
        {
            return BLUE;
        }
        else if(i == 1)
        {
            return YELLOW;
        }
        else if(i == 2)
        {
            return RED;
        }
        else if(i == 3)
        {
            return GREEN;
        }

        return null;
    }

    public static int getInt(Color color)
    {
        if(color == BLUE)
        {
            return 0;
        }
        else if(color == YELLOW)
        {
            return 1;
        }
        else if(color == RED)
        {
            return 2;
        }
        else if(color == GREEN)
        {
            return 3;
        }
        return -1;
    }

    public static Color getColor(String color)
    {
        return color.equals(sBLUE) ? BLUE : color.equals(sYELLOW) ? YELLOW : color.equals(sRED) ? RED
                : color.equals(sGREEN) ? GREEN : null;
    }
}
