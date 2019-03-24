package main.java.com.colourMe.common.marshalling;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements javax.websocket.Decoder.Text<JsonElement> {
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
