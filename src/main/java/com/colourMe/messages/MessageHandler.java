package com.colourMe.messages;

import com.colourMe.actions.ActionBase;
import com.colourMe.game.GameService;
import com.google.gson.JsonElement;
import java.util.HashMap;

public class MessageHandler {
    private HashMap<String, ActionBase> actionMap;
    private GameService gameService;

    public MessageHandler() {
        gameService = new GameService();
        actionMap = new HashMap<>();
    }

    public JsonElement processMessage(Message message) {
        return actionMap.get(message.getMessageType()).execute(message.getData(), gameService);
    }
}