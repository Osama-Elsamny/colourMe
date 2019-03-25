package com.colourMe.common.messages;

import com.colourMe.common.actions.ActionBase;
import com.colourMe.common.actions.ConnectRequestAction;
import com.colourMe.common.gameState.GameService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.EnumMap;

public class MessageExecutor {
    private Gson gson = new Gson();
    private EnumMap<MessageType, ActionBase> actionMap;
    private GameService gameService;

    public MessageExecutor() {
        this.gameService = new GameService();
        this.actionMap = new EnumMap<>(MessageType.class);
        buildActions();
    }

    public JsonElement processMessage(Message message) {
        Message response = actionMap.get(message.getMessageType()).execute(message, gameService);
        return gson.toJsonTree(response);
    }

    private void buildActions() {
        actionMap.put(MessageType.ConnectRequest, new ConnectRequestAction());
    }
}