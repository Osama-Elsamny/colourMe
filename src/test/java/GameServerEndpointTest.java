import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.networking.server.GameServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GameServerEndpointTest extends NetworkingTestBase {
    private TestClient client;

    @Before
    public void init() {
        this.gson = new Gson();
        this.server = new GameServer();
        server.start();

        // Give some time for server to start
        waitTillServerRuns();
        server.initGameService(getDefaultGameConfig());

        this.client = new TestClient(serverAddress);
    }

    @After
    public void end() {
        this.client.disconnect();
        this.server.finish();
        waitTillServerFinishes();
    }

    @Test
    public void verifyConnectActionResponse() {
         Message response = client.sendMessage(getDefaultConnectMessage());
         assert (response.equals(getExpectedConnectResponse()));
    }



    //////////////////////////////// Get Cell Response Tests //////////////////////////////////

    @Test
    public void verifyGetCellActionFirstCell() {
        JsonObject data = getFirstCellData();
        client.sendMessage(getDefaultConnectMessage());
        Message response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, true)));
    }

    @Test
    public void verifyGetCellActionLastCell() {
        JsonObject data = getLastCellData();
        client.sendMessage(getDefaultConnectMessage());
        Message response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, true)));
    }

    @Test
    public void verifyEmptyDataGetCellActionResponse() {
        JsonObject data = new JsonObject();
        client.sendMessage(getDefaultConnectMessage());
        Message response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, false)));
    }

    @Test
    public void verifyGetCellActionFaultyRowField() {
        String faultyField = "row";
        JsonObject data = getFaultyCellData(faultyField, -1);
        client.sendMessage(getDefaultConnectMessage());
        Message response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, false)));

        data = getFaultyCellData("row", 5);
        response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, false)));
    }

    @Test
    public void verifyGetCellActionFaultyColField() {
        String faultyField = "col";
        JsonObject data = getFaultyCellData(faultyField, -1);
        client.sendMessage(getDefaultConnectMessage());
        Message response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, false)));

        data = getFaultyCellData(faultyField, 5);
        response = client.sendMessage(getRequest(MessageType.GetCellRequest, data));
        assert (response.equals(getResponse(MessageType.GetCellResponse, data, false)));
    }

    @Test
    public void verifyCellUpdateResponseFirstCell() {
        JsonObject data = getCellUpdateFirstCellData();
        client.sendMessage(getDefaultConnectMessage());
        client.sendMessage(getRequest(MessageType.GetCellRequest, getFirstCellData()));
        Message response = client.sendMessage(getRequest(MessageType.CellUpdateRequest, data));
        assert (response.equals(getResponse(MessageType.CellUpdateResponse, data, true)));
    }

    //////////////////////////////// Cell Update Response Tests //////////////////////////////////

    @Test
    public void verifyCellUpdateResponseLastCell() {
        JsonObject data = getCellUpdateLastCellData();
        client.sendMessage(getDefaultConnectMessage());
        client.sendMessage(getRequest(MessageType.GetCellRequest, getLastCellData()));
        Message response = client.sendMessage(getRequest(MessageType.CellUpdateRequest, data));
        assert (response.equals(getResponse(MessageType.CellUpdateResponse, data, true)));
    }

    @Test
    public void verifyInvalidCellUpdateResponse() {
        JsonObject data = getCellUpdateLastCellData();
        client.sendMessage(getDefaultConnectMessage());
        client.sendMessage(getRequest(MessageType.GetCellRequest, getFirstCellData()));
        Message response = client.sendMessage(getRequest(MessageType.CellUpdateRequest, data));
        assert (response.equals(getResponse(MessageType.CellUpdateResponse, data, false)));
    }

    @Test
    public void verifyEmptyDataCellUpdateResponse() {
        JsonObject data = new JsonObject();
        client.sendMessage(getDefaultConnectMessage());
        client.sendMessage(getRequest(MessageType.GetCellRequest, getFirstCellData()));
        Message response = client.sendMessage(getRequest(MessageType.CellUpdateRequest, data));
        assert (response.equals(getResponse(MessageType.CellUpdateResponse, data, false)));
    }

    ////////////////////////////////  Server Performance Tests //////////////////////////////////

    @Test
    public void verifySingleMessageDelay() {
        long delay = System.currentTimeMillis();
        Message response = client.sendMessage(getDefaultConnectMessage());
        delay = System.currentTimeMillis() - delay;
        System.out.println("Delay for response: " +  delay);
        assert (delay < DELAY_THRESHOLD) || response == null;
    }

    @Test
    public void verifyMultiClientDelay() {
        long value;
        double avg;
        double sum = 0;
        int NUM_THREADS = 10;
        int NUM_TASKS = 100;

        try {
            ExecutorService service = Executors.newFixedThreadPool(NUM_THREADS);
            List<Callable<Long>> tasks = new ArrayList<>(NUM_TASKS);

            for (int i=0; i< NUM_TASKS; i++)
                tasks.add(this::simulateClientWorkFlow);
            List<Future<Long>> futures = service.invokeAll(tasks);

            for(Future<Long> future: futures){
                value = future.get();
                sum += value;
            }

            avg = sum/NUM_TASKS;
            System.out.println("Average delay of requests : " + avg);
            assert (avg < MULTI_DELAY_THRESHOLD);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            assert (false);
        }
    }

    private Long simulateClientWorkFlow() {
        TestClient randClient = generateRandomClient();
        long delay = System.currentTimeMillis();
        Message response = randClient.sendMessage(getDefaultConnectMessage(randClient.getClientId()));
        delay = System.currentTimeMillis() - delay;
        System.out.println(Thread.currentThread().getName() + ": Delay -> " + delay);
        randClient.disconnect();
        return response != null ? delay : null;
    }

    private TestClient generateRandomClient() {
        // Used to generate unique user id
        String id = "" + System.currentTimeMillis();
        return new TestClient(this.baseAddress, id);
    }


}