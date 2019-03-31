import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.networking.server.GameServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class NetworkingTestBase {
    protected Gson gson;
    protected static final int DEFAULT_BOARD_SIZE = 5;
    protected static final String LOCALHOST_IP = "127.0.0.1";
    protected static final int MULTI_DELAY_THRESHOLD = 500;
    protected static final int DELAY_THRESHOLD = 100;
    protected static final String DEFAULT_ID = "test";
    protected static final String baseAddress = "ws://127.0.0.1:8080/connect/";
    protected static final String serverAddress = "ws://127.0.0.1:8080/connect/" + DEFAULT_ID;
    GameServer server;

    protected GameConfig getDefaultGameConfig() {
        return new GameConfig(DEFAULT_BOARD_SIZE, (float) 0.9, 10);
    }

    public Message getDefaultConnectMessage(String id) {
        Message message = new Message(MessageType.ConnectRequest, null, id);
        JsonObject data = new JsonObject();
        data.addProperty("playerIP", LOCALHOST_IP);
        message.setData(data);
        return message;
    }

    protected Message getDefaultConnectMessage() {
        return getDefaultConnectMessage(DEFAULT_ID);
    }

    protected Message getExpectedConnectResponse() {
        GameConfig config = getDefaultGameConfig();
        config.addplayerConfig(DEFAULT_ID, LOCALHOST_IP);
        Message response = new Message(MessageType.ConnectResponse, null, DEFAULT_ID);
        response.setData(gson.toJsonTree(config));
        return response;
    }

    protected JsonObject getFirstCellData() {
        JsonObject data = new JsonObject();
        data.addProperty("row", 0);
        data.addProperty("col", 0);
        data.addProperty("x", 0);
        data.addProperty("y", 0);
        return data;
    }

    protected JsonObject getLastCellData() {
        JsonObject data = new JsonObject();
        data.addProperty("row", DEFAULT_BOARD_SIZE - 1);
        data.addProperty("col", DEFAULT_BOARD_SIZE - 1);
        data.addProperty("x", 0);
        data.addProperty("y", 0);
        return data;
    }

    protected JsonObject getFaultyCellData(String faultyField, int value) {
        JsonObject data = new JsonObject();
        String[] fields = new String[] {"row", "col", "x", "y"};
        for (String field:fields) {
            if (faultyField.equals(field))
                data.addProperty(field, value);
            else data.addProperty(field, 0);
        }
        return data;
    }

    protected Message getFaultyGetCellRequest(String faultyField, int value) {
        return new Message(MessageType.GetCellRequest, getFaultyCellData(faultyField, value), DEFAULT_ID);
    }

    protected Message getGetCellRequest(JsonObject data) {

        return new Message(MessageType.GetCellRequest, data, DEFAULT_ID);
    }

    protected Message getCellResponse(JsonObject data, boolean successful) {
        data.addProperty("successful", successful);
        return new Message(MessageType.GetCellResponse, data, DEFAULT_ID);
    }

    // HELPER Functions
    public void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void waitTillServerRuns(){
        while(!server.isRunning()) {}
    }

    public void waitTillServerFinishes(){
        while(server.isRunning()) {}
    }
}
