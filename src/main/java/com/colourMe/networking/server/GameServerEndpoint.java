package com.colourMe.networking.server;

import com.colourMe.common.marshalling.MessageDecoder;
import com.colourMe.common.marshalling.MessageEncoder;
import com.colourMe.common.messages.Message;
import com.colourMe.common.util.Log;

import javax.websocket.*;
import javax.websocket.server.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

@ServerEndpoint(
        value = "/connect/{username}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class GameServerEndpoint {
    private Session session;
    private static Set<GameServerEndpoint> endPointSessions = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("username") String username) throws IOException {
        this.session = session;
        endPointSessions.add(this);
        users.put(session.getId(), username);
    }

    @OnMessage
    public void onMessage(Session session, Message request) {
        Logger logger = Log.get(this);
        boolean result = GameServer.addToIncoming(request);
        logger.info("Added message to incoming queue " +
                (result ? "successfully" : "unsuccessfully"));
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        users.remove(session.getId());
        endPointSessions.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

    }

    public static int numberOfSessions() {
        return endPointSessions.size();
    }

    public static void broadcast(final Message message) {
        endPointSessions.forEach(x -> {
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

