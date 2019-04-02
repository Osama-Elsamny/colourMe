import com.colourMe.networking.server.GameServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameServerTest extends NetworkingTestBase{
    // Milliseconds
    private final long MAX_SERVER_START_TIME = 1000;
    private final long MAX_SERVER_SHUTDOWN_TIME = 1000;

    @Before
    public void init(){
        this.server = new GameServer();
    }

    @After
    public void end(){
        server.finish();
        waitTillServerFinishes();
        server = null;
    }

    @Test
    public void verifyRunningForUnStartedServer(){
        assert(! server.isRunning());
    }

    @Test
    public void verifyServerStart(){
        server.start();
        sleep(MAX_SERVER_START_TIME);
        assert(server.isRunning());
        server.finish();
    }

    @Test
    public void verifyServerFinish(){
        server.start();
        server.finish();
        sleep(MAX_SERVER_SHUTDOWN_TIME);
        assert(!server.isRunning());
    }

    @Test
    public void verifyServerStartTime(){
        long time = System.currentTimeMillis();
        server.start();
        waitTillServerRuns();
        time = System.currentTimeMillis() - time;
        System.out.println("Took " + time + "ms to start server");
        assert(time < MAX_SERVER_START_TIME);
    }

    @Test
    public void verifyServerEndTime(){
        server.start();
        waitTillServerRuns();

        long time = System.currentTimeMillis();
        server.finish();
        waitTillServerFinishes();
        time = System.currentTimeMillis() - time;

        System.out.println("Took " + time + "ms to stop server");
        assert(time < MAX_SERVER_SHUTDOWN_TIME);
    }
}
