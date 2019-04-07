package com.colourMe.common.marshalling;

import com.colourMe.common.messages.Message;
import com.google.gson.Gson;

import javax.websocket.EndpointConfig;

public class MessageEncoder implements javax.websocket.Encoder.Text<Message> {
    private static Gson gson = new Gson();

    @Override
    public String encode(Message message) {
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Log init
        // Initialize resources
    }

    @Override
    public void destroy() {
        // Log destroy
        // Clear resources
    }
}
