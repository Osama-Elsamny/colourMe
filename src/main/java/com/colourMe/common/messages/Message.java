package com.colourMe.common.messages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Message {
    private long timestamp;
    private MessageType messageType;

    private JsonElement data;

    private String clientId;

    public Message(MessageType messageType, JsonElement data, String clientId) {
        this.messageType = messageType;
        this.data = data;
        this.clientId = clientId;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public boolean equals(Message message){
        boolean equal = true;
        equal = equal && this.getClientId().equals(message.getClientId());
        equal = equal && this.getData().toString().equals(message.getData().toString());
        equal = equal && this.getMessageType().equals(message.getMessageType());
        return equal;
    }
}
