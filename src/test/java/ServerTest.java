
import com.colourMe.game.GameConfig;
import com.colourMe.messages.Message;
import com.colourMe.messages.MessageType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ServerTest{
    private Gson gson;
    public static final int DELAY_THRESHOLD = 50;
    private static final String baseAddress = "ws://127.0.0.1:8080/connect/";
    private static final String serverAddress = "ws://127.0.0.1:8080/connect/test";
    private TestClient client;
    private TestServer server;

    @Before
    public void init() {
        this.gson = new Gson();
        this.server = new TestServer();
        server.start();

        // Give some time for server to start
        try {Thread.sleep(10); } catch (Exception ex) {}

        this.client = new TestClient(serverAddress);
    }

    @After
    public void end() {
        this.client.disconnect();
        this.server.finish();
        try { Thread.sleep(1000); } catch(Exception ex) {}
    }

    public String getDefaultInitMessage(){

        Message message = new Message(MessageType.InitRequest, null, "127.0.0.1");
        GameConfig config = new GameConfig(10, 5, 10, new ArrayList<>());
        message.setData(gson.toJsonTree(config));
        return gson.toJson(message);
    }

    public String getExpectedInitResponse(){
        JsonObject response = new JsonObject();
        response.addProperty("messageType", MessageType.InitResponse.name());
        response.addProperty("data", "{\"successful\": true}");
        return response.getAsJsonObject().toString();
    }

    @Test
    public void verifyInitActionResponse(){

         String response = client.sendMessage(getDefaultInitMessage());
         assert (response.equals(getExpectedInitResponse()));
    }

    @Test
    public void verifyInitActionDelay(){
        long delay = System.currentTimeMillis();
        String response = client.sendMessage(getDefaultInitMessage());
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
            assert (avg < DELAY_THRESHOLD);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            assert (false);
        }
    }

    private Long simulateClientWorkFlow() {
        TestClient randClient = generateRandomClient();
        long delay = System.currentTimeMillis();
        String response = randClient.sendMessage(getDefaultInitMessage());
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
        return new TestClient(this.baseAddress + id);
    }


}
