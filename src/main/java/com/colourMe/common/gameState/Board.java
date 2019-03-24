package com.colourMe.common.gameState;

public class Board {
    private Cell cells[][];

    public Board(int size) {
        this.cells = new Cell[size][size];
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
