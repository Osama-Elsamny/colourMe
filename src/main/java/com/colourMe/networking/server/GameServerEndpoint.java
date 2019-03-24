package com.colourMe.networking.server;

import com.colourMe.common.marshalling.MessageDecoder;
import com.colourMe.common.marshalling.MessageEncoder;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.colourMe.common.messages.MessageType;
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
public class GameServerEndpoint {
    private Session session;
    private static Set<GameServerEndpoint> servers = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

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
        Message message = new Message(MessageType.valueOf(jsonObject.get("messageType").getAsString()),
                jsonObject.get("data"),
                jsonObject.get("clientId").getAsString());
        boolean result = GameServer.addToIncoming(message);
        System.out.println("Added message to incoming queue " +
                (result ? "successfully" : "unsuccessfully"));
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        servers.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    public static void broadcast(final JsonElement message) {
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

