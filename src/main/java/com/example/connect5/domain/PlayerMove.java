package com.example.connect5.domain;

public class PlayerMove {
    String playerName;
    Integer column;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
