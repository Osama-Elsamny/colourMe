package com.colourMe.common.gameState;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class GameConfig {
    private int size;

    private float ratio;

    private int thickness;

    private List<Pair<String, String>> ipAddresses;

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

    public List<Pair<String, String>> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<Pair<String, String>> ipAddresses) { this.ipAddresses = ipAddresses; }

    public void addplayerConfig(String playerId, String ip) {
        this.ipAddresses.add(new Pair<>(playerId, ip));
    }

    public void removePlayerConfig(String playerID) {
        for(Pair<String, String> entry : ipAddresses) {
            if(entry.getKey().equals(playerID)) {
                ipAddresses.remove(entry);
                break;
            }
        }
    }

    public String getLastIP() {
        return ipAddresses.get(size -  1).getValue();
    }

    public String getNextIP(){
        if (ipAddresses.isEmpty()) return null;
        return ipAddresses.remove(0).getValue();
    }
}
