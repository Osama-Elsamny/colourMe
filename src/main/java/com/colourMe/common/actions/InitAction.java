package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitAction extends ActionBase {
    public JsonElement execute(Message message, GameService gameService) {
        Gson gson = new Gson();
        GameConfig gameConfig = gson.fromJson(message.getData(), GameConfig.class);
        gameService.init(gameConfig);
        return createSuccessResponse(message.getClientId());
    }

    public JsonElement createSuccessResponse(String clientId) {
        JsonObject response = new JsonObject();
        response.addProperty("messageType", MessageType.InitResponse.name());
        response.addProperty("data", "{\"successful\": true}");
        response.addProperty("clientId", clientId);
        return response;
    }
}
