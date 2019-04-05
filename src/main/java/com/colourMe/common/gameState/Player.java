package com.colourMe.common.gameState;

import javafx.scene.paint.Color;

public class Player implements Cloneable {
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

    public Player clone() throws CloneNotSupportedException {
        return (Player) super.clone();
    }

    public boolean equals(Player player) {
        return this.colorCode == player.colorCode &&
                this.score == player.score &&
                this.color.equals(player.color) &&
                this.ipAddress.equals(player.ipAddress);
    }
}
