package com.colourMe.gui;

import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.messages.Message;
import javafx.scene.paint.Color;

import java.util.List;

public class gameAPI {
    //Requests
    boolean sendConnectRequest(GameConfig gameConfig){return false;}
    boolean sendCellRequest(int row, int col, Coordinate coordinate){return false;}
    boolean sendCellUpdateRequest(int row, int col, List coordinates){return false;}
    boolean sendReleaseCellRequest(int row, int col, boolean isColoured){return false;}
    boolean sendClientDisconnectRequest(String playerID){return false;}
    //Responses
    Message getConnectResponse(GameConfig gameConfig){return null;}
    Message getCellResponse(int row, int col, Coordinate coordinate){return null;}
    Message getCellUpdateResponse(int row, int col, List coordinates){return null;}
    Message getReleaseCellResponse(int row, int col, boolean isColoured){return null;}
    Message getClientDisconnectResponse(){return null;}
    Message getServerDisconnectResponse(){return null;}
    Message getGameEndResponse(){return null;}
    //Free functions
    int getNumOfPlayers(){return 0;}
    String[] getPlayerNames(){return null;}
    int getPlayerColourCode(String playerID){return 0;}
    Color getPlayerColour(String playerID){return null;}
    int getPlayerScore(String playerID){return 0;}
    boolean hasResponse(){return false;}
    Message getResponse(){return null;}
}
