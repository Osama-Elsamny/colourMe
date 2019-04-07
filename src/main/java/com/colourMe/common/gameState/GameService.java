package com.colourMe.common.gameState;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;

public class GameService implements Cloneable {
    public transient Gson gson = new Gson();

    private GameConfig gameConfig;

    private Cell[][] cells;

    private Map<String, Player> players;

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

    public Cell[][] getCells() {
        return cells;
    }

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
        if(!playerExists(playerId)) {
            int colorCode = ColorPair.COLOR_PAIRS[players.size()].COLOR_CODE;
            players.put(playerId, new Player(ip, players.size(), colorCode));
            this.gameConfig.addplayerConfig(playerId, ip);
        }
    }

    // Spawns from players from game config
    public void spawnPlayersFromConfig() {
        for(Pair<String, String> entry : gameConfig.getIpAddresses()) {
            if(!playerExists(entry.getKey())) {
                int colorCode = ColorPair.COLOR_PAIRS[players.size()].COLOR_CODE;
                players.put(entry.getKey(), new Player(entry.getValue(), players.size(), colorCode));
            }
        }
    }

    // Remove a player
    public void killPlayer(String playerID) {
        // Release locks
        releaseAllAcquiredLocks(
            x -> x.getPlayerID().equals(playerID) && x.getState().equals(CellState.LOCKED)
        );
        // Remove player
        players.remove(playerID);
        gameConfig.removePlayerConfig(playerID);
    }

    public int getNumberOfClientIPs() {
        return gameConfig.getIpAddresses().size();
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

    private boolean playerExists(String playerID) {
        return players.containsKey(playerID);
    }

    private void releaseAllAcquiredLocks(Function<Cell, Boolean> predicate) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (predicate.apply(cell)) {
                    cell.setState(CellState.AVAILABLE);
                    cell.setPlayerID("");
                }
            }
        }
    }

    @Override
    public GameService clone() throws CloneNotSupportedException {
        releaseAllAcquiredLocks(x -> x.getState().equals(CellState.LOCKED));
        return (GameService) super.clone();
    }

    public boolean equals(GameService service) {
        boolean isEqual = this.cells.length == service.cells.length;

        // Verify all cells are equal
        if (isEqual) {
            for (int r=0; r < this.cells.length; r++) {
                for (int c=0; c < this.cells.length; c++)
                    isEqual = isEqual && cells[r][c].equals(service.cells[r][c]);
            }
        }

        // Verify all players and their keys are equal
        if (isEqual) {
            for (String playerID: players.keySet()){
                if(isEqual && service.players.keySet().contains(playerID))
                    isEqual = this.players.get(playerID).equals(service.players.get(playerID));
            }
        }

        isEqual = isEqual && this.gameConfig.equals(service.gameConfig);
        return isEqual;
    }
}
