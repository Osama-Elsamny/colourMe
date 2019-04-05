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
            return successResponse(message);
        }
        return failureResponse();
    }

    private Message successResponse(Message message) {
        JsonObject data = message.getData().getAsJsonObject();
        data.addProperty("successful", true);
        return new Message(MessageType.ReconnectResponse, data, null);
    }

    private Message failureResponse() {
        JsonObject data = new JsonObject();
        data.addProperty("successful", false);
        return new Message(MessageType.ReconnectResponse, data, null);
    }
}
