package com.colourMe.messages;

import com.google.gson.JsonElement;

public class Message {
    public Message(String messageType, JsonElement data, String clientId) {
        this.messageType = messageType;
        this.data = data;
        this.clientId = clientId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
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

    private String messageType;
    private JsonElement data;
    private String clientId;
}
