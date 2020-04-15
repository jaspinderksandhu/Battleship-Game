package cu.su;

import cu.su.view.component.Ship;
/**
 * Judge if the ship is conflict with the ships on the board.
 * It's also conflict if the cells near the ship have other ships.
 * @author k4
 */

public class Utils {
    public static final int LAUNCH_VIEW_WIDTH = 400;
    public static final int LAUNCH_VIEW_HEIGHT = 200;
    public static final int NET_VIEW_WIDTH = 400;
    public static final int NET_VIEW_HEIGHT = 300;
    public static final int GAME_WIDTH = 900;
    public static final int GAME_HEIGHT = 460;
    public static final int BOARD_AREA_SIDE_LEN = 300;
    public static final int BOARD_MARGIN = 30;
    public static final int BOARD_CELL_NUM_PER_SIDE = 10;
    public static final double BOARD_CELL_STROKE_WIDTH = 1;
    public static final int BOARD_CELL_SIDE_LEN = BOARD_AREA_SIDE_LEN / BOARD_CELL_NUM_PER_SIDE;
    public static final int SHIP_DISPLAY_AREA_X = 20;
    public static final int SHIP_DISPLAY_AREA_Y = 80;
    public static final int MARGIN_BETWEEN_SHIPS = BOARD_CELL_SIDE_LEN + 20;
    // The peroid of salva mode
    public static final int SALVA_PEROID = 10;
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_FAIL = 1;
    private static final int BOARD_AREA_START_X = 220;
    public static final int USER_BOARD_AREA_START_X = BOARD_AREA_START_X;
    public static final int ENEMY_BOARD_AREA_START_X = BOARD_AREA_START_X + BOARD_AREA_SIDE_LEN + BOARD_MARGIN;
    private static final int BOARD_AREA_START_Y = 80;
    public static final int USER_BOARD_AREA_START_Y = BOARD_AREA_START_Y;
    public static final int ENEMY_BOARD_AREA_START_Y = BOARD_AREA_START_Y;

    /**
     * 
     * @param placement for placement of ship
     * @param row if the ship is placed vertical
     * @param col if ship is placed horizontal
     * @param orientation checks whether the sip placed in vertically or horizontally.
     * @param longGridNum checks the grid of the ship.
     * @return True if the ship is not conflict with other ships, otherwise return false.
     */
    public static boolean isConflict(boolean[][] placement, int row, int col, int orientation, int longGridNum) {
        if (orientation == Ship.VERTICAL_SHIP) {
            // vertical
            if (row + longGridNum > BOARD_CELL_NUM_PER_SIDE) return true;

            for (int i = row; i < row + longGridNum; i++) {
                if (placement[i][col]) return true;
                if (col != 0 && placement[i][col - 1]) return true;
                if (col != BOARD_CELL_NUM_PER_SIDE - 1 && placement[i][col + 1]) return true;

                if (i == row && i != 0 && placement[i - 1][col]) return true;
                if (i == row + longGridNum - 1 && i != BOARD_CELL_NUM_PER_SIDE - 1 &&
                        placement[i + 1][col]) return true;
            }
        } else {
            // horizontal
            if (col + longGridNum > BOARD_CELL_NUM_PER_SIDE) return true;

            for (int i = col; i < col + longGridNum; i++) {
                if (placement[row][i]) return true;
                if (row != 0 && placement[row - 1][i]) return true;
                if (row != BOARD_CELL_NUM_PER_SIDE - 1 && placement[row + 1][i]) return true;

                if (i == col && col != 0 && placement[row][i - 1]) return true;
                if (i == col + longGridNum - 1 && i != BOARD_CELL_NUM_PER_SIDE - 1
                        && placement[row][i + 1]) return true;
            }
        }

        return false;
    }
    
    /**
     * GAME_MODE which help the user to select the mode in which he or she wants 
     * to play i.e the normal mode(Build1) or the salva mode(Build2) or the dual player mode(Build3)
     */
    public enum GAME_MODE {
        NORMAL,
        SALVA,
        DUAL
    }
    
    /**
     * GAME_STATUS basically helps the user to understand the status of the game . 
     * Checks whether a new game is started or some game is running.
     * Checks whether game is paused or whether we have saved.
     * If status is saved then that is used to load the same game if the user wishes to load a game.
     */


    public enum GAME_STATUS {
        START,
        RUNNING,
        PAUSE,
        SAVE
    }

}
