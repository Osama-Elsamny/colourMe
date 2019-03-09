package com.colourMe.game;

public class Board {
    private Cell cells[][];

    public Board(int row, int col) {
        this.cells = new Cell[row][col];
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }
}
