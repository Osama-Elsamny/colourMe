package com.colourMe.networking.server;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.colourMe.common.messages.MessageType;
import com.colourMe.common.util.Log;
import com.colourMe.common.util.U;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.glassfish.tyrus.server.Server;
import com.colourMe.networking.ClockSynchronization.Clock;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;

public class GameServer extends Thread {
    private MessageExecutor messageExecutor;
    private GameService gameService;
    private boolean reconnectState = false;
    private volatile boolean running = false;
    private volatile boolean finished = false;
    private Clock serverClock;
    private int clockSyncCounter = 0;

    private static final PriorityBlockingQueue<Message> incoming =
            new PriorityBlockingQueue<>(10, Message.messageComparator);

    public GameServer(Clock serverClock) { this.serverClock = serverClock; }

    @Override
    public void run() {
        Logger logger = Log.get(this);
        logger.info("Starting a new GameServer ...");
        Server server = new Server("localhost", 8080, "",
                null, GameServerEndpoint.class);

        this.gameService = new GameService();
        this.messageExecutor = new MessageExecutor(gameService);
        this.messageExecutor.buildServerActions();

        try {
            server.start();
            this.running = true;
            logger.info("GameServer has started!");

            while(!finished) {
                processIncoming();
                // Broadcast server time after every 10 seconds.
                if (clockSyncCounter == 10000){
                    clockSyncCounter = 0;
                    GameServerEndpoint.broadcast(sendServerTime());
                }
                clockSyncCounter++;
                Thread.sleep(1);
            }
        } catch (Exception ex) {
            U.handleExceptionBase(logger, ex);
        } finally {
            logger.info("Stopping Server ...");
            server.stop();
            this.running = false;
            logger.info("Server stopped successfully");
        }
    }

    private void processIncoming() {
        Logger logger = Log.get(this);
        // Read each message from Incoming
        synchronized (incoming) {
            try {
                resetServerIfDisconnected();
                while (!incoming.isEmpty()) {
                    // Read message
                    logger.info("Waiting for message ...");
                    Message message = incoming.take();
                    logger.info("Message extracted from incoming queue");
                    logger.info("Message:\n" + U.json(message));

                    // Process message
                    Message response = messageExecutor.processMessage(message);
                    response.setTimestamp (serverClock.getTime());
                    logger.info("Message processed");
                    logger.info("Response:\n" + U.json(response));

                    // Broadcast response to everyone
                    GameServerEndpoint.broadcast(response);
                }
            } catch(Exception ex) {
                U.handleExceptionBase(logger, ex);
            }
        }
    }

    private void resetServerIfDisconnected() {
        Logger logger = Log.get(this);
        if(reconnectState && allClientsConnected()) {
            // Broadcast GameService to all clients
            logger.warning("Sending reconnect message to all connected clients");
            Message reconnectRequest = new Message(MessageType.ReconnectRequest, null, null);
            incoming.put(reconnectRequest);
            reconnectState = false;
        }
    }

    private boolean allClientsConnected() {
        return GameServerEndpoint.numberOfSessions() == gameService.getNumberOfClientIPs();
    }

    static boolean addToIncoming(Message m) {
        Logger logger = Log.get(GameServer.class);
        return U.wrapInTryCatch(logger, () -> {
            synchronized (incoming) {
                incoming.add(m);
            }
        });
    }

    public boolean initGameService(GameConfig config) {
        Logger logger = Log.get(GameServer.class);
        return U.wrapInTryCatch(() -> {
            logger.info("Initializing gameConfig with Config:\n" + U.json(config));
            messageExecutor.initGameConfig(config);
        });
    }

    public boolean initGameService(GameService gameService){
        boolean successful;
        try {
            this.gameService = gameService;
            this.messageExecutor = new MessageExecutor(gameService);
            this.messageExecutor.buildServerActions();
            reconnectState = true;
            successful = true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            successful = false;
        }
        return successful;
    }

    public Message sendServerTime(){
        JsonObject data = new JsonObject();
        data.addProperty("TimeStamp", serverClock.getTime());

        Message message = new Message(MessageType.ClockSyncResponse, data, null );
        return message;
    }

    public void finish(){
        this.finished = true;
    }

    public boolean isRunning() {
        return this.running;
    }

}
