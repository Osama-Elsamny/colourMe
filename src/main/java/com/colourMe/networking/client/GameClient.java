package com.colourMe.networking.client;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.concurrent.PriorityBlockingQueue;

import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;

/**
 * ColourMe Client
 *
 * @author Arsalan Macknojia
 */
public class GameClient extends Thread {

    private static final int maxTries = 3;
    private int connectionAttempt = 0;

    private String serverAddr;
    private String playerID;
    public final PriorityBlockingQueue<Message> receivedQueue;
    public final PriorityBlockingQueue<Message> sendQueue;

    public GameClient (PriorityBlockingQueue<Message> receive,  PriorityBlockingQueue<Message> send, String serverAddress, String playerID){

        // Parameter Check
        if(receive == null)
            throw new IllegalArgumentException("Receive Queue cannot be null");
        if(send == null)
            throw new IllegalArgumentException("Send Queue cannot be null");
        if(serverAddress == null)
            throw new IllegalArgumentException("Server Address cannot be null");
        if(playerID == null)
            throw new IllegalArgumentException("Player ID cannot be null");

        receivedQueue = receive;
        sendQueue = send;
        serverAddr = serverAddress;
        playerID = playerID;

    }

    private void handleFailure(){

        // Construct message
        Message msg = new Message(MessageType.Disconnect, null, playerID);

        synchronized (sendQueue){
            sendQueue.clear();
        }
        synchronized (receivedQueue){
            receivedQueue.clear();
            receivedQueue.put(msg);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Open WebSocket.
                final GameClientEndpoint clientEndPoint = new GameClientEndpoint(new URI(serverAddr));

                // Add receiveQueue.
                clientEndPoint.addReceiveQueue(receivedQueue);

                // Send message to WebSocket.
                while (clientEndPoint.session != null) {
                    synchronized (sendQueue) {
                        Message msg = sendQueue.poll();
                        if (msg != null) {
                            clientEndPoint.sendMessage(msg);
                        }
                    }
                }
            } catch (URISyntaxException ex) {
                if (connectionAttempt < maxTries){
                    connectionAttempt++;
                } else {
                    // Connection Failure
                    handleFailure();
                    break;
                }
            }
        }
    }
}
