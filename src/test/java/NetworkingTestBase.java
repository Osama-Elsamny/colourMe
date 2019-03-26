import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.networking.server.GameServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class NetworkingTestBase {
    protected Gson gson;
    protected static final String LOCALHOST_IP = "127.0.0.1";
    protected static final int MULTI_DELAY_THRESHOLD = 150;
    protected static final int DELAY_THRESHOLD = 50;
    protected static final String DEFAULT_ID = "test";
    protected static final String baseAddress = "ws://127.0.0.1:8080/connect/";
    protected static final String serverAddress = "ws://127.0.0.1:8080/connect/" + DEFAULT_ID;
    GameServer server;

    protected GameConfig getDefaultGameConfig(){
        return new GameConfig(5, (float) 0.9, 10, new ArrayList<>());
    }

    public Message getDefaultConnectMessage(String id){
        Message message = new Message(MessageType.ConnectRequest, null, id);
        JsonObject data = new JsonObject();
        data.addProperty("ip", LOCALHOST_IP);
        message.setData(data);
        return message;
    }

    protected Message getDefaultConnectMessage(){

        return getDefaultConnectMessage(DEFAULT_ID);
    }

    protected Message getExpectedConnectResponse(){
        GameConfig config = getDefaultGameConfig();
        config.addIp(LOCALHOST_IP);
        Message response = new Message(MessageType.ConnectResponse, null, DEFAULT_ID);
        response.setData(gson.toJsonTree(config));
        return response;
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
