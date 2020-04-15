package cu.su.controller;

import cu.su.model.CellEntry;
import cu.su.model.PlayerEntry;
import cu.su.view.component.Ship;
import javafx.geometry.Point2D;

import java.util.*;
import java.util.stream.Collectors;

import static cu.su.Utils.BOARD_CELL_NUM_PER_SIDE;
import static cu.su.Utils.isConflict;

/**
 * EnemyAI class is the ai of the game. It has two methods, putShips is used to generate random locations
 * of ships, runOnce is used to execute one step of AI's algorithm.
 *
 * @author k4
 * @see EnemyAI#runOnce()
 */
public class EnemyAI {

    /**
     * The stack is used to store the stack when AI finds a ship cell.
     */
    private Stack<Point2D> cellStack;
    private Random random;
    private boolean[][] shipPlacements;
    private int[][] panel;

    public EnemyAI(int seed) {
        random = new Random(seed);
    }

    public EnemyAI() {
        random = new Random();

    }

    public int[][] getPanel() {
        return panel;
    }

    public boolean[][] getShipPlacements() {
        return shipPlacements;
    }

    public void reset() {
        cellStack = new Stack<>();
        shipPlacements = new boolean[BOARD_CELL_NUM_PER_SIDE][BOARD_CELL_NUM_PER_SIDE];
        panel = new int[BOARD_CELL_NUM_PER_SIDE][BOARD_CELL_NUM_PER_SIDE];
        putShips();
    }

    /**
     * must called after buildShipMap
     *
     * @param playerEntry checks whether player entry done is valid or no.
     */
    public void rebuildPanel(PlayerEntry playerEntry) {
        shipPlacements = new boolean[BOARD_CELL_NUM_PER_SIDE][BOARD_CELL_NUM_PER_SIDE];
        panel = new int[BOARD_CELL_NUM_PER_SIDE][BOARD_CELL_NUM_PER_SIDE];
        for (int[] ints : panel) {
            Arrays.fill(ints, -1);
        }
        for (List<CellEntry> cellEntries : playerEntry.getCellEntryMap().values()) {
            for (CellEntry cellEntry : cellEntries) {
                int index = cellEntry.getIndex();
                int x = index / BOARD_CELL_NUM_PER_SIDE;
                int y = index % BOARD_CELL_NUM_PER_SIDE;
                panel[x][y] = cellEntry.getShipIndex();
                shipPlacements[x][y] = true;
            }
        }
    }

    /**
     * AI will put ships randomly.
     *
     * @since 1.0
     */
    public void putShips() {
        for (int[] ints : panel) {
            Arrays.fill(ints, -1);
        }
        buildRandomShip(0, 5);
        buildRandomShip(1, 4);
        buildRandomShip(2, 3);
        buildRandomShip(3, 3);
        buildRandomShip(4, 2);

    }

    public Map<Integer, List<CellEntry>> buildShipMap() {
        Map<Integer, List<CellEntry>> shipCellMap = new HashMap<>();
        for (int i = 0; i < BOARD_CELL_NUM_PER_SIDE; i++) {
            for (int j = 0; j < BOARD_CELL_NUM_PER_SIDE; j++) {
                if (panel[i][j] != -1) {
                    int shipIndex = panel[i][j];
                    if (!shipCellMap.containsKey(shipIndex)) {
                        shipCellMap.put(shipIndex, new ArrayList<>());
                    }
                    CellEntry cellEntry = new CellEntry(i * BOARD_CELL_NUM_PER_SIDE + j, panel[i][j]);
                    shipCellMap.get(shipIndex).add(cellEntry);
                }
            }
        }
        return shipCellMap;
    }


    /**
     * The function is used to generate the location of one ship randomly
     * @param index checks the index of ship.
     * @param len the length of ship.
     */
    public void buildRandomShip(int index, int len) {
        int orientation;
        int x, y;
        orientation = new Random().nextInt(2);

        while (true) {
            if (orientation == Ship.VERTICAL_SHIP) {
                x = random.nextInt(BOARD_CELL_NUM_PER_SIDE - len);
                y = random.nextInt(BOARD_CELL_NUM_PER_SIDE);

                if (!isConflict(shipPlacements, x, y, orientation, len)) {
                    for (int i = x; i < x + len; i++) {
                        shipPlacements[i][y] = true;
                        if (panel != null)
                            panel[i][y] = index;
                    }
                    break;
                }
            } else if (orientation == Ship.HORIZONTAL_SHIP) {
                x = random.nextInt(BOARD_CELL_NUM_PER_SIDE);
                y = random.nextInt(BOARD_CELL_NUM_PER_SIDE - len);

                if (!isConflict(shipPlacements, x, y, orientation, len)) {
                    for (int i = y; i < y + len; i++) {
                        shipPlacements[x][i] = true;
                        if (panel != null)
                            panel[x][i] = index;
                    }
                    break;
                }
            }
        }
    }

    /**
     * Each turn of enemy, this function will be called.
     * When AI randomly selected a ship cell, it will change to NEIGHBOR_SEARCH status,
     * it will search the cell's four neighbors, until no cell in the stack, then it will change
     * to to the RANDOM_SEARCH status to randomly select a cell.
     * @return index index of ship.
     * @since 2.0
     */
    public int runOnce() {
        int x, y;

        while (!cellStack.empty()) {
            Point2D nextPoint = cellStack.pop();

            x = (int) nextPoint.getX();
            y = (int) nextPoint.getY();

            if (shipPlacements[x][y] && panel[x][y] != -2) {
                // Ship cell, shoot and add neighbors
                getNeighbors(x, y).stream().filter(p -> panel[(int) p.getX()][(int) p.getY()] != -2).forEach(p -> cellStack.push(p));
                panel[x][y] = -2;
                return x * BOARD_CELL_NUM_PER_SIDE + y;
            }
        }

        List<Integer> integerList = new ArrayList<>();
        for (x = 0; x < panel.length; x++) {
            for (y = 0; y < panel[x].length; y++) {
                if (panel[x][y] != -2) {
                    integerList.add(x * BOARD_CELL_NUM_PER_SIDE + y);
                }
            }
        }
        if (integerList.size() == 0) {
            return -1;
        }
        int index = integerList.get(random.nextInt(integerList.size()));
        x = index / BOARD_CELL_NUM_PER_SIDE;
        y = index % BOARD_CELL_NUM_PER_SIDE;
        panel[x][y] = -2;
        if (shipPlacements[x][y]) {
            getNeighbors(x, y).stream().filter(p -> panel[(int) p.getX()][(int) p.getY()] != -2).forEach(p -> cellStack.push(p));
        }

        return index;
    }

    // Get neighbors of specific cell.
    public List<Point2D> getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[]{
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        return Arrays.stream(points).filter(this::isValidPoint).collect(Collectors.toList());

    }

    //judge is validpoints
    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < BOARD_CELL_NUM_PER_SIDE && y >= 0 && y < BOARD_CELL_NUM_PER_SIDE;
    }
}
