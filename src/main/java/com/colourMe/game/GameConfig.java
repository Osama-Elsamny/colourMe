package com.colourMe.game;

import java.util.List;

public class GameConfig {
    private int size;

    private float ratio;

    private int thickness;

    private List<String> ipAddresses;

    public GameConfig(int size, float ratio, int thickness, List<String> ipAddresses) {
        this.size = size;
        this.ratio = ratio;
        this.thickness = thickness;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }
}
