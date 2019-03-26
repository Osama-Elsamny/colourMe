package com.colourMe.networking.server;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.google.gson.JsonElement;
import org.glassfish.tyrus.server.Server;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class GameServer extends Thread {
    private MessageExecutor messageExecutor;
    private volatile boolean running = false;
    private volatile boolean finished = false;

    private static Comparator<Message> messageComparator = (m1, m2) ->
            (int) (m1.getTimestamp() - m2.getTimestamp());

    private static PriorityBlockingQueue<Message> incoming =
            new PriorityBlockingQueue<>(10, messageComparator);

    @Override
    public void run() {
        Server server = new Server("localhost", 8080, "",
                null, GameServerEndpoint.class);

        this.messageExecutor = new MessageExecutor();
        this.messageExecutor.buildServerActions();

        try {
            server.start();
            this.running = true;
            System.out.println("GameServer has started!");

            while(!finished) {
                processIncoming();
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

    private void processIncoming(){

        // Read each message from Incoming
        synchronized (incoming) {
            try {
                while (!incoming.isEmpty()) {
                    // Read message
                    Message m = incoming.take();
                    // Process message
                    JsonElement response = messageExecutor.processMessage(m);
                    // Broadcast response to everyone
                    GameServerEndpoint.broadcast(response);
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    static boolean addToIncoming(Message m){
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

    public boolean initGameService(GameConfig gc){
        throw new NotImplementedException();
    }

    public void finish(){
        this.finished = true;
    }

    public boolean isRunning() {
        return this.running;
    }

}
