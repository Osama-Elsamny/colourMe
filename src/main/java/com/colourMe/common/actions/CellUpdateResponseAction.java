package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.google.gson.JsonObject;

public class CellUpdateResponseAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        boolean successful = false;
        JsonObject data = message.getData().getAsJsonObject();
        if (data.has("successful"))
            successful = data.get("successful").getAsBoolean();

        // FIXME: You should return the whole message as it is and not null.
        //  Otherwise, GUI won't be able to check the Message type.
        //  GUI will check the successful field to decide whether to render the strokes for not.
        return successful ? message : null;
    }
}
