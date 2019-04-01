import com.colourMe.common.gameState.Cell;
import com.colourMe.common.gameState.CellState;
import com.colourMe.common.gameState.GameConfig;
import org.junit.Before;
import org.junit.Test;
import com.colourMe.common.gameState.GameService;

public class GameServiceTest {
    private GameService gameService;

    private GameConfig gameConfig;

    private String playerId = "testPlayer";

    private String playerIp = "127.0.0.1";

    @Before
    public void init() {
        gameService = new GameService();
        gameConfig = new GameConfig(5, (float) 0.90, 10);
        gameService.init(gameConfig);
    }

    @Test
    public void verifyGameConfigInit() {
        assert (gameService.getGameConfig().equals(gameConfig));
    }

    @Test
    public void verifyAcquireCellSuccess() {
        boolean didAcquire = gameService.acquireCell(0, 0, 0.0, 0.0, playerId);
        assert (didAcquire);
        Cell acquiredCell = gameService.getCell(0, 0);
        assert (acquiredCell.getPlayerID().equals(playerId));
        assert (acquiredCell.getState().equals(CellState.LOCKED));
    }

    @Test
    public void verfiyAcquireCellFailure() {
        // Acquire a cell
        boolean didAcquire = gameService.acquireCell(0, 0, 0.0, 0.0, playerId);
        assert (didAcquire);

        // Try to acquire the same cell again
        String anotherPlayerId = "failureToAcquireCellTest";
        boolean acquireAgain = gameService.acquireCell(0, 0, 0.0, 0.0, anotherPlayerId);
        assert (!acquireAgain);

        Cell failedAcquisitionCell = gameService.getCell(0, 0);
        assert (failedAcquisitionCell.getPlayerID().equals(playerId));
        assert (failedAcquisitionCell.getState().equals(CellState.LOCKED));
    }
}
