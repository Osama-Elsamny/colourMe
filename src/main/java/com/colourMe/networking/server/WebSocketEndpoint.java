package com.colourMe.networking.server;

import javax.websocket.*;
import javax.websocket.server.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/connect/{username}")
public class WebSocketEndpoint {

    private static final String MESSAGE_FORMAT = "{\"from\" : \"%s\", \"Content\" : \"%s\" }";
    private Session session;
    private static Set<WebSocketEndpoint> servers = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("username") String username) throws IOException {
        this.session = session;
        servers.add(this);
        users.put(session.getId(), username);

        broadcast("Connected with " + username);
    }

    @OnMessage
    public static void main(Session session, String message) throws IOException{
        broadcast( users.get(session.getId()) + ": " + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        servers.remove(this);
        broadcast("Disconnected");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    public static void broadcast(final String message) throws IOException{
        servers.forEach(x ->{
            synchronized (x){
                try{
                    x.session.getBasicRemote().sendText(message);

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}

