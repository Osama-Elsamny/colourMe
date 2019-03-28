package com.colourMe.networking.client;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.LinkedBlockingQueue;

import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;

/**
 * ColourMe Client
 *
 * @author Arsalan Macknojia
 */
public class GameClient extends Thread{

    private static final int maxTries = 3;
    private int connectionAttempt = 0;

    private String serverAddr;
    public LinkedBlockingQueue<JsonElement> receivedQueue;
    public LinkedBlockingQueue<JsonElement> sendQueue;

    GameClient (LinkedBlockingQueue<JsonElement> receive,  LinkedBlockingQueue<JsonElement> send, String serverAddress){

        // Parameter Check
        if(receive == null)
            throw new IllegalArgumentException("Receive Queue cannot be null");
        if(send == null)
            throw new IllegalArgumentException("Send Queue cannot be null");
        if(serverAddress == null)
            throw new IllegalArgumentException("Server Address cannot be null");

        receivedQueue = receive;
        sendQueue = send;
        serverAddr = serverAddress;
    }

    private void handleFailure(){

        // Construct message
        Message msg = new Message(MessageType.Disconnect, null, "");
        Gson gson = new Gson();
        JsonElement message = gson.toJsonTree(msg);

        synchronized (sendQueue){
            sendQueue.clear();
        }
        synchronized (receivedQueue){
            receivedQueue.clear();
            try {
                receivedQueue.put(message);
            } catch (InterruptedException e) {
                System.err.println("Failed to add message.");
            }
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
                        JsonElement msg = sendQueue.poll();
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