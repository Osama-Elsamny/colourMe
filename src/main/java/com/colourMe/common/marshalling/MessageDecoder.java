package com.colourMe.common.marshalling;

import com.colourMe.common.messages.Message;
import com.google.gson.Gson;

import javax.websocket.EndpointConfig;

public class MessageDecoder implements javax.websocket.Decoder.Text<Message> {
    private static Gson gson = new Gson();

    @Override
    public Message decode(String s) {
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
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
