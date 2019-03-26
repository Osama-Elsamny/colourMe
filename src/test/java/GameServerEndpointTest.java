
import com.colourMe.common.gameState.GameConfig;
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
import java.util.Random;
import java.util.concurrent.*;

public class GameServerEndpointTest extends NetworkingTestBase {
    TestClient client;

    @Before
    public void init() {
        this.gson = new Gson();
        this.server = new GameServer();
        server.start();

        // Give some time for server to start
        try {Thread.sleep(100); } catch (Exception ex) {}
        server.initGameService(getDefaultGameConfig());

        this.client = new TestClient(serverAddress);
    }

    @After
    public void end() {
        this.client.disconnect();
        this.server.finish();
        try { Thread.sleep(1000); } catch(Exception ex) {}
    }

    @Test
    public void verifyInitActionResponse(){
         Message response = client.sendMessage(getDefaultConnectMessage());
         assert (response.equals(getExpectedConnectResponse()));
    }

    @Test
    public void verifyInitActionDelay(){
        long delay = System.currentTimeMillis();
        Message response = client.sendMessage(getDefaultConnectMessage());
        delay = System.currentTimeMillis() - delay;
        System.out.println("Delay for response: " +  delay);
        assert (delay < DELAY_THRESHOLD) || response == null;
    }

    @Test
    public void verifyMultiClientDelay(){
        long value;
        double avg;
        double sum = 0;
        int NUM_THREADS = 10;
        int NUM_TASKS = 1000;

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
        Random random = new Random();
        long x = random.nextLong();
        long y = random.nextLong();
        String id = "" + x + y;
        return new TestClient(this.baseAddress, id);
    }


}