package com.colourMe.networking.client;

import java.net.URI;
import com.google.gson.JsonElement;
import java.util.concurrent.PriorityBlockingQueue;
import javax.websocket.*;

import com.colourMe.common.marshalling.*;

/**
 * ColourMe ClientEndPoint
 *
 * @author Arsalan Macknojia
 */
@ClientEndpoint(
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class
)
public class GameClientEndpoint {

    public Session session;
    private PriorityBlockingQueue<JsonElement> receivedQueue;

    /**
     * Constructor
     *
     * @param ServerEndpointURI - URI to connect to the server.
     */
    public GameClientEndpoint(URI ServerEndpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, ServerEndpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to handle connection open events.
     *
     * @param session - The session which is opened.
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Opening a WebSocket.");
        this.session = session;
    }

    /**
     * Function to handle incoming messages from server.
     *
     * @param session - The current session
     * @param update - The message from the server
     */
    @OnMessage
    public void onMessage(Session session, JsonElement update) {
        receivedQueue.put(update);
    }

    /**
     * OnError
     *
     * @param session
     */
    @OnError
    public void onError(Session session, Throwable throwable){

    }

    public void addReceiveQueue(PriorityBlockingQueue<JsonElement> queue) {
        this.receivedQueue = queue;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(JsonElement message) {
        this.session.getAsyncRemote().sendObject(message);
    }

    /**
     * Function to handle connection close events.
     *
     * @param session - The session which is getting closed.
     * @param reason - The reason for connection close.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Closing WebSocket." + reason);
        this.session = null;
    }
}