import com.colourMe.common.util.L;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;

public class LogTest {
    @Before
    public void init(){
        L.initLogging();
    }

    @Test
    public void verifyLog(){
        L.log(Level.INFO, this, "verifyLog", "Test message");
        String test = "Test message";
        L.log(Level.INFO, this, "Test message");
        L.info( this, "Test message");
        L.warn(this, "Test message");
        L.severe(this, "Test message");
    }
}
