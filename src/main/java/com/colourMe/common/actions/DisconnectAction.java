package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class DisconnectAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        GameConfig gameConfig = gameService.getGameConfig();


        String nextIP = gameConfig.getNextIP();
        String userIP = gameService.getPlayerIP(message.getPlayerID());

        JsonObject data = new JsonObject();
        data.addProperty("nextIP", nextIP);
        if (nextIP.equals(userIP)){
            data.addProperty("startServer", true);
        }else{
            data.addProperty("startServer", false);
        }

        return new Message(MessageType.Disconnect, data, null);
    }
}
