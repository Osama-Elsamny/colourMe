package com.colourMe.gui;

import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class GameAPI {
    private MessageExecutor messageExecutor;

    private GameService gameService;

    private PriorityBlockingQueue<Message> sendQueue;

    private PriorityBlockingQueue<Message> receivedQueue;


    public GameAPI(PriorityBlockingQueue<Message> sendQueue,
                   PriorityBlockingQueue<Message> receivedQueue) {
        this.gameService = new GameService();
        this.messageExecutor = new MessageExecutor(gameService);
        this.messageExecutor.buildClientActions();
        this.sendQueue = sendQueue;
        this.receivedQueue = receivedQueue;
    }

    //Requests
    public boolean wrapInTryCatch(Runnable function) {
        boolean successful = false;
        try{
            function.run();
            successful = true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return successful;
    }

    public boolean sendConnectRequest(String playerID, String playerIP) {
        return wrapInTryCatch(() -> {
            JsonObject data = new JsonObject();
            data.addProperty("playerIP", playerIP);
            Message connectRequest = new Message(MessageType.ConnectRequest, data, playerID);
            sendQueue.add(connectRequest);
        });
    }

    public boolean sendGetCellRequest(String playerID, int row, int col, Coordinate coordinate) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("row", row);
            data.addProperty("col", col);
            data.addProperty("x", coordinate.x);
            data.addProperty("y", coordinate.y);
            Message getCellRequest = new Message(MessageType.GetCellRequest, data, playerID);
            sendQueue.add(getCellRequest);
        });
    }

    public boolean sendCellUpdateRequest(String playerID, int row, int col, List coordinates) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("row", row);
            data.addProperty("col", col);
            data.add("coordinates", gameService.getGson().toJsonTree(coordinates));
            Message cellUpdateRequest = new Message(MessageType.CellUpdateRequest, data, playerID);
            sendQueue.add(cellUpdateRequest);
        });
    }

    public boolean sendReleaseCellRequest(String playerID, int row, int col, boolean hasColoured) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("row", row);
            data.addProperty("col", col);
            data.addProperty("hasColoured", hasColoured);
            Message releaseCellRequest = new Message(MessageType.ReleaseCellRequest, data, playerID);
            sendQueue.add(releaseCellRequest);
        });
    }

    public boolean sendClientDisconnectRequest(String playerID, String reason) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("reason", reason);
            Message disconnectRequest = new Message(MessageType.ClientDisconnectRequest, data, playerID);
            sendQueue.add(disconnectRequest);
        });
    }

    //Responses
    public Message processResponse() {
        Message receivedMessage = receivedQueue.poll();

        if(receivedMessage != null) {
            return messageExecutor.processMessage(receivedMessage);
        }

        // Default handler for error case: no message found
        return new Message(MessageType.DefaultType, null, null);
    }

    public boolean hasResponse() {
        return !receivedQueue.isEmpty();
    }

    //Free functions
    public int getNumOfPlayers() {
        return gameService.getNumOfPlayers();
    }

    public GameConfig getGameConfig() { return gameService.getGameConfig(); }

    public List<String> getPlayerIds() {
        return gameService.getPlayerIds();
    }

    public GameService getGameService(){
        return this.gameService;
    }

    public int getPlayerColourCode(String playerID) {
        return gameService.getPlayerColourCode(playerID);
    }

    public Color getPlayerColour(String playerID) {
        return gameService.getPlayerColour(playerID);
    }

    public int getPlayerScore(String playerID) {
        return gameService.getPlayerScore(playerID);
    }

    public boolean playerOwnsCell(int row, int col, String playerID) {
        return gameService.validCellOwner(row, col, playerID);
    }

    public boolean isCellLocked(int row, int col) {
        return gameService.isCellLocked(row, col);
    }

    public int getBoardSize() {
        return gameService.getGameConfig().getSize();
    }

    public int getThickness() {
        return gameService.getGameConfig().getThickness();
    }

    public float getRatio() {
        return gameService.getGameConfig().getRatio();
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        this.messageExecutor = new MessageExecutor(gameService);
        messageExecutor.buildClientActions();
    }
}
