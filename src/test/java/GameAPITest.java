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
        return new Message(MessageType.GetCell, data, playerID);
    }

    private Message cellUpdateRequestMessage() {
        JsonObject data = new JsonObject();
        data.addProperty("row", row);
        data.addProperty("col", col);
        data.addProperty("coordinates", gson.toJson(coordinates));
        return new Message(MessageType.CellUpdateRequest, data, playerID);
    }

    private Message cellReleaseRequestMessage() {
        JsonObject data = new JsonObject();
        data.addProperty("row", row);
        data.addProperty("col", col);
        data.addProperty("hasColoured", hasColoured);
        return new Message(MessageType.ReleaseCell, data, playerID);
    }

    private Message connectResponseMessage() {
        JsonElement data = gson.toJsonTree(gameConfig);
        return new Message(MessageType.ConnectResponse, data, playerID);
    }

    private void initializeCoordinates() {
        for(double i = 0.5; i < 5.0; i += 0.5) {
            for(double j = 0.0; j < 5.0; j += 1.0) {
                coordinates.add(new Coordinate(i, j));
            }
        }
    }
}
