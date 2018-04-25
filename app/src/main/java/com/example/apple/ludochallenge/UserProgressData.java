package com.example.apple.ludochallenge;

public class UserProgressData {


    public GameType getUserGameType() {
        return userGameType;
    }

    public void setUserGameType(GameType userGameType) {
        this.userGameType = userGameType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    GameType userGameType;
    String ID;
    String coins;

    public UserProgressData(GameType user, String ID, String coins) {

        this.userGameType = user;
        this.ID = ID;
        this.coins = coins;
    }


}
