import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class TestClient {
    private int NUM_TRIES = 0;
    private final int RETRY_COUNT = 3;
    private volatile boolean received = true;
    private String user;
    private String testResponse;
    private Session session;

    public void BaseConstructor(String url){
        try {
            URI endpointURI = new URI(url + user);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }


    public TestClient(String url, String user){
        BaseConstructor(url+user);
    }

    public TestClient(String url){
        BaseConstructor(url);
    }

    @OnOpen
    public void onOpen(Session s){
        System.out.println("Connected to server");
        this.session = s;
    }

    @OnMessage
    public void onMessage(String s){
        // Only set testResponse for messages sent by the Client
        if(!received) {
            System.out.println("Received " + s);
            this.testResponse = s;
            received = true;
        }
    }

    @OnClose
    public void onDisconnect(Session session){
        System.out.println("Disconnected from server");
        session = null;
    }

    public String sendMessage(String s){
        try {
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
}
