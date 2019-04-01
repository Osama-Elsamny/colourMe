import com.colourMe.common.gameState.Cell;
import com.colourMe.common.gameState.CellState;
import com.colourMe.common.gameState.GameConfig;
import org.junit.Before;
import org.junit.Test;
import com.colourMe.common.gameState.GameService;

import static org.junit.Assert.*;

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
        gameService.spawnPlayer(playerId, playerIp);
    }

    @Test
    public void verifyGameConfigInit() {
        assert (gameService.getGameConfig().equals(gameConfig));
    }

    @Test
    public void verifyAcquireCellSuccess() {
        boolean didAcquire = gameService.acquireCell(0, 0, 0.0, 0.0, playerId);
        assertTrue (didAcquire);
        Cell acquiredCell = gameService.getCell(0, 0);
        assertEquals (acquiredCell.getPlayerID(), playerId);
        assertEquals (acquiredCell.getState(), CellState.LOCKED);
    }

    @Test
    public void verfiyAcquireCellFailure() {
        // Acquire a cell
        boolean didAcquire = gameService.acquireCell(0, 0, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        // Try to acquire the same cell again
        String anotherPlayerId = "failureToAcquireCellTest";
        boolean acquireAgain = gameService.acquireCell(0, 0, 0.0, 0.0, anotherPlayerId);
        assertFalse (acquireAgain);

        Cell failedAcquisitionCell = gameService.getCell(0, 0);
        assertEquals (failedAcquisitionCell.getPlayerID(), playerId);
        assertEquals (failedAcquisitionCell.getState(), CellState.LOCKED);
    }

    @Test
    public void verifyReleaseColouredCell() {
        boolean didAcquire = gameService.acquireCell(1, 0, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        boolean didRelease = gameService.releaseCell(1, 0, playerId, true);
        assertTrue (didRelease);

        Cell releasedCell = gameService.getCell(1, 0);
        assertEquals (releasedCell.getState(), CellState.COLOURED);
    }

    @Test
    public void verifyReleaseUnColouredCell() {
        boolean didAcquire = gameService.acquireCell(1, 0, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        boolean didRelease = gameService.releaseCell(1, 0, playerId, false);
        assertTrue (didRelease);

        Cell releasedCell = gameService.getCell(1, 0);
        assertEquals (releasedCell.getState(), CellState.AVAILABLE);
    }

    @Test
    public void verifyReleaseCellFailure() {
        boolean didAcquire = gameService.acquireCell(1, 0, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        boolean didRelease = gameService.releaseCell(1, 1, playerId, true);
        assertFalse (didRelease);
    }

    @Test
    public void verifyValidCellOwnerWithColouring() {
        boolean didAcquire = gameService.acquireCell(0, 1, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        assertTrue (gameService.validCellOwner(0, 1, playerId));

        boolean didRelease = gameService.releaseCell(0, 1, playerId, false);
        assertTrue (didRelease);

        assertFalse (gameService.validCellOwner(0, 1, playerId));
    }

    @Test
    public void verifyValidCellOwnerWithoutColouring() {
        boolean didAcquire = gameService.acquireCell(0, 1, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        assertTrue (gameService.validCellOwner(0, 1, playerId));

        boolean didRelease = gameService.releaseCell(0, 1, playerId, true);
        assertTrue (didRelease);

        assertFalse (gameService.validCellOwner(0, 1, playerId));
    }

    @Test (expected = IllegalStateException.class)
    public void getNonExistentPlayerScore() {
        String testId = "nonExistentPlayerId";
        gameService.getPlayerScore(testId);
    }

    @Test
    public void verifyKillPlayer() {
        boolean didAcquire = gameService.acquireCell(1, 1, 0.0, 0.0, playerId);
        assertTrue (didAcquire);

        Cell acquiredCell = gameService.getCell(1, 1);
        assertEquals (acquiredCell.getPlayerID(), playerId);
        assertEquals (acquiredCell.getState(), CellState.LOCKED);

        gameService.killPlayer(playerId);
        assertEquals (acquiredCell.getPlayerID(), "");
        assertEquals (acquiredCell.getState(), CellState.AVAILABLE);
    }
}
