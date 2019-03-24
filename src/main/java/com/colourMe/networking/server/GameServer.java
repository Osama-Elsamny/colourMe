package com.colourMe.networking.server;

import org.glassfish.tyrus.server.Server;

public class GameServer extends Thread {
    private boolean running = false;
    private volatile boolean finished = false;

    @Override
    public void run(){
        Server server = new Server("localhost", 8080, "",
                null, GameServerEndpoint.class);
        try{
            server.start();
            this.running = true;
            System.out.println("GameServer has started!");
            while(!finished){}
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            server.stop();
        }
    }

    public void finish(){
        this.finished = true;
    }

    public boolean isRunning() {
        return running;
    }

}
