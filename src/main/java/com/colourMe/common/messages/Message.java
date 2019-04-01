package com.colourMe.common.messages;

import com.google.gson.JsonElement;

import java.util.Comparator;

public class Message {
    private long timestamp;
    private MessageType messageType;

    private JsonElement data;

    private String playerID;

    public static Comparator<Message> messageComparator = (m1, m2) ->
            (int) (m1.getTimestamp() - m2.getTimestamp());

    public Message(MessageType messageType, JsonElement data, String playerID) {
        this.messageType = messageType;
        this.data = data;
        this.playerID = playerID;
        this.timestamp = System.currentTimeMillis();
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public boolean equals(Message message){
        boolean equal = true;
        equal = equal && this.getPlayerID().equals(message.getPlayerID());
        equal = equal && this.getData().toString().equals(message.getData().toString());
        equal = equal && this.getMessageType().equals(message.getMessageType());
        return equal;
    }
}
