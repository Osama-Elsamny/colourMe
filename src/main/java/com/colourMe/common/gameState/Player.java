package com.colourMe.common.gameState;

import javafx.scene.paint.Color;

public class Player {
    private int score = 0;

    private String ipAddress;

    private Color color;
    private int colorCode;

    public Player(String ipAddress, Color color, int colorCode) {

        this.ipAddress = ipAddress;
        this.color = color;
        this.colorCode = colorCode;
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
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }
}
