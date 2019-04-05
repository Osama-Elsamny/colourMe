import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import org.junit.Test;

public class CloneTest {

    @Test
    public void verifyGameServiceClone() {
        GameConfig config = new GameConfig(5, 0.6f, 10);
        GameConfig config2 = new GameConfig(5, 0.6f, 10);
        GameService service = new GameService();

        for(int i=0; i < 4; i++)
            config2.addplayerConfig("test" + i, "127.0.0.1");

        config2.getIpAddresses().forEach(x -> service.spawnPlayer(x.getKey(), x.getValue()));
    }
}
