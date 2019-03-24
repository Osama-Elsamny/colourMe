package com.colourMe.common.gameState;

public class Cell {
    private int state = -1;

    private int clientId = -1;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
}
