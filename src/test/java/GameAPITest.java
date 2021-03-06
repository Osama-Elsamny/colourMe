import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageExecutor;
import com.colourMe.common.messages.MessageType;
import com.colourMe.gui.GameAPI;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import static org.junit.Assert.*;

public class GameAPITest {
    private Gson gson = new Gson();

    private PriorityBlockingQueue<Message> sendQueue;

    private PriorityBlockingQueue<Message> receivedQueue;

    private GameService gameService;

    private GameConfig gameConfig = new GameConfig(10, (float) 0.90, 10);

    private MessageExecutor messageExecutor;

    private GameAPI gameAPI;

    private String playerID = "testPlayer";

    private String playerIP = "127.0.0.1";

    private int row = 0;

    private int col = 0;

    private double x = 1.0;

    private double y = 1.0;

    private boolean hasColoured = true;

    private Coordinate coordinate = new Coordinate(1.0, 1.0);

    private List<Coordinate> coordinates = new ArrayList<>();

    @Before
    public void init() {
        sendQueue = new PriorityBlockingQueue<>(10, Message.messageComparator);
        receivedQueue = new PriorityBlockingQueue<>(10, Message.messageComparator);
        gameAPI = new GameAPI(sendQueue, receivedQueue);
        gameService = new GameService();
        messageExecutor = new MessageExecutor(gameService);
        messageExecutor.initGameConfig(gameConfig);
        messageExecutor.buildServerActions();
        this.initializeCoordinates();
    }

    @Test
    public void verifySendConnectRequest() {
        assertTrue(sendQueue.isEmpty());

        boolean successful = gameAPI.sendConnectRequest(playerID, playerIP);
        assertTrue(successful);

        assertFalse(sendQueue.isEmpty());
        Message request = sendQueue.poll();

        assertEquals(connectRequestMessage(), request);
    }

    @Test
    public void verifySendGetCellRequest() {
        assertTrue (sendQueue.isEmpty());

        boolean successful = gameAPI.sendGetCellRequest(playerID, row, col, coordinate);
        assertTrue(successful);

        assertFalse(sendQueue.isEmpty());
        Message request = sendQueue.poll();

        assertEquals(getCellRequestMessage(), request);
    }

    @Test
    public void verifySendCellUpdateRequest() {
        assertTrue(sendQueue.isEmpty());

        boolean successful = gameAPI.sendCellUpdateRequest(playerID, row, col, coordinates);
        assertTrue(successful);

        assertFalse(sendQueue.isEmpty());
        Message request = sendQueue.poll();

        assertEquals(cellUpdateRequestMessage(), request);
    }

    @Test
    public void verifySendReleaseCellRequest() {
        assertTrue(sendQueue.isEmpty());

        boolean successful = gameAPI.sendReleaseCellRequest(playerID, row, col, hasColoured);
        assertTrue(successful);

        assertFalse(sendQueue.isEmpty());
        Message request = sendQueue.poll();

        assertEquals(cellReleaseRequestMessage(), request);
    }

    // Workflow tests, sending a request and expecting valid response from gameAPI processResponse
    @Test
    public void verifyConnectResponseAction() {
        gameAPI.sendConnectRequest(playerID, playerIP);

        assertEquals(sendQueue.size(), 1);
        Message response = messageExecutor.processMessage(sendQueue.poll());

        receivedQueue.put(response);
        assertTrue(gameAPI.hasResponse());
        Message processedResponse = gameAPI.processResponse();

        assertEquals(processedResponse, connectResponseMessage());
        assertEquals(gameConfig, gameAPI.getGameConfig());
    }

    @Test
    public void verifyGetCellResponseAction() {
        connect();
        gameAPI.sendGetCellRequest(playerID, row, col, coordinate);

        assertEquals(sendQueue.size(), 1);
        Message response = messageExecutor.processMessage(sendQueue.poll());

        receivedQueue.put(response);
        assertTrue(gameAPI.hasResponse());
        Message processedResponse = gameAPI.processResponse();

        assertTrue(gameAPI.playerOwnsCell(row, col, playerID));
        assertEquals(processedResponse, getCellResponseMessage(getCellRequestMessage(), true));
    }

