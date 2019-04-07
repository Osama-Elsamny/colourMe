package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;

public class CellUpdateResponseAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        return message;
    }
}
