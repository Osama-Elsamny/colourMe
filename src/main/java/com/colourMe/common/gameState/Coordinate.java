package com.colourMe.common.gameState;

public class Coordinate {
    public double x;
    public double y;

    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Coordinate: (%.3f,%.3f)", x , y);
    }
}
