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
        boolean playerOwnsCell = playerOwnsCell(gameService, data, playerID);

        if (playerOwnsCell) {
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            boolean hasColoured = data.get("hasColoured").getAsBoolean();
            gameService.releaseCell(row, col, playerID, hasColoured);
        }
        return message;
    }

    private boolean isDataValid(JsonObject data) {
        return data.has("row") && data.has("col") &&
                data.has("x") && data.has("y");
    }

    // FIXME:  Why not call isDataValid and gameService.playerHasCell directly from execute.
    //          Also gameService.releaseCell do check if the player has the cell. We don't have to check it again using gameService.playerHasCell.
    //          Also we need to add success/failure field to the message so that GUI can either fully colour the cell or revert the changes.

    private boolean playerOwnsCell(GameService gameService, JsonObject data, String playerID){
        boolean playerOwnsCell = false;
        if (isDataValid(data)) {
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            playerOwnsCell = gameService.playerHasCell(row, col, playerID);
        }
        return playerOwnsCell;
    }
}
