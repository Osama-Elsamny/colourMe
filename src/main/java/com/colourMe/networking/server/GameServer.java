package com.colourMe.networking.server;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.glassfish.tyrus.server.Server;
import com.colourMe.networking.ClockSynchronization.Clock;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

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
        Server server = new Server("localhost", 8080, "",
                null, GameServerEndpoint.class);

        this.gameService = new GameService();
        this.messageExecutor = new MessageExecutor(gameService);
        this.messageExecutor.buildServerActions();

        try {
            server.start();
            this.running = true;
            System.out.println("GameServer has started!");

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
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            server.stop();
            this.running = false;
        }
    }

    private void processIncoming() {

        // Read each message from Incoming
        synchronized (incoming) {
            try {
                resetServerIfDisconnected();
                while (!incoming.isEmpty()) {
                    // Read message
                    Message m = incoming.take();

                    // Process message
                    Message response = messageExecutor.processMessage(m);
                    response.setTimestamp (serverClock.getTime());

                    // Broadcast response to everyone
                    GameServerEndpoint.broadcast(response);
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void resetServerIfDisconnected() {
        if(reconnectState && allClientsConnected()) {
            // Broadcast GameService to all clients
            Message reconnectRequest = new Message(MessageType.ReconnectRequest, null, null);
            incoming.put(reconnectRequest);
            reconnectState = false;
        }
    }

    private boolean allClientsConnected() {
        return GameServerEndpoint.numberOfSessions() == gameService.getNumberOfClientIPs();
    }

    static boolean addToIncoming(Message m) {
        boolean successful;
        try {
            synchronized (incoming) {
                incoming.add(m);
                successful = true;
            }
        } catch(Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            successful = false;
        }
        return successful;
    }

    public boolean initGameService(GameConfig config) {
        boolean successful;
        try {
            System.out.println("Config: " + config);
            messageExecutor.initGameConfig(config);
            successful = true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            successful = false;
        }

        return successful;
    }

    public boolean initGameService(GameService gameService){
        boolean successful;
        try {
            this.messageExecutor = new MessageExecutor(gameService);
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
