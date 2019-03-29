package com.colourMe.common.gameState;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

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
    }

    // Returns GameConfig as Json
    public JsonElement getGameConfigAsJson() {
        return gson.toJsonTree(this.gameConfig);
    }

    // Return GameConfig
    public GameConfig getGameConfig() { return gameConfig; }

    // Acquire a cell
    public boolean acquireCell(int row, int col, double x, double y, String playerID) {
        if(isCellAvailable(row, col)) {
            this.cells[row][col].setPlayerID(playerID);
            this.cells[row][col].setState(CellState.LOCKED);
            return true;
        }

        return false;
    }

    // Get the size of the board
    public int getBoardSize() {
        return this.cells.length;
    }

    // Gets GSON instance
    public Gson getGson() {
        return this.gson;
    }

    // Spawns a new player in the game when connected
    public void spawnPlayer(String playerId, String ip) {
        players.put(playerId, new Player(ip));
        this.gameConfig.addIp(ip);
    }

    // Checks whether a given cell is available for colouring
    private boolean isCellAvailable(int row, int col) {
        return this.cells[row][col].getState() == CellState.AVAILABLE;
    }

    public String getPlayerIP(String playerID) { return players.get(playerID).getIpAddress(); }
}
