package com.colourMe.common.gameState;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;

public class GameService implements Cloneable {

    public static class ColorPair {
        public Color COLOR;
        public int COLOR_CODE;

        public ColorPair(Color color, int colorCode) {
            this.COLOR = color;
            this.COLOR_CODE = colorCode;
        }

    }

    private static ColorPair[] COLOR_PAIRS =  {
            new ColorPair(Color.BLUE, -16776961),
            new ColorPair(Color.RED, -65536),
            new ColorPair(Color.GREEN, -16744448),
            new ColorPair(Color.BLACK, -16777216)
    };

    private Gson gson = new Gson();

    private GameConfig gameConfig;

    private Cell[][] cells;

    public Map<String, Player> players;

    // Constructor
    public GameService() {
        this.players = new HashMap<>();
    }

    // Initializes GameConfig from given configuration
    public void init(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        this.cells = new Cell[gameConfig.getSize()][gameConfig.getSize()];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    // Returns GameConfig as Json
    public JsonElement getGameConfigAsJson() {
        return gson.toJsonTree(this.gameConfig);
    }

    // Return GameConfig
    public GameConfig getGameConfig() { return gameConfig; }

    // Acquire a cell
    public boolean acquireCell(int row, int col, double x, double y, String playerID) {
        if (isCellAvailable(row, col)) {
            this.cells[row][col].setPlayerID(playerID);
            this.cells[row][col].setState(CellState.LOCKED);
            return true;
        }

        return false;
    }

    // Release a cell
    public boolean releaseCell(int row, int col, String playerID, boolean hasColoured) {
        if (validCellOwner(row, col, playerID)) {
            if(hasColoured) {
                this.cells[row][col].setState(CellState.COLOURED);
                players.get(playerID).incrementScore();
            } else {
                this.cells[row][col].setPlayerID("");
                this.cells[row][col].setState(CellState.AVAILABLE);
            }

            return true;
        }

        return false;
    }

    // Validate cell owner
    public  boolean validCellOwner(int row, int col, String playerID) {
        return this.cells[row][col].getPlayerID().equals(playerID)
                && cells[row][col].getState().equals(CellState.LOCKED);
    }

    // Get the size of the board
    public int getBoardSize() {
        return this.cells.length;
    }

    // Gets GSON instance
    public Gson getGson() {
        return this.gson;
    }

    public Cell getCell(int row, int col) {
        return this.cells[row][col];
    }

    public Player getPlayerByID(String playerID) {
        return players.get(playerID);
    }

    // TODO: Add bolean parameter to check if it is server or not
    // Spawns a new player in the game when connect
    public void spawnPlayer(String playerId, String ip) {
        if (! players.containsKey(playerId)) {
            ColorPair pair = COLOR_PAIRS[players.size()];
            players.put(playerId, new Player(ip, pair.COLOR, pair.COLOR_CODE));
            this.gameConfig.addplayerConfig(playerId, ip);
        }
    }

    // Remove a player
    public void killPlayer(String playerID) {
        // Release locks
        for(Cell[] cellRow : cells) {
            for(Cell cell : cellRow) {
                if(cell.getPlayerID().equals(playerID)
                    && cell.getState().equals(CellState.LOCKED)) {
                    cell.setPlayerID("");
                    cell.setState(CellState.AVAILABLE);
                }
            }
        }

        // Remove player
        players.remove(playerID);
        gameConfig.removePlayerConfig(playerID);
    }

    public boolean isCellLocked(int row, int col){
        return this.cells[row][col].getState().equals(CellState.LOCKED);
    }

    public String getPlayerIP(String playerID) { return players.get(playerID).getIpAddress(); }

    public int getNumOfPlayers() {
        return players.size();
    }

    public List<String> getPlayerIds() {
        return new ArrayList<>(players.keySet());
    }

    public int getPlayerColourCode(String playerID) {
        return players.get(playerID).getColorCode();
    }

    public Color getPlayerColour(String playerID) {
        return players.get(playerID).getColor();
    }

    public int getPlayerScore(String playerID) {
        Player player = players.get(playerID);
        if(player != null) {
            return player.getScore();
        }

        throw new IllegalStateException("Player not found!");
    }

    // Checks whether a given cell is available for colouring
    private boolean isCellAvailable(int row, int col) {
        return this.cells[row][col].getState() == CellState.AVAILABLE;
    }

    @Override
    public GameService clone() throws CloneNotSupportedException {
        return (GameService) super.clone();
    }

    public boolean equals(GameService service) {
        boolean isEqual = this.cells.length == service.cells.length;

        if (isEqual) {
            for (int r=0; r < this.cells.length; r++) {
                for (int c=0; c < this.cells.length; c++)
                    isEqual = isEqual && cells[r][c].equals(service.cells[r][c]);
            }
        }

        if (isEqual) {
            for (String playerID: players.keySet()){
                isEqual = isEqual && service.players.keySet().contains(playerID);
                if(isEqual) {
                    isEqual = this.players.get(playerID).equals(service.players.get(playerID));
                }
            }
        }

        isEqual = isEqual && this.gameConfig.equals(service.gameConfig);
        return isEqual;
    }
}
