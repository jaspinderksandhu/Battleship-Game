package cu.su.test.model;

import cu.su.controller.GameState;
import cu.su.model.CellEntry;
import cu.su.model.PlayerEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameState {

    private GameState getGameState() {
        Map<Integer, List<CellEntry>> map = new HashMap<>();
        List<CellEntry> cellEntries = new ArrayList<>();
        cellEntries.add(new CellEntry(0, 1));
        cellEntries.add(new CellEntry(1, 1));
        cellEntries.add(new CellEntry(2, 1));
        cellEntries.add(new CellEntry(3, 1));
        map.put(1, cellEntries);
        cellEntries = new ArrayList<>();
        cellEntries.add(new CellEntry(5, 2));
        cellEntries.add(new CellEntry(6, 2));
        cellEntries.add(new CellEntry(7, 2));
        map.put(2, cellEntries);
        GameState gameState = new GameState(null);
        gameState.getPlayerEntries().put(1, new PlayerEntry(map));

        map = new HashMap<>();
        cellEntries = new ArrayList<>();
        cellEntries.add(new CellEntry(11, 1));
        cellEntries.add(new CellEntry(12, 1));
        cellEntries.add(new CellEntry(13, 1));
        cellEntries.add(new CellEntry(14, 1));
        map.put(1, cellEntries);
        cellEntries = new ArrayList<>();
        cellEntries.add(new CellEntry(25, 2));
        cellEntries.add(new CellEntry(26, 2));
        cellEntries.add(new CellEntry(27, 2));
        map.put(2, cellEntries);

        gameState.getPlayerEntries().put(2, new PlayerEntry(map));

        return gameState;
    }

    @Test
    public void testNextTurn1() {
        GameState gameState = getGameState();
        int playerid = gameState.getCurrentPlayer();
        gameState.nextTurn();
        assertEquals(3, playerid + gameState.getCurrentPlayer());
    }

    @Test
    public void testNextTurn2() {
        GameState gameState = getGameState();
        int playerid = gameState.getCurrentPlayer();
        gameState.nextTurn();
        gameState.nextTurn();
        assertEquals(playerid, gameState.getCurrentPlayer());
    }

    @Test
    public void testGetShipNumber1() {
        GameState gameState = getGameState();
        int ship = gameState.getShipNumber();
        assertEquals(2, ship);
    }

    @Test
    public void testGetShipNumber2() {
        GameState gameState = getGameState();
        gameState.nextTurn();
        int ship = gameState.getShipNumber();
        assertEquals(2, ship);
    }

    @Test
    public void testGetUserReady() {
        GameState gameState = getGameState();
        assertFalse(gameState.getUserReady());
    }

    @Test
    public void testReset() {
        GameState gameState = getGameState();
        assertEquals(2, gameState.getPlayerEntries().size());
        gameState.reset();
        assertEquals(0, gameState.getPlayerEntries().size());
    }


    @Test
    public void testShot1() {
        GameState gameState = getGameState();
        assertFalse(gameState.shot(11));
        assertFalse(gameState.shot(13));
    }

    @Test
    public void testShot2() {
        GameState gameState = getGameState();
        assertTrue(gameState.shot(2));
        assertTrue(gameState.shot(3));
    }

    @Test
    public void testTryShot1() {
        GameState gameState = getGameState();
        assertFalse(gameState.tryShot(11));
    }

    @Test
    public void testTryShot2() {
        GameState gameState = getGameState();
        gameState.shot(2);
        assertFalse(gameState.tryShot(11));
    }

    @Test
    public void testTryShot3() {
        GameState gameState = getGameState();
        gameState.shot(3);
        assertFalse(gameState.tryShot(3));
    }

    @Test
    public void testTryShot4() {
        GameState gameState = getGameState();
        gameState.shot(3);
        gameState.nextTurn();
        assertTrue(gameState.tryShot(3));
    }
}
