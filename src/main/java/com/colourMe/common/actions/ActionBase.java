package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.google.gson.JsonElement;

public abstract class ActionBase {
    abstract public JsonElement execute(Message message, GameService gameService);
}
