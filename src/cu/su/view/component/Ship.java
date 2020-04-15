package cu.su.view.component;

import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static cu.su.Utils.*;


/**
 * Ship class is used to execute mouse drag action, move action, etc.
 *
 * @author k4
 */
public class Ship extends Group {

    private static final Color SHIP_COLOR = Color.GRAY;
    private static final double SHIP_SQUARE_WIDTH = BOARD_CELL_SIDE_LEN;
    private static final double SHIP_SQUARE_HEIGHT = BOARD_CELL_SIDE_LEN;
    public static int VERTICAL_SHIP = 0;
    public static int HORIZONTAL_SHIP = 1;
    // How long and wide the ship is
    public int longGridNum, wideGridNum;
    // The location of the left-upper corner of the ship
    public int col = -1, row = -1;
    // The orientation of the ship, vertical or horizontal
    public int orientation;
    public int originOrientation;
    public double homeX, homeY;
    public double mouseX, mouseY;
    private Group shipGridPane;
    private int index;
    private Group group;
    private ShipInterface shipInterface;

    public Ship() {
        super();
    }


    public Ship(int index, int longGridNum, int wideGridNum, int orientation,
                int homeX, int homeY, Group group,
                ShipInterface shipInterface) {

        this.longGridNum = longGridNum;
        this.wideGridNum = wideGridNum;

        this.orientation = orientation;
        this.originOrientation = orientation;

        this.homeX = homeX;
        this.homeY = homeY;

        this.shipInterface = shipInterface;


        this.index = index;

        this.group = group;


        buildShipView(group);
        if (shipInterface != null)
            snapShipToHome();
    }

    public int getIndex() {
        return index;
    }

    /**
     * Draw rectangles to build a ship.
     * @return true
     */
    public boolean drawShipView() {

        shipGridPane.getChildren().clear();

        for (int i = 0; i < longGridNum; i++) {
            for (int j = 0; j < wideGridNum; j++) {

                Rectangle square = buildShipRectangle();

                if (orientation == VERTICAL_SHIP) {
                    // vertical
                    square.setX(j * square.getWidth());
                    square.setY(i * square.getHeight());
                } else if (orientation == HORIZONTAL_SHIP) {
                    // horizontal
                    square.setX(i * square.getWidth());
                    square.setY(j * square.getHeight());
                }

                shipGridPane.getChildren().add(square);

            }
        }
        return true;
    }

    private void buildShipView(Group group) {
        shipGridPane = new Group();

        drawShipView();

        shipGridPane.setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            this.shipGridPane.requestFocus();
        });

        shipGridPane.setOnMouseDragged(event -> {
            double movementX = event.getSceneX() - mouseX;
            double movementY = event.getSceneY() - mouseY;

            dragShip(movementX, movementY);
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

        });

        shipGridPane.setOnMouseReleased(event -> {
            if (onBoard()) {
                if (!calcShipPosition())
                    snapShipToHome();
                else {
                    snapShipToGrid();
                    shipInterface.addShip(this);
                }
            } else {
                snapShipToHome();
            }
        });

        shipGridPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {

                // redraw the ship
                group.getChildren().remove(shipGridPane);

                snapShipToHome();

                if (orientation == HORIZONTAL_SHIP) orientation = VERTICAL_SHIP;
                else orientation = HORIZONTAL_SHIP;

                drawShipView();
                group.getChildren().add(shipGridPane);


                event.consume();
            }
        });

        group.getChildren().add(shipGridPane);
    }

    private Rectangle buildShipRectangle() {
        Rectangle square = new Rectangle();
        square.setWidth(SHIP_SQUARE_WIDTH);
        square.setHeight(SHIP_SQUARE_HEIGHT);


        square.setFill(SHIP_COLOR);

        return square;
    }

    private void dragShip(double movementX, double movementY) {

        setLayoutX(getLayoutX() + movementX);
        setLayoutY(getLayoutY() + movementY);

        shipGridPane.setTranslateX(getLayoutX());
        shipGridPane.setTranslateY(getLayoutY());
        shipGridPane.toFront();
    }

    /**
     * Snap the ship to the original location.
     * It is used when drag action is invald.
     */
    private void snapShipToHome() {
        setLayoutX(homeX);
        setLayoutY(homeY);

        shipGridPane.setTranslateX(homeX);
        shipGridPane.setTranslateY(homeY);

        shipInterface.removeShip(this);

        row = -1;
        col = -1;
    }

    public void reset() {
        // redraw the ship
        group.getChildren().remove(shipGridPane);

        snapShipToHome();

        orientation = originOrientation;

        drawShipView();
        group.getChildren().add(shipGridPane);

    }

    /**
     * Snap the ship to the right location of the board.
     */
    private void snapShipToGrid() {

        setLayoutX(USER_BOARD_AREA_START_X + (BOARD_CELL_SIDE_LEN) * col);
        setLayoutY(USER_BOARD_AREA_START_Y + (BOARD_CELL_SIDE_LEN) * row);

        shipGridPane.setTranslateX(getLayoutX());
        shipGridPane.setTranslateY(getLayoutY());
    }

    /**
     * If the ship is moved on the board, return True. Otherwise return false.
     *
     * @return true
     */
    public boolean onBoard() {

        return getLayoutX() > USER_BOARD_AREA_START_X - BOARD_CELL_SIDE_LEN
                && getLayoutX() < (USER_BOARD_AREA_START_X + (BOARD_CELL_SIDE_LEN) * BOARD_CELL_NUM_PER_SIDE)
                && getLayoutY() > (USER_BOARD_AREA_START_Y - (BOARD_CELL_SIDE_LEN))
                && getLayoutY() < (USER_BOARD_AREA_START_Y + (BOARD_CELL_SIDE_LEN) * BOARD_CELL_NUM_PER_SIDE);
    }

    public boolean calcShipPosition() {

        if (col != -1 && row != -1) {
            if (shipInterface != null)
                shipInterface.removeShip(this);
        }

        row = (int) ((getLayoutY() - USER_BOARD_AREA_START_Y + 0.5 * BOARD_CELL_SIDE_LEN) / BOARD_CELL_SIDE_LEN);
        col = (int) ((getLayoutX() - USER_BOARD_AREA_START_X + 0.5 * BOARD_CELL_SIDE_LEN) / BOARD_CELL_SIDE_LEN);

        if (col < 0) col = 0;
        else if (col > BOARD_CELL_NUM_PER_SIDE - 1) col = BOARD_CELL_NUM_PER_SIDE - 1;

        if (row < 0) row = 0;
        else if (row > BOARD_CELL_NUM_PER_SIDE - 1) row = BOARD_CELL_NUM_PER_SIDE - 1;

        if (shipInterface != null) {
            if (!shipInterface.isConflict(this)) return true;
            else {
                row = -1;
                col = -1;
                return false;
            }
        } else {
            return false;
        }


    }

    public int getLongGridNum() {
        return longGridNum;
    }

    public int getWideGridNum() {
        return wideGridNum;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getOrientation() {
        return orientation;
    }

    public interface ShipInterface {

        /**
         * The callback that the ship will call to check whether it can be added on the board.
         *
         * @param ship the ship placed
         * @return true if add successfully, otherwise false
         */
        boolean isConflict(Ship ship);

        /**
         * The callback that the ship will called when it will be added on the board.
         *
         * @param ship a instance of Ship class
         */
        void addShip(Ship ship);

        /**
         * The callback that the ship will called when it is removed from the board.
         *
         * @param ship the ship is passed for evaluation.
         */
        void removeShip(Ship ship);
    }
}
