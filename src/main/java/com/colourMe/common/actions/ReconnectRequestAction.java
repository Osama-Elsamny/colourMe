package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonElement;

public class ReconnectRequestAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        JsonElement data = gameService.getGson().toJsonTree(gameService);
        return new Message(MessageType.ReconnectResponse, data, null);
    }
}
