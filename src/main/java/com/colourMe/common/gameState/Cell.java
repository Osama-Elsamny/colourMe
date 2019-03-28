package com.colourMe.common.gameState;

public class Cell {
    private CellState state = CellState.AVAILABLE;

    private String clientId;

    public Cell() {}

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
