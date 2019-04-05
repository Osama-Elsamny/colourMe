package com.colourMe.networking.client;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.concurrent.PriorityBlockingQueue;

import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.networking.ClockSynchronization.Clock;

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

    private final PriorityBlockingQueue<Message> receivedQueue;

    private final PriorityBlockingQueue<Message> sendQueue;

    private Clock clientClock;

    public GameClient (PriorityBlockingQueue<Message> receive, PriorityBlockingQueue<Message> send, String serverAddress, String playerID, Clock clientClock){

        // Parameter Check
        if(receive == null)
            throw new IllegalArgumentException("Receive Queue cannot be null");
        if(send == null)
            throw new IllegalArgumentException("Send Queue cannot be null");
        if(serverAddress == null)
            throw new IllegalArgumentException("Server Address cannot be null");
        if(playerID == null)
            throw new IllegalArgumentException("Player ID cannot be null");
        if(clientClock == null)
            throw new IllegalArgumentException("Client clock cannot be null");

        this.receivedQueue = receive;
        this.sendQueue = send;
        this.serverAddr = serverAddress;
        this.playerID = playerID;
        this.clientClock = clientClock;
    }

    private void handleFailure(){

        // Construct message
        Message msg = new Message(MessageType.Disconnect, null, playerID);
        msg.setTimestamp(clientClock.getTime());

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
                            msg.setTimestamp(clientClock.getTime());
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
