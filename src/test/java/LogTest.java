import com.colourMe.common.util.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

public class LogTest {
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
    }
}
