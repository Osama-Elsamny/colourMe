package com.colourMe.networking.server;

import com.colourMe.messages.Message;
import com.colourMe.messages.MessageHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.websocket.*;
import javax.websocket.server.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(
        value = "/connect/{username}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class WebSocketEndpoint {

    private static final String MESSAGE_FORMAT = "{\"from\" : \"%s\", \"Content\" : \"%s\" }";
    private MessageHandler messageHandler;
    private Session session;
    private static Set<WebSocketEndpoint> servers = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    public WebSocketEndpoint() {
        messageHandler = new MessageHandler();
    }

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("username") String username) throws IOException {
        this.session = session;
        servers.add(this);
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(Session session, JsonElement request) {
        JsonObject jsonObject = request.getAsJsonObject();
        Message message = new Message(jsonObject.get("messageType").getAsString(),
                jsonObject.get("data"),
                session.getId());
        JsonElement response = messageHandler.processMessage(message);
        broadcast(response);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        servers.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    private static void broadcast(final JsonElement message) {
        servers.forEach(x -> {
            synchronized (x) {
                try {
                    x.session.getAsyncRemote().sendObject(message);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

