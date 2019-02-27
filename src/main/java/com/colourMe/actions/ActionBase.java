package com.colourMe.actions;

import com.colourMe.game.GameService;
import com.google.gson.JsonElement;

public abstract class ActionBase {
    abstract public JsonElement execute(JsonElement data, GameService gameService);
}
