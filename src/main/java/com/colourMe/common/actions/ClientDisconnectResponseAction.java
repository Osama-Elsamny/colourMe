package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;

public class ClientDisconnectResponseAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        String playerID = message.getPlayerID();
        gameService.killPlayer(playerID);
        return message;
    }
}
