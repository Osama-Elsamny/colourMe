package com.colourMe.common.gameState;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javafx.scene.paint.Color;

import java.util.*;

public class GameService {
    private Gson gson = new Gson();

    private GameConfig gameConfig;

    private Cell cells[][];

    private Map<String, Player> players;

    // Constructor
    public GameService() {
        this.players = new HashMap<>();
    }

    // Initializes GameConfig from given configuration
    public void init(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        this.cells = new Cell[gameConfig.getSize()][gameConfig.getSize()];
        for (int i=0; i < cells.length; i++) {
            for (int j=0; j < cells.length; j++) {
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
            }else{
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

    // Spawns a new player in the game when connect
    public void spawnPlayer(String playerId, String ip) {
        if (! players.containsKey(playerId)) {
            players.put(playerId, new Player(ip));
            this.gameConfig.addplayerConfig(playerId, ip);
        }
    }

    // Remove a player
    public void killPlayer(String playerID) {
        //TODO: Release any locks acquired by the player.
        String playerIP = players.get(playerID).getIpAddress();
        players.remove(playerID);
        gameConfig.removeIP(playerIP);
    }

    // Checks whether a given cell is available for colouring
    private boolean isCellAvailable(int row, int col) {
        return this.cells[row][col].getState() == CellState.AVAILABLE;
    }

    public String getPlayerIP(String playerID) { return players.get(playerID).getIpAddress(); }

    public int getNumOfPlayers() {
        return players.size();
    }

    public List<String> getPlayerIds() {
        return new ArrayList<>(players.keySet());
    }

    public int getPlayerColourCode(String playerID) {
        return 0;
    }

    public Color getPlayerColour(String playerID) {return null;}

    public int getPlayerScore(String playerID) {
        Player player = players.get(playerID);
        if(player != null) {
            return player.getScore();
        }

        throw new IllegalStateException("Player not found!");
    }
}
