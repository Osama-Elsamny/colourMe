import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.gameState.GameService;
import org.junit.Test;

public class CloneTest {

    @Test
    public void verifyGameServiceClone() {
        GameConfig config2 = new GameConfig(5, 0.6f, 10);
        GameService service = new GameService();
        GameService clonedService = new GameService();

        service.init(config2);

        for(int i=0; i < 4; i++)
            service.spawnPlayer("test" + i, "127.0.0.1");


        config2.getIpAddresses().forEach(x -> service.spawnPlayer(x.getKey(), x.getValue()));

        try { clonedService = service.clone(); } catch (Exception ex) {}

        boolean isEqual = clonedService.equals(service);
        System.out.println(isEqual);
    }
}
