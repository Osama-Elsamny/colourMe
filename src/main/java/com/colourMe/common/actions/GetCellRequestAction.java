package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class GetCellRequestAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        // TODO: Check if data is null
        JsonObject data = message.getData().getAsJsonObject();
        String clientId = message.getPlayerID();
        int boardSize = gameService.getBoardSize();
        if(isDataValid(data)) {
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            double X = data.get("x").getAsDouble();
            double Y = data.get("y").getAsDouble();

            if(isRowAndColInBounds(row, col, boardSize) &&
                    gameService.acquireCell(row, col, X, Y, clientId)) {
                return successResponse(data, clientId);
            }
        }

        return failureResponse(data, clientId);
    }

    private boolean isRowAndColInBounds(int row, int col, int size) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }

    private boolean isDataValid(JsonObject data) {
        return data.has("row") && data.has("col")
                && data.has("x") && data.has("y");
    }

    private Message successResponse(JsonObject data, String clientId) {
        data.addProperty("successful", true);
        return new Message(MessageType.GetCellResponse, data, clientId);
    }

    private Message failureResponse(JsonObject data, String clientId) {
        data.addProperty("successful", false);
        return new Message(MessageType.GetCellResponse, data, clientId);
    }
}
