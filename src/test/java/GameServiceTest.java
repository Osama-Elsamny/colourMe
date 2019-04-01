import com.colourMe.common.gameState.GameConfig;
import org.junit.Before;
import org.junit.Test;
import com.colourMe.common.gameState.GameService;

public class GameServiceTest {
    private GameService gameService;

    private GameConfig gameConfig;

    @Before
    public void init() {
        gameService = new GameService();
        gameConfig = new GameConfig(5, (float) 0.90, 10);
        gameService.init(gameConfig);
    }
}
