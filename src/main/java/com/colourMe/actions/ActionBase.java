package com.colourMe.actions;

import com.colourMe.game.GameService;
import com.colourMe.messages.Message;
import com.google.gson.JsonElement;

public abstract class ActionBase {
    abstract public JsonElement execute(Message message, GameService gameService);
}
