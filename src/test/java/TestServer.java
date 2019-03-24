
import com.colourMe.networking.server.GameServerEndpoint;
import org.glassfish.tyrus.server.Server;


public class TestServer extends Thread {

    private volatile boolean finished = false;

    public void finish() {
        this.finished = true;
    }

    @Override
    public void run() {

        Server server = new Server("localhost", 8080, "",
                null, GameServerEndpoint.class);
        try{
            server.start();
            System.out.println("Test GameServer has started!");
            while(! finished) {};
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            server.stop();
        }

    }
}