package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConnectRequestAction extends ActionBase {
    public Message execute(Message message, GameService gameService) {
        JsonObject data = message.getData().getAsJsonObject();
        if(data.has("ip")) {
            gameService.addIpToConfig(data.get("ip").getAsString());
            JsonElement gameConfig = gameService.getGameConfigAsJson();
            return new Message(MessageType.ConnectResponse, gameConfig, message.getClientId());
        }

        return new Message(MessageType.ConnectResponse, null, message.getClientId());
    }
}
