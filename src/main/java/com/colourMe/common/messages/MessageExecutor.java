package com.colourMe.common.messages;

import com.colourMe.common.actions.*;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;

import java.util.EnumMap;

public class MessageExecutor {
    private EnumMap<MessageType, ActionBase> actionMap;
    private GameService gameService;

    public MessageExecutor(GameService gameService) {
        this.gameService = gameService;
        this.actionMap = new EnumMap<>(MessageType.class);
    }

    public void initGameConfig(GameConfig config){
        this.gameService.init(config);
    }

    public Message processMessage(Message message) {
        return actionMap.get(message.getMessageType()).execute(message, gameService);
    }

    public void buildServerActions() {
        actionMap.put(MessageType.ConnectRequest, new ConnectRequestAction());
        actionMap.put(MessageType.GetCellRequest, new GetCellRequestAction());
        actionMap.put(MessageType.CellUpdateRequest, new CellUpdateRequestAction());
        actionMap.put(MessageType.ReleaseCellRequest, new ReleaseCellRequestAction());
        actionMap.put(MessageType.ClientDisconnectRequest, new ClientDisconnectRequestAction());
        actionMap.put(MessageType.ReconnectRequest, new ReconnectRequestAction());
    }

    public void buildClientActions() {
        actionMap.put(MessageType.Disconnect, new DisconnectAction());
        actionMap.put(MessageType.GetCellResponse, new GetCellResponseAction());
        actionMap.put(MessageType.CellUpdateResponse, new CellUpdateResponseAction());
        actionMap.put(MessageType.ReleaseCellResponse, new ReleaseCellResponseAction());
        actionMap.put(MessageType.ConnectResponse, new ConnectResponseAction());
        actionMap.put(MessageType.ClientDisconnectResponse, new ClientDisconnectResponseAction());
        actionMap.put(MessageType.ReconnectResponse, new ReconnectResponseAction());
        actionMap.put(MessageType.ClockSyncResponse, new ClockSyncResponseAction());
    }
}
