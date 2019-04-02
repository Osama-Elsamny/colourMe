import com.colourMe.common.messages.Message;
import com.google.gson.Gson;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class TestClient {

    private volatile boolean received = true;
    private String id = "test";
    private Message testResponse;
    private Session session;
    private Gson gson = new Gson();

    public void BaseConstructor(String url){
        try {
            URI endpointURI = new URI(url);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public TestClient(String url, String id){
        this.id = id;
        BaseConstructor(url + id);
    }

    public TestClient(String url){
        BaseConstructor(url);
    }

    @OnOpen
    public void onOpen(Session s){
        System.out.println("Connected to server with id: " + id);
        this.session = s;
    }

    @OnMessage
    public void onMessage(String s){
        // Only set testResponse for messages sent by the Client
        Message message = gson.fromJson(s, Message.class);
        if(message.getPlayerID().equals(id)) {
            System.out.println("Received " + s);
            this.testResponse = message;
            received = true;
        }
    }

    @OnClose
    public void onDisconnect(Session session){
        System.out.println("Disconnected from server");
        session = null;
    }

    public Message sendMessage(Message message){
        try {
            String s = gson.toJson(message);
            this.received = false;
            this.session.getBasicRemote().sendText(s);
            while(!received) {}
            return testResponse;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    public void disconnect(){
        try {
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String getClientId(){
        return id;
    }

}
