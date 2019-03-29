package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConnectRequestAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        JsonObject data = message.getData().getAsJsonObject();
        if(data.has("clientIP")) {
            gameService.spawnPlayer(message.getPlayerID(), data.get("clientIP").getAsString());
            JsonElement gameConfig = gameService.getGameConfigAsJson();
            return new Message(MessageType.ConnectResponse, gameConfig, message.getPlayerID());
        }

        return new Message(MessageType.ConnectResponse, null, message.getPlayerID());
    }
}
