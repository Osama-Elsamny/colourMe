package com.colourMe.networking.client;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonObject;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;


/**
 * ColourMe Client
 *
 * @author Arsalan Macknojia
 */
public class GameClient implements Runnable{

    private int maxTries = 3;
    private int connectionAttempt = 0;

    private String serverAddr;
    public final LinkedBlockingQueue<JsonElement> receivedQueue;
    public final LinkedBlockingQueue<JsonElement> sendQueue;

    GameClient (LinkedBlockingQueue<JsonElement> receive,  LinkedBlockingQueue<JsonElement> send, String serverAddress){
        receivedQueue = receive;
        sendQueue = send;
        serverAddr = serverAddress;
    }

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

                // Failure Case.
                sendQueue.clear();
                receivedQueue.clear();

                // Construct message
                JsonObject data = new JsonObject();
                data.addProperty("Disconnect", true);
                Message msg = new Message(MessageType.Disconnect, data, "");
                Gson gson = new Gson();
                JsonElement message = gson.toJsonTree(msg);

                try {
                    receivedQueue.put(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (URISyntaxException ex) {
                if (connectionAttempt < maxTries){
                    connectionAttempt++;
                } else {
                    System.err.println("URISyntaxException exception: " + ex.getMessage());
                    break;
                }
            }
        }
    }
}