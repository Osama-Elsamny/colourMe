package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class CellUpdateRequestAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        // Expects row, col, x, y
        Message response;
        String playerID = message.getPlayerID();
        JsonObject data = message.getData().getAsJsonObject();
        boolean playerOwnsCell = playerOwnsCell(gameService, data, playerID);
        response = playerOwnsCell ?
                    successResponse(data, playerID) : failureResponse(data, playerID);
        return response;
    }

    // FIXME: Don't we expect a queue of coordinates instead of just x and y?
    private boolean isDataValid(JsonObject data) {
        return data.has("row") && data.has("col") &&
                data.has("x") && data.has("y");
    }

    // FIXME: Is this function really necessary? Can't we just call isDataValid and gameService.releaseCell from execute directly.
    //          releaseCell do check if the user has the cell. We don't have to check it again.
    private boolean playerOwnsCell(GameService gameService, JsonObject data, String playerID){
        boolean playerOwnsCell = false;
        if (isDataValid(data)) {
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            playerOwnsCell = gameService.playerHasCell(row, col, playerID);
        }
        return playerOwnsCell;
    }

    private Message successResponse(JsonObject data, String playerID) {
        data.addProperty("successful", true);
        return new Message(MessageType.CellUpdateRequest, data, playerID);
    }

    private Message failureResponse(JsonObject data, String playerID) {
        data.addProperty("successful", false);
        return new Message(MessageType.CellUpdateRequest, data, playerID);
    }


}
