package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class ReleaseCellResponseAction extends ActionBase {
    @Override
    public Message execute (Message message, GameService gameService){
        String playerID = message.getPlayerID();
        JsonObject data = message.getData().getAsJsonObject();

        if(isDataValid(data, playerID)) {
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            boolean hasColoured = data.get("hasColoured").getAsBoolean();

            if(gameService.releaseCell(row, col, playerID, hasColoured)) { return successResponse(data, playerID); }
        }
        return failureResponse(data, playerID);
    }

    private boolean isDataValid(JsonObject data, String playerID) {
        return data.has("row") && data.has("col")
                && data.has("hasColoured") && (playerID != null);
    }

    private Message successResponse(JsonObject data, String clientId) {
        data.addProperty("successful", true);
        return new Message(MessageType.ReleaseCellResponse, data, clientId);
    }

    private Message failureResponse(JsonObject data, String playerID) {
        data.addProperty("successful", false);
        return new Message(MessageType.ReleaseCellResponse, data, playerID);
    }
}
