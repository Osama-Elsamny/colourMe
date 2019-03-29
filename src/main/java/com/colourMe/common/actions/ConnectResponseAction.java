package com.colourMe.common.actions;

import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;

public class ConnectResponseAction extends ActionBase {
    //TODO: verify that everyone ends up with the same IP address list
    @Override
    public Message execute(Message message, GameService gameService) {

        GameConfig data = gameService.getGson()
                .fromJson(message.getData(), GameConfig.class);
        // FIXME: Check if the data is not null before calling gameService.init
        //  data can be null in case of sending connectRequest without playerID.
        gameService.init(data);
        gameService.spawnPlayer(message.getPlayerID(), data.getLastIP());
        return message;
    }
}
