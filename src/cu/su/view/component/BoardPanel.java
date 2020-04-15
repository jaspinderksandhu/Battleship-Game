package cu.su.view.component;

import cu.su.model.CellEntry;
import cu.su.model.PlayerEntry;
import cu.su.view.BoardPanelView;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cu.su.Utils.BOARD_CELL_NUM_PER_SIDE;
import static cu.su.Utils.isConflict;

/**
 * GridPanel class is the controller of board.
 *
 * @author k4
 */
public class BoardPanel {

    /**
     * shipNum is the number of ships on the board.
     */
    private int shipNumOnBoard = 0;
    /**
     * shotCellNum is the number of cells that are shot
     */
    private boolean isEnemy;


    private BoardPanelView view;

    private boolean[][] shipPlacements = new boolean[BOARD_CELL_NUM_PER_SIDE][BOARD_CELL_NUM_PER_SIDE];

    /**
     * GridPanel is used to manage cells of the board.
     *
     * @param x:       x of the left-upper corner coordinate
     * @param y:       y of the left-upper corner coordinate
     * @param isEnemy: true if the board is for enemy.
     * @param handler: event handler called when click one cell.
     */
    public BoardPanel(int x, int y, boolean isEnemy, EventHandler<MouseEvent> handler) {
        super();
        view = new BoardPanelView(x, y, handler);
        this.isEnemy = isEnemy;

        shipNumOnBoard = 0;

    }

    public void updateUserShipPlacements(int row, int col, boolean value) {
        shipPlacements[row][col] = value;
    }

    public void updateShipNumOnBoard(int count) {
        shipNumOnBoard += count;
        if (shipNumOnBoard < 0) {
            shipNumOnBoard = 0;
        }
    }

    public boolean isConflictOnUserBoard(int row, int col, int orientation, int longGridNum) {
        return isConflict(shipPlacements, row, col, orientation, longGridNum);
    }

    public void addOneShip() {
        shipNumOnBoard++;
    }

    public void removeOneShip() {
        if (shipNumOnBoard > 0) shipNumOnBoard--;
    }

    public int getShipNum() {
        return shipNumOnBoard;
    }


    public Group getPanelView() {
        return view;
    }

    public void hiddenPlayShip() {
        view.hiddenPlayShip();
    }

    public Cell getCell(int row, int col) {
        return view.getCell(row, col);
    }

    public Cell getCell(int index) {
        int row = index / BOARD_CELL_NUM_PER_SIDE;
        int col = index % BOARD_CELL_NUM_PER_SIDE;
        return view.getCell(row, col);
    }

    public void load(PlayerEntry playerEntry) {
        for (Map.Entry<Integer, Boolean> entry : playerEntry.getShotList().entrySet()) {
            getCell(entry.getKey()).shot(entry.getValue());
        }
    }


    /**
     * The function is called after the user clicks the start game button.
     * It shall get all indexes of ships and corresponding cells, then build a hashmap.
     * @return ShipCellMap that checks the cell map of ship.
     */
    public Map<Integer, List<CellEntry>> buildShipMap() {
        Map<Integer, List<CellEntry>> shipCellMap = new HashMap<>();
        for (int i = 0; i < BOARD_CELL_NUM_PER_SIDE; i++) {
            for (int j = 0; j < BOARD_CELL_NUM_PER_SIDE; j++) {
                Cell cell = getCell(i, j);
                if (cell.getShipIndex() != -1) {
                    int shipIndex = cell.getShipIndex();
                    if (!shipCellMap.containsKey(shipIndex)) {
                        shipCellMap.put(shipIndex, new ArrayList<>());
                    }

                    CellEntry cellEntry = new CellEntry(cell.getIndex(), cell.getShipIndex());
                    shipCellMap.get(shipIndex).add(cellEntry);
                }
            }
        }
        return shipCellMap;
    }

    public int getShotCellNum() {
        int shotCellNum = 0;
        for (int i = 0; i < BOARD_CELL_NUM_PER_SIDE; ++i) {
            for (int j = 0; j < BOARD_CELL_NUM_PER_SIDE; ++j) {
                Cell cell = getCell(i, j);
                if (cell.isShot() && cell.getShipIndex() != -1) {
                    shotCellNum++;
                }
            }
        }
        return shotCellNum;
    }

    /**
     * Shoot the ship at specific cell.
     * @param cell the cells of ship .
     */
    public void shootShip(Cell cell) {
//        int shipIndex = cell.getShipIndex();
//        if (shipIndex == -1) return;
//
//        System.out.println(shipIndex);
//        if(shipCellMap.get(shipIndex)!=null)
//        	shipCellMap.get(shipIndex).remove(cell);
//
//        if (shipCellMap.get(shipIndex)!=null && shipCellMap.get(shipIndex).size() == 0)
//        {
//            String infoString;
//            if (isEnemy)
//            {
//                infoString = "An enemy ship is sinking! Enemy still has %d ships";
//            } else
//            {
//                infoString = "One of your ships is sinking! You still have %d ships";
//            }
//            if(shipCellMap.get(shipIndex)!=null)
//            	shipCellMap.remove(shipIndex);
//            removeOneShip();
//
//            String alertString = String.format(infoString, shipNumOnBoard);
//            // The ship is sinking
//            Alert alert = new Alert(Alert.AlertType.INFORMATION,
//                    alertString,
//                    ButtonType.YES);
//            alert.showAndWait();
//
//        }
    }

    public boolean reset() {

        shipNumOnBoard = 0;

        for (int i = 0; i < BOARD_CELL_NUM_PER_SIDE; i++) {
            for (int j = 0; j < BOARD_CELL_NUM_PER_SIDE; j++) {
                Cell cell = getCell(i, j);
                cell.setShip(-1);
                cell.setShot(false);
            }
        }
//        shipCellMap.clear();
        view.hiddenPlayShip();
        return true;
    }

}