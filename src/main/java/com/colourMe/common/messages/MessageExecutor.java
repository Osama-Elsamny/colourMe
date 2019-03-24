package com.colourMe.common.messages;

import com.colourMe.common.actions.ActionBase;
import com.colourMe.common.actions.InitAction;
import com.colourMe.common.gameState.GameService;
import com.google.gson.JsonElement;

import java.util.EnumMap;

public class MessageExecutor {
    private EnumMap<MessageType, ActionBase> actionMap;
    private GameService gameService;

    public MessageExecutor() {
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