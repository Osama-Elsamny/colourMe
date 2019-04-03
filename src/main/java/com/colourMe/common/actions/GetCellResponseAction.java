package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.google.gson.JsonObject;

public class GetCellResponseAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        JsonObject data = message.getData().getAsJsonObject();
        String playerID = message.getPlayerID();
        if(data.get("successful").getAsBoolean()) {
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            double x = data.get("x").getAsDouble();
            double y = data.get("y").getAsDouble();

            gameService.acquireCell(row, col, x, y, playerID);
        }
        return message;
    }
}
