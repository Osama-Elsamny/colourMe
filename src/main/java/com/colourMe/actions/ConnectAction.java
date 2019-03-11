package com.colourMe.actions;

import com.colourMe.game.GameService;
import com.colourMe.messages.Message;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConnectAction extends ActionBase {
    public JsonElement execute(Message message, GameService gameService) {
        return new JsonObject();
    }
}
