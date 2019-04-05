package com.colourMe.common.gameState;

public class Cell implements Cloneable {
    private CellState state = CellState.AVAILABLE;

    private String playerID = "";

    public Cell() {}

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public boolean equals(Cell cell) {
        return this.state.equals(cell.state)
               && this.playerID.equals(cell.playerID);
    }
}
