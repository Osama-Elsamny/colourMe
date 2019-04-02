package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;

public abstract class ActionBase {
    abstract public Message execute(Message message, GameService gameService);
}
