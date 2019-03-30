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
    boolean wrapInTryCatch(Runnable function) {
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

    boolean sendConnectRequest(String playerID, String playerIP) {
        return wrapInTryCatch(() -> {
            JsonObject data = new JsonObject();
            data.addProperty("playerIP", playerIP);
            Message connectRequest = new Message(MessageType.ConnectRequest, data, playerID);
            sendQueue.add(connectRequest);
        });
    }

    boolean sendGetCellRequest(String playerID, int row, int col, Coordinate coordinate) {
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

    boolean sendCellUpdateRequest(String playerID, int row, int col, List coordinates) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("row", row);
            data.addProperty("col", col);
            data.addProperty("coordinates", gameService.getGson().toJson(coordinates));
            Message cellUpdateRequest = new Message(MessageType.CellUpdateRequest, data, playerID);
            sendQueue.add(cellUpdateRequest);
        });
    }

    boolean sendReleaseCellRequest(String playerID, int row, int col, boolean isColoured) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("row", row);
            data.addProperty("col", col);
            data.addProperty("isColoured", isColoured);
            Message releaseCellRequest = new Message(MessageType.ReleaseCellRequest, data, playerID);
            sendQueue.add(releaseCellRequest);
        });
    }

    boolean sendClientDisconnectRequest(String playerID, String reason) {
        return wrapInTryCatch( () -> {
            JsonObject data = new JsonObject();
            data.addProperty("reason", reason);
            Message disconnectRequest = new Message(null, data, playerID);
        });
    }

    //Responses
    Message processResponse() {
        Message receivedMessage = receivedQueue.poll();

        if(receivedMessage != null) {
            return messageExecutor.processMessage(receivedMessage);
        }

        // Default handler for error case: no message found
        return new Message(MessageType.DefaultType, null, null);
    }

    boolean hasResponse() {
        return !receivedQueue.isEmpty();
    }

    //Free functions
    int getNumOfPlayers(){return 0;}
    String[] getPlayerNames(){return null;}
    int getPlayerColourCode(String playerID){return 0;}
    Color getPlayerColour(String playerID){return null;}
    int getPlayerScore(String playerID){return 0;}
    Message getResponse(){return null;}
}
