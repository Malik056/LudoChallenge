package com.example.apple.ludochallenge;


public class Versus {

    WinAndLoses winAndLoses;
    String versus;

    public Versus(WinAndLoses winAndLoses, String versus) {
        this.winAndLoses = winAndLoses;
        this.versus = versus;
    }

    public WinAndLoses getWinAndLoses() {
        return winAndLoses;
    }

    public void setWinAndLoses(WinAndLoses winAndLoses) {
        this.winAndLoses = winAndLoses;
    }

    public String getVersus() {
        return versus;
    }

    public void setVersus(String versus) {
        this.versus = versus;
    }
}

