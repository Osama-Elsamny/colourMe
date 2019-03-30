package com.colourMe.common.gameState;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
    private int size;

    private float ratio;

    private int thickness;

    private List<String> ipAddresses;

    public GameConfig(int size, float ratio, int thickness) {
        this.size = size;
        this.ratio = ratio;
        this.thickness = thickness;
        this.ipAddresses = new ArrayList<>();
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

    public void setIpAddresses(List<String> ipAddresses) { this.ipAddresses = ipAddresses; }

    public void addIp(String ip) {
        this.ipAddresses.add(ip);
    }

    public void removeIP(String ip){ this.ipAddresses.remove(ip); }

    public String getLastIP() {
        return ipAddresses.get(ipAddresses.size() -  1);
    }

    public String getNextIP(){
        if (ipAddresses.isEmpty()) return null;
        return ipAddresses.remove(0);
    }
}
