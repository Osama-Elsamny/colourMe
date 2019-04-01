import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.gui.GameAPI;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.PriorityBlockingQueue;

import static org.junit.Assert.*;

public class GameAPITest {
    private PriorityBlockingQueue<Message> sendQueue;

    private PriorityBlockingQueue<Message> receivedQueue;

    private GameAPI gameAPI;

    private String playerID = "testPlayer";

    private String playerIP = "127.0.0.1";

    @Before
    public void init() {
        sendQueue = new PriorityBlockingQueue<>(10, Message.messageComparator);
        receivedQueue = new PriorityBlockingQueue<>(10, Message.messageComparator);
        gameAPI = new GameAPI(sendQueue, receivedQueue);
    }

    @Test
    public void verifySendConnectRequest() {
        assertTrue (sendQueue.isEmpty());

        boolean successful = gameAPI.sendConnectRequest(playerID, playerIP);
        assertTrue (successful);

        assertFalse (sendQueue.isEmpty());
        Message request = sendQueue.poll();
        assertEquals (request.getMessageType(), MessageType.ConnectRequest);
        assertEquals (request.getData().getAsJsonObject().get("playerIP").getAsString(), playerIP);
        assertEquals (request.getPlayerID(), playerID);
    }
}
