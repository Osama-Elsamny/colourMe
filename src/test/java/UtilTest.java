import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.common.util.Log;
import com.colourMe.common.util.U;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class UtilTest {
    @Before
    public void init(){
        Log.initLogging();
    }

    @Test
    public void verifyLog(){

        String test = "Test message";
        Logger logger = Log.get(this);
        logger.info(test);
        logger.warning(test);
        logger.severe(test);

        logger.info("Sleeping for 200ms");
        U.sleep(200);

        Message message = new Message(MessageType.DefaultType, new JsonObject(), "Test");
        List<Message> messages = Arrays.asList(
                new Message(MessageType.DefaultType, new JsonObject(), "Test"),
                new Message(MessageType.DefaultType, new JsonObject(), "Test2"));

        String jsonObj= U.json(message);
        String jsonArr = U.json(messages);
        JsonObject obj = U.toJsonObject(message);
        JsonArray arr = U.toJsonArray(messages);
        Message deserializedObj = U.fromJson(jsonObj , Message.class);
        List<Message> deserializedArr = Arrays.asList(U.fromJson(jsonArr, Message[].class));

        logger.info("jsonObj: " + jsonObj);
        logger.info("obj: " + obj);
        logger.info("jsonArr: " + jsonArr);
        logger.info("arr: " + arr);
        logger.info("Deserialized Obj: Equal: " + message.equals(deserializedObj));
        logger.info("Deserialized Arr: Equal: " + U.listsAreEqual(messages, deserializedArr));
    }
}
