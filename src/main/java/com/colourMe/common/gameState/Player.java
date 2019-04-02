package com.colourMe.common.gameState;

public class Player {
    private int score = 0;

    private String ipAddress;

    public Player(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void incrementScore() { this.score++; }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
