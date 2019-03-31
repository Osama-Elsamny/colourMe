import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.networking.server.GameServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

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

    protected JsonObject getCellData(int row, int col) {
        JsonObject data = new JsonObject();
        data.addProperty("row", row);
        data.addProperty("col", col);
        return data;
    }

    protected JsonObject getCellData(int row, int col, double x, double y) {
        JsonObject data = getCellData(row, col);
        data.addProperty("x", x);
        data.addProperty("y", y);
        return data;
    }

    public Message getDefaultConnectMessage(String id) {
        JsonObject data = new JsonObject();
        data.addProperty("playerIP", LOCALHOST_IP);
        return new Message(MessageType.ConnectRequest, data, id);
    }

    protected Message getDefaultConnectMessage() {
        return getDefaultConnectMessage(DEFAULT_ID);
    }

    protected Message getExpectedConnectResponse() {
        GameConfig config = getDefaultGameConfig();
        config.addplayerConfig(DEFAULT_ID, LOCALHOST_IP);
        return new Message(MessageType.ConnectResponse, gson.toJsonTree(config), DEFAULT_ID);
    }

    protected JsonObject getFirstCellData() {
        return getCellData(0, 0, 0, 0);
    }

    protected JsonObject getLastCellData() {
        return getCellData(DEFAULT_BOARD_SIZE - 1, DEFAULT_BOARD_SIZE - 1, 0, 0);
    }

    protected JsonObject getCellUpdateFirstCellData() {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0.1, 0.1));
        JsonObject data = getCellData(0, 0);
        data.addProperty("coordinates", gson.toJson(coordinates));
        return data;
    }

    protected JsonObject getCellUpdateLastCellData() {
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0, 0));
        JsonObject data = getCellData(DEFAULT_BOARD_SIZE - 1, DEFAULT_BOARD_SIZE - 1);
        data.addProperty("coordinates", gson.toJson(coordinates));
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

    protected JsonObject getFaultyCellData(String faultyField, int value, String[] fields) {
        JsonObject data = new JsonObject();
        for (String field:fields) {
            if (faultyField.equals(field))
                data.addProperty(field, value);
            else data.addProperty(field, 0);
        }
        return data;
    }

    protected Message getResponse(MessageType type, JsonObject data, boolean successful) {
        data.addProperty("successful", successful);
        return new Message(type, data, DEFAULT_ID);
    }

    protected Message getRequest(MessageType type, JsonObject data) {
        return new Message(type, data, DEFAULT_ID);
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
