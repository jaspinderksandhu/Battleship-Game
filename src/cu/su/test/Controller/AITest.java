package cu.su.test.Controller;

import cu.su.controller.EnemyAI;
import cu.su.model.CellEntry;
import cu.su.model.PlayerEntry;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AITest {


    EnemyAI enemyAI = new EnemyAI(0);


    @Test
    public void withShip1() {
        enemyAI.reset();
        int count = 0;
        for (int[] ints : enemyAI.getPanel()) {
            for (int i : ints) {
                if (i == 0) {
                    count++;
                }
            }
        }
        assertEquals(5, count);
    }

    @Test
    public void withShip2() {
        enemyAI.reset();
        int count = 0;
        for (int[] ints : enemyAI.getPanel()) {
            for (int i : ints) {
                if (i == 1) {
                    count++;
                }
            }
        }
        assertEquals(4, count);
    }

    @Test
    public void withShip3() {
        enemyAI.reset();
        int count = 0;
        for (int[] ints : enemyAI.getPanel()) {
            for (int i : ints) {
                if (i == 2) {
                    count++;
                }
            }
        }
        assertEquals(3, count);
    }

    @Test
    public void withShip4() {
        enemyAI.reset();
        int count = 0;
        for (int[] ints : enemyAI.getPanel()) {
            for (int i : ints) {
                if (i == 3) {
                    count++;
                }
            }
        }
        assertEquals(3, count);
    }

    @Test
    public void withShip5() {
        enemyAI.reset();
        int count = 0;
        for (int[] ints : enemyAI.getPanel()) {
            for (int i : ints) {
                if (i == 4) {
                    count++;
                }
            }
        }
        assertEquals(2, count);
    }


    @Test
    public void CountShips() {
        enemyAI.reset();
        int count = 0;
        boolean[][] place = enemyAI.getShipPlacements();
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (place[i][j]) {
                    count++;
                }
            }
        }
        assertEquals(17, count);
    }


    @Test
    public void testRunOnce1() {
        enemyAI.reset();

        for (int i = 0; i < 100; i++) {
            assertTrue(enemyAI.runOnce() >= 0);
        }
        assertTrue(enemyAI.runOnce() < 0);
    }

    @Test
    public void testRunOnce2() {
        enemyAI.reset();

        for (int i = 0; i < 100; i++) {
            enemyAI.runOnce();
        }
        int[][] panel = enemyAI.getPanel();
        int count = 0;
        for (int[] ints : panel) {
            for (int i : ints) {
                if (i == -2) {
                    count++;
                }
            }
        }
        assertEquals(100, count);
    }

    @Test
    public void testBuildShip() {
        enemyAI.reset();
        Map<Integer, List<CellEntry>> cellMap = enemyAI.buildShipMap();
        assertEquals(5, cellMap.size());
    }

    @Test
    public void testrebuildShip() {
        enemyAI.reset();
        Map<Integer, List<CellEntry>> cellMap = enemyAI.buildShipMap();
        enemyAI.rebuildPanel(new PlayerEntry(cellMap));
        Map<Integer, List<CellEntry>> cellMap1 = enemyAI.buildShipMap();
        assertEquals(cellMap, cellMap1);
    }

}
