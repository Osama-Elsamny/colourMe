package com.colourMe.game;

import java.util.HashMap;
import java.util.Map;

public class GameService {
    private GameConfig gameConfig;

    private Board board;

    private Map<String, Player> players;

    public GameService() {
        this.players = new HashMap<>();
    }

    public void init(GameConfig gameConfig) {
        this.gameConfig = gameConfig;
        this.board = new Board(gameConfig.getSize());
    }

    public void spawnPlayer(String playerId, Player player) {
        players.put(playerId, player);
    }
}
