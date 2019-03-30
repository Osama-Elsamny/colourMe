package com.colourMe.gui;

import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.colourMe.common.messages.MessageType;
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
    boolean sendConnectRequest(GameConfig gameConfig){return false;}

    boolean sendCellRequest(int row, int col, Coordinate coordinate){return false;}

    boolean sendCellUpdateRequest(int row, int col, List coordinates){return false;}

    boolean sendReleaseCellRequest(int row, int col, boolean isColoured){return false;}

    boolean sendClientDisconnectRequest(String playerID){return false;}

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
