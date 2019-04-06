package com.colourMe.common.gameState;

public class Coordinate {
    public double x;
    public double y;

    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Coordinate: (%.3f,%.3f)", x , y);
    }
}
