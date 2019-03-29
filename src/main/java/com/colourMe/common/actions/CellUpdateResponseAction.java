package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.google.gson.JsonObject;

public class CellUpdateResponseAction extends ActionBase {
    // TODO: Add to buildClientActions in MessageExecutor
    @Override
    public Message execute(Message message, GameService gameService) {
        boolean successful = false;
        JsonObject data = message.getData().getAsJsonObject();
        if (data.has("successful"))
            successful = data.get("successful").getAsBoolean();
        return successful ? message : null;
    }
}
