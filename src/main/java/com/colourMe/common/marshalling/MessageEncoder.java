package com.colourMe.common.marshalling;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class MessageEncoder implements javax.websocket.Encoder.Text<JsonElement> {
    private static Gson gson = new Gson();

    @Override
    public String encode(JsonElement message) {
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
