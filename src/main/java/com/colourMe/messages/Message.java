package com.colourMe.messages;

import com.google.gson.JsonElement;

public class Message {
    private MessageType messageType;

    private JsonElement data;

    private String clientId;

    public Message(MessageType messageType, JsonElement data, String clientId) {
        this.messageType = messageType;
        this.data = data;
        this.clientId = clientId;
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
}
