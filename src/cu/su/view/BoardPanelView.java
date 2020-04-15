package cu.su.view;

import cu.su.view.component.Cell;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static cu.su.Utils.BOARD_CELL_NUM_PER_SIDE;
import static cu.su.Utils.BOARD_CELL_SIDE_LEN;
/**
 * This class manages the mobile events for player and AI.
 * Checks for cell coordinates that form part of ship and calls hidden function once the game starts.
 * @author k4
 *
 */
public class BoardPanelView extends Group {

    public BoardPanelView(int x, int y, EventHandler<? super MouseEvent> handler) {

        super();
        for (int j = 0; j < BOARD_CELL_NUM_PER_SIDE; j++) {

            Group row = new Group();
            for (int i = 0; i < BOARD_CELL_NUM_PER_SIDE; i++) {
                Cell c = new Cell(x + i * BOARD_CELL_SIDE_LEN,
                        y + j * BOARD_CELL_SIDE_LEN,
                        j * BOARD_CELL_NUM_PER_SIDE + i);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }

            getChildren().add(row);
        }
    }

    public Cell getCell(int row, int col) {
        return (Cell) ((Group) getChildren().get(row)).getChildren().get(col);
    }


    public void hiddenPlayShip() {

        for (int i = 0; i < BOARD_CELL_NUM_PER_SIDE; i++) {
            for (int j = 0; j < BOARD_CELL_NUM_PER_SIDE; j++) {
                Cell cell = getCell(i, j);
                cell.setFill(Color.rgb(10, 190, 200));
                cell.setStroke(Color.rgb(220, 40, 30));
            }
        }
    }


}
