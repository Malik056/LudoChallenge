package com.example.apple.ludochallenge;

/**
 * Created by Taha Malik on 4/14/2018.
 **/
public enum PlayerType {

    HUMAN, CPU, ONLINE;

    public static String sHUMAN = "HUMAN";
    public static String sCPU = "CPU";
    public static String sONILE = "ONLINE";

    public static PlayerType getPlayerType(int i)
    {
        return i==0 ? HUMAN : i == 1? CPU : i == 2 ? ONLINE : null;
    }
    public static int getInt(PlayerType playerType)
    {
        return playerType == HUMAN ? 0 : playerType == CPU ? 1 : playerType == ONLINE ? 2 : -1;
    }
    public static PlayerType getPlayerType(String i)
    {
        return i.equals(sHUMAN)? HUMAN : i.equals(sCPU) ? CPU : i.equals(sONILE) ? ONLINE : null;
    }

}
