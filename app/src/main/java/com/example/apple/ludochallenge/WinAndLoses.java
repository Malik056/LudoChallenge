package com.example.apple.ludochallenge;

public class WinAndLoses {
    String wins;
    String loses;

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getLoses() {
        return loses;
    }

    public void setLoses(String loses) {
        this.loses = loses;
    }

    public WinAndLoses(String wins, String loses) {
        this.wins = wins;
        this.loses = loses;
    }
}
