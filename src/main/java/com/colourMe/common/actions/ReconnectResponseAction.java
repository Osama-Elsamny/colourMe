package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class ReconnectResponseAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        JsonObject data = message.getData().getAsJsonObject();
        if(data != null) {
            gameService = gameService.getGson().fromJson(data, GameService.class);
            return successResponse();
        }

        return failureResponse();
    }

    private Message successResponse() {
        JsonObject data = new JsonObject();
        data.addProperty("success", true);
        return new Message(MessageType.ReconnectResponse, data, null);
    }

    private Message failureResponse() {
        JsonObject data = new JsonObject();
        data.addProperty("success", false);
        return new Message(MessageType.ReconnectResponse, data, null);
    }
}
