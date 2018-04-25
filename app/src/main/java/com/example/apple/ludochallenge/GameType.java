package com.example.apple.ludochallenge;

public class GameType {

    Versus versus;
    String gameType;

    public GameType(Versus versus, String gameType) {
        this.versus = versus;
        this.gameType = gameType;
    }

    public Versus getVersus() {
        return versus;
    }

    public void setVersus(Versus versus) {
        this.versus = versus;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}