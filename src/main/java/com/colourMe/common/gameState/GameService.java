package com.colourMe.common.gameState;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class GameService {
    private Gson gson = new Gson();

    private GameConfig gameConfig;

    private Board board;

    private Map<String, Player> players;

    public GameService() {
        this.players = new HashMap<>();
    }

    public void init(GameConfig gameConfig) {
        try {
            this.gameConfig = gameConfig;
            this.board = new Board(gameConfig.getSize());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();

        }
    }

    public JsonElement getGameConfigAsJson() {
        return gson.toJsonTree(this.gameConfig);
    }

    public Gson getGson() {
        return this.gson;
    }

    public void spawnPlayer(String playerId, Player player) {
        players.put(playerId, player);
    }

    public void addIpToConfig(String ip) {
        this.gameConfig.addIp(ip);
    }
}
