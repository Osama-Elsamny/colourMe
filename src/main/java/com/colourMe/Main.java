package com.colourMe;

import com.colourMe.networking.server.WebSocketEndpoint;
import org.glassfish.tyrus.server.Server;


public class Main {

    public static void main(String[] args) {

        Server server = new Server("localhost", 8080, "",
                null, WebSocketEndpoint.class);
        try{
            server.start();
            System.out.println("Server has started!");
            Thread.sleep(30000);
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            server.stop();
        }

    }
}
