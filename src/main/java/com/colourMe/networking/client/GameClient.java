package com.colourMe.networking.client;

import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.common.util.Log;
import com.colourMe.common.util.U;
import com.colourMe.networking.ClockSynchronization.Clock;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

/**
 * ColourMe Client
 *
 * @author Arsalan Macknojia
 */
public class GameClient extends Thread {

    private boolean connected = false;

    private static final int maxTries = 3;

    private int connectionAttempt = 0;

    private String serverAddress;

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
        this.serverAddress = serverAddress;
        this.playerID = playerID;
        this.clientClock = clientClock;
    }

    private void handleFailure() {

        // Construct message
        Logger logger = Log.get(this);
        logger.warning("Handling server failure, sending Disconnect Message to GUI");
        Message msg = new Message(MessageType.Disconnect, null, playerID);
        msg.setTimestamp(clientClock.getTime());

        logger.warning("Clearing send and receive queues, and adding disconnect message to receive queue");
        synchronized (sendQueue) {
            sendQueue.clear();
        }
        synchronized (receivedQueue) {
            receivedQueue.clear();
            receivedQueue.put(msg);
        }
    }

    private boolean isServerAlive(GameClientEndpoint endpoint) throws IOException {
        int SERVER_TIMEOUT = 5000;
        long currentTime = System.currentTimeMillis();
        return currentTime - endpoint.getLastMessageReceivedTime() < SERVER_TIMEOUT;
    }

    @Override
    public void run() {
        while (true) {
            Logger logger = Log.get(this);
            logger.info("Starting GameClient, trying to connect with server with address " + serverAddress);
            try {
                // Open WebSocket.
                final GameClientEndpoint clientEndPoint = new GameClientEndpoint(new URI(serverAddress));
                this.connectionAttempt = 0;
                this.connected = true;

                // Add receiveQueue.
                clientEndPoint.addReceiveQueue(receivedQueue);

                // Send message to WebSocket.
                while (clientEndPoint.session != null) {
                    if (isServerAlive(clientEndPoint)) {
                        synchronized (sendQueue) {
                            Message msg = sendQueue.poll();
                            if (msg != null) {
                                msg.setTimestamp(clientClock.getTime());
                                logger.info("Sending message to server, with MessageType: " + msg.getMessageType().name());
                                logger.info("Message:\n" + U.json(msg));
                                clientEndPoint.sendMessage(msg);
                            }
                        }
                    } else {
                        logger.warning("Disconnecting from the server endpoint");
                        clientEndPoint.disconnect();
                    }
                }
                handleFailure();
                break;
            } catch (Exception ex) {
                U.handleExceptionBase(logger, ex);
                if (connectionAttempt < maxTries) {
                    logger.warning("Failed to connect to server, trying again.");
                    connectionAttempt++;
                } else {
                    // Connection Failure
                    if (connected) {
                        handleFailure();
                        connected = false;
                    } else {
                        logger.severe("Killing Client thread");
                    }
                    break;
                }
            }
        }
    }
}
