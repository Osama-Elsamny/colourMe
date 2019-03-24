package main.java.com.colourMe.networking.client;

import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.JsonElement;
import java.util.PriorityQueue;

/**
 * ColourMe Client
 *
 * @author Arsalan Macknojia
 */
public class GameClient implements Runnable{

    private int maxTries = 3;
    private int connectionAttempt = 0;

    private String serverAddr;
    public final PriorityQueue<JsonElement> receivedQueue;
    public final PriorityQueue<JsonElement> sendQueue;

    GameClient (PriorityQueue<JsonElement> receive,  PriorityQueue<JsonElement> send, String serverAddress){
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