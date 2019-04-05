import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import org.junit.Test;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import static junit.framework.TestCase.assertEquals;

public class PQueueTest {
    private static Comparator<Message> messageComparator = (m1, m2) ->
            (int) (m1.getTimestamp() - m2.getTimestamp());

    @Test
    public void test() {
       PriorityBlockingQueue<Message> incoming = new PriorityBlockingQueue<>(10, messageComparator);
       Message message1 = new Message(MessageType.ConnectRequest, null, null);
       message1.setTimestamp(10000);

       Message message2 = new Message(MessageType.Disconnect, null, null);
       message2.setTimestamp(20000);

       incoming.add(message2);
       incoming.add(message1);
       assertEquals(MessageType.ConnectRequest, incoming.remove().getMessageType());
       assertEquals(MessageType.Disconnect, incoming.remove().getMessageType());
    }
}
