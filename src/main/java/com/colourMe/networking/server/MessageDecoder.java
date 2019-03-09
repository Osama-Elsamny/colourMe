package com.colourMe.networking.server;

import com.colourMe.messages.Message;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<JsonElement> {
    private static Gson gson = new Gson();

    @Override
    public JsonElement decode(String s) {
        return gson.fromJson(s, JsonElement.class);
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
