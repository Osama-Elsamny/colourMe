package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class ConnectResponseAction extends ActionBase {
    //TODO: verify that everyone ends up with the same IP address list
    @Override
    public Message execute(Message message, GameService gameService) {
        JsonObject data = message.getData().getAsJsonObject();
        GameConfig gameConfig = gameService.getGson().fromJson(message.getData(), GameConfig.class);

        if (data != null){
            gameService.init(gameConfig);
            gameService.spawnPlayer(message.getPlayerID(), gameConfig.getLastIP());
            return successResponse(data, message.getPlayerID());
        }

        return failureResponse(data, message.getPlayerID());
    }

    private Message successResponse(JsonObject data, String playerID) {
        data.addProperty("successful", true);
        return new Message(MessageType.ReleaseCellResponse, data, playerID);
    }

    private Message failureResponse(JsonObject data, String playerID) {
        data.addProperty("successful", false);
        return new Message(MessageType.ReleaseCellResponse, data, playerID);
    }
}
