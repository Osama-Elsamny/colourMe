package com.colourMe.common.messages;

import com.colourMe.common.actions.ActionBase;
import com.colourMe.common.actions.ConnectRequestAction;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.actions.DisconnectAction;
import com.colourMe.common.actions.GetCellRequestAction;
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
    }

    public void initGameConfig(GameConfig config){
        this.gameService.init(config);
    }

    public JsonElement processMessage(Message message) {
        Message response = actionMap.get(message.getMessageType()).execute(message, gameService);
        return gson.toJsonTree(response);
    }

    public void buildServerActions() {
        actionMap.put(MessageType.ConnectRequest, new ConnectRequestAction());
        actionMap.put(MessageType.GetCellRequest, new GetCellRequestAction());
    }

    public void buildClientAction() {
        actionMap.put(MessageType.Disconnect, new DisconnectAction());
    }
}