package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.google.gson.JsonObject;

public class DisconnectAction extends ActionBase {
    public Message execute(Message message, GameService gameService) {
        GameConfig gameConfig = gameService.getGameConfig();

        String nextIP = gameConfig.getNextIP();
        String userIP = gameService.getPlayerIP(message.getPlayerID());

        JsonObject data = new JsonObject();
        data.addProperty("next_IP", nextIP);
        if (nextIP == userIP){
            data.addProperty("start_server", true);
        }else{
            data.addProperty("start_server", false);
        }

        return new Message(MessageType.Disconnect, data, null);
    }
}
