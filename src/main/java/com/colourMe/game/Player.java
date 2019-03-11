package com.colourMe.game;

import java.util.ArrayList;

public class Player {
    private boolean pixels[][];

    private int score = 0;

    private String ipAddress;

    public Player(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean[][] getPixels() {
        return pixels;
    }

    public void setPixels(boolean[][] pixels) {
        this.pixels = pixels;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
