package cu.su.view.component;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static cu.su.Utils.BOARD_CELL_SIDE_LEN;
import static cu.su.Utils.BOARD_CELL_STROKE_WIDTH;

/**
 * This cell class extends Rectangle, this cell have exposed functions and get some characters
 *
 * @author k4
 */
public class Cell extends Rectangle {

    private int index;
    private boolean isShot = false;
    private int shipIndex = -1;

    public Cell(double point_x, double point_y, int index) {
        super(point_x, point_y, BOARD_CELL_SIDE_LEN, BOARD_CELL_SIDE_LEN);

        this.index = index;

        setFill(Color.rgb(10, 190, 200));
        setStroke(Color.rgb(220, 40, 30));
        setStrokeWidth(BOARD_CELL_STROKE_WIDTH);
    }

    public int getIndex() {
        return index;
    }

    /**
     * The function will be called when the cell is clicked.
     */
    public void expose() {

        if (isShot) return;

        isShot = true;
        if (shipIndex == -1)
            setColor(Color.BLACK);
        else
            setColor(Color.RED);
    }

    public void shot(boolean isShip) {
        if (isShip) {
            setColor(Color.RED);
        } else {
            setColor(Color.BLACK);
        }
    }

    public void showTip(boolean show) {
        if (show) {
            setColor(Color.YELLOW);
        } else {
            setColor(Color.rgb(10, 190, 200));
        }
    }

    public Color getColor() {
        return (Color) getFill();
    }

    public void setColor(Color color) {
        setFill(color);
    }

    public boolean isShot() {
        return isShot;
    }

    public void setShot(boolean isShot) {
        this.isShot = isShot;
    }

    public void setShip(int shipIndex) {
        this.shipIndex = shipIndex;
    }

    public int getShipIndex() {
        return shipIndex;
    }

    public boolean hasShip() {
        return shipIndex != -1;
    }

}
