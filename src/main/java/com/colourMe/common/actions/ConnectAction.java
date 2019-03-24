package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConnectAction extends ActionBase {
    public JsonElement execute(Message message, GameService gameService) {
        return new JsonObject();
    }
}
