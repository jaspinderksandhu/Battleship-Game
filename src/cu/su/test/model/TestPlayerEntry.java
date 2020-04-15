package cu.su.test.model;

import cu.su.model.CellEntry;
import cu.su.model.PlayerEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlayerEntry {
    private PlayerEntry getPlayerEntry() {
        Map<Integer, List<CellEntry>> map = new HashMap<>();
        List<CellEntry> cellEntries = new ArrayList<>();
        cellEntries.add(new CellEntry(0, 0));
        cellEntries.add(new CellEntry(1, 0));
        cellEntries.add(new CellEntry(2, 0));
        cellEntries.add(new CellEntry(3, 0));
        map.put(0, cellEntries);
        cellEntries = new ArrayList<>();
        cellEntries.add(new CellEntry(5, 1));
        cellEntries.add(new CellEntry(6, 1));
        cellEntries.add(new CellEntry(7, 1));
        map.put(1, cellEntries);
        return new PlayerEntry(map);
    }

    @Test
    public void testgetPlayTip() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertEquals(0, playerEntry.getPlayTip());
    }

    @Test
    public void testDestoryShip1() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertTrue(playerEntry.destoryShip(1));
    }

    @Test
    public void testDestoryShip2() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertFalse(playerEntry.destoryShip(10));
    }

    @Test
    public void testShop1() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertFalse(playerEntry.shot(10));
    }

    @Test
    public void testShop2() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertTrue(playerEntry.shot(1));
    }

    @Test
    public void testTryShot1() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertFalse(playerEntry.tryShot(1));
    }

    @Test
    public void testTryShot2() {
        PlayerEntry playerEntry = getPlayerEntry();
        assertFalse(playerEntry.tryShot(2));
    }

    @Test
    public void testTryShot3() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(1);
        assertTrue(playerEntry.tryShot(1));
    }

    @Test
    public void testTryShot4() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(2);
        assertTrue(playerEntry.tryShot(2));
    }

    @Test
    public void testTryShot5() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(2);
        assertFalse(playerEntry.tryShot(1));
    }

    @Test
    public void testTryShot6() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(10);
        assertFalse(playerEntry.tryShot(2));
    }

    @Test
    public void testCalcScore1() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(1);
        playerEntry.shot(2);
        playerEntry.shot(3);
        playerEntry.shot(13);
        playerEntry.updateTurnTime(5);
        playerEntry.calcScore(10);
        assertEquals(20, playerEntry.getScore());
    }

    @Test
    public void testCalcScore2() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(1);
        playerEntry.shot(20);
        playerEntry.shot(3);
        playerEntry.shot(13);
        playerEntry.updateTurnTime(8);
        playerEntry.calcScore(300);
        assertEquals(38, playerEntry.getScore());
    }

    @Test
    public void testCalcScore3() {
        PlayerEntry playerEntry = getPlayerEntry();
        playerEntry.shot(6);
        playerEntry.shot(7);
        playerEntry.shot(3);
        playerEntry.shot(20);
        playerEntry.updateTurnTime(800);
        playerEntry.calcScore(999);
        assertEquals(7, playerEntry.getScore());
    }

}
