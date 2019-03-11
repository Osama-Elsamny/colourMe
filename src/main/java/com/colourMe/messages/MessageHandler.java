package com.colourMe.messages;

import com.colourMe.actions.ActionBase;
import com.colourMe.actions.InitAction;
import com.colourMe.game.GameService;
import com.google.gson.JsonElement;

import java.util.EnumMap;

public class MessageHandler {
    private EnumMap<MessageType, ActionBase> actionMap;
    private GameService gameService;

    public MessageHandler() {
        this.gameService = new GameService();
        this.actionMap = new EnumMap<>(MessageType.class);
        buildActions();
    }

    public JsonElement processMessage(Message message) {
        return actionMap.get(message.getMessageType()).execute(message, gameService);
    }

    private void buildActions() {
        actionMap.put(MessageType.InitRequest, new InitAction());
    }
}