    @Test
    public void verifyCellUpdateResponseAction() {
        connect();
        acquireCell();
        gameAPI.sendCellUpdateRequest(playerID, row, col, coordinates);

        assertEquals(sendQueue.size(), 1);
        Message response = messageExecutor.processMessage(sendQueue.poll());

        receivedQueue.put(response);
        assertTrue(gameAPI.hasResponse());
        Message processedResponse = gameAPI.processResponse();

        assertEquals(processedResponse, cellUpdateResponseMessage(true));
    }

    @Test
    public void verifyCellReleaseResponseAction() {
        connect();
        acquireCell();
        gameAPI.sendReleaseCellRequest(playerID, row, col, hasColoured);

        assertEquals(sendQueue.size(), 1);
        Message response = messageExecutor.processMessage(sendQueue.poll());

        receivedQueue.put(response);
        assertTrue(gameAPI.hasResponse());
        Message processedResponse = gameAPI.processResponse();

        assertFalse(gameAPI.playerOwnsCell(row, col, playerID));
        assertEquals(processedResponse, cellReleaseResponseMessage(true));
    }

    private Message connectRequestMessage() {
        JsonObject data = new JsonObject();
        data.addProperty("playerIP", playerIP);
        return new Message(MessageType.ConnectRequest, data, playerID);
    }

    private Message getCellRequestMessage() {
        JsonObject data = new JsonObject();
        data.addProperty("row", row);
        data.addProperty("col", col);
        data.addProperty("x", x);
        data.addProperty("y", y);
        return new Message(MessageType.GetCellRequest, data, playerID);
    }

    private Message getCellResponseMessage() {
        Message responseMessage = getCellRequestMessage();
        responseMessage.getData().getAsJsonObject().addProperty("successful", true);
        responseMessage.setMessageType(MessageType.GetCellResponse);
        return responseMessage;
    }

    private Message cellUpdateRequestMessage() {
        JsonObject data = new JsonObject();
        data.addProperty("row", row);
        data.addProperty("col", col);
        data.add("coordinates", gson.toJsonTree(coordinates));
        return new Message(MessageType.CellUpdateRequest, data, playerID);
    }

    private Message cellUpdateResponseMessage(boolean success) {
        Message responseMessage = cellUpdateRequestMessage();
        responseMessage.setMessageType(MessageType.CellUpdateResponse);
        responseMessage.getData().getAsJsonObject().addProperty("successful", success);
        return responseMessage;
    }

    private Message cellReleaseRequestMessage() {
        JsonObject data = new JsonObject();
        data.addProperty("row", row);
        data.addProperty("col", col);
        data.addProperty("hasColoured", hasColoured);
        return new Message(MessageType.ReleaseCellRequest, data, playerID);
    }

    private Message cellReleaseResponseMessage(boolean success) {
        Message responseMessage = cellReleaseRequestMessage();
        responseMessage.setMessageType(MessageType.ReleaseCellResponse);
        responseMessage.getData().getAsJsonObject().addProperty("successful", success);
        return responseMessage;
    }

    private Message connectResponseMessage() {
        JsonElement data = gson.toJsonTree(gameConfig);
        data.getAsJsonObject().addProperty("successful", true);
        return new Message(MessageType.ConnectResponse, data, playerID);
    }

    private Message getCellResponseMessage(Message message, boolean success) {
        message.getData().getAsJsonObject().addProperty("successful", success);
        message.setMessageType(MessageType.GetCellResponse);
        return message;
    }

    private void initializeCoordinates() {
        for(double i = 0.5; i < 5.0; i += 0.5) {
            for(double j = 0.0; j < 5.0; j += 1.0) {
                coordinates.add(new Coordinate(i, j));
            }
        }
    }

    private void connect() {
        messageExecutor.processMessage(connectRequestMessage());
        receivedQueue.put(connectResponseMessage());
        gameAPI.processResponse();
    }

    private void acquireCell() {
        messageExecutor.processMessage(getCellRequestMessage());
        receivedQueue.put(getCellResponseMessage());
        gameAPI.processResponse();
    }
}
