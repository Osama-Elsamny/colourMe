package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.common.util.Log;
import com.colourMe.common.util.U;
import com.google.gson.JsonObject;
import javafx.util.Pair;

public class DisconnectAction extends ActionBase {
    @Override
    public Message execute(Message message, GameService gameService) {
        GameConfig gameConfig = gameService.getGameConfig();

        Log.get(this).info("GameConfig:\n" + U.json(gameConfig));
        Pair<String, String> nextIPPair = gameConfig.getNextIP();
        // String userIP = gameService.getPlayerIP(message.getPlayerID());

        JsonObject data = new JsonObject();
        data.addProperty("nextIP", nextIPPair.getValue());
        if (nextIPPair.getKey().equals(message.getPlayerID())) {
            data.addProperty("startServer", true);
        } else {
            data.addProperty("startServer", false);
        }

        return new Message(MessageType.Disconnect, data, null);
    }
}
