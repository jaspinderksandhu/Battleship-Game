package cu.su.controller;

import cu.su.model.PlayerEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static cu.su.Utils.GAME_MODE;
import static cu.su.Utils.GAME_STATUS;

/**
 * The controller of the game
 *
 * @author k4
 */
public class GameState {

    private GAME_STATUS gameStatus;
    private GAME_MODE gameMode;

    private Map<Integer, PlayerEntry> playerEntries;
    private int currentPlayer;

    private int loadComfire;

    private ScoreSystem scoreSystem;

    private EnemyAI enemyAI;

    public GameState(Consumer<Object> cb) {
        playerEntries = new HashMap<>();
        gameStatus = GAME_STATUS.START;
        scoreSystem = new ScoreSystem(this, cb);
        enemyAI = new EnemyAI();
    }


    /**
     * When user puts all five ships on the board, the game starts.
     * AI will randomly put five ships on its board.
     */
    public void startGame() {
        currentPlayer = 0;
        loadComfire = 0;
        scoreSystem.startScoreSystem();
        gameStatus = GAME_STATUS.RUNNING;
        if (!gameMode.equals(GAME_MODE.DUAL)) {
            enemyAI.reset();
            PlayerEntry playerEntry = new PlayerEntry(enemyAI.buildShipMap());
            playerEntries.put(2, playerEntry);
            enemyAI.rebuildPanel(playerEntries.get(1));
        }

    }

    public int getPlayTip() {
        return playerEntries.get(2).getPlayTip();
    }

    public int aiRunOnce() {
        return enemyAI.runOnce();
    }

    public synchronized int getCurrentPlayer() {
        return getPlayerID(currentPlayer);
    }

    private int getPlayerID(int index) {
        return (int) playerEntries.keySet().toArray()[index];
    }

    public void nextTurn() {
        int turnTime = scoreSystem.getTurnTime();
        playerEntries.get(getCurrentPlayer()).updateTurnTime(turnTime);
        currentPlayer = 1 - currentPlayer;
        scoreSystem.resetTurnTime();
    }


    public boolean shot(int index) {
        int playerID = getPlayerID(currentPlayer);
        return playerEntries.get(playerID).shot(index);
    }

    public boolean tryShot(int index) {
        int playerID = getPlayerID(1 - currentPlayer);
        return playerEntries.get(playerID).tryShot(index);
    }


    public int getShipNumber() {
        int playerID = getPlayerID(currentPlayer);
        int shipNumber = playerEntries.get(playerID).getShipNumber();
        if (shipNumber == 0) {
            gameStatus = GAME_STATUS.START;
            int wholeTime = scoreSystem.getWholeTime();
            for (PlayerEntry playerEntry : playerEntries.values()) {
                playerEntry.calcScore(wholeTime);
            }
        }
        return shipNumber;
    }

    public boolean getUserReady() {
        if (getPlayerEntries().size() != 2) {
            return false;
        }
        for (PlayerEntry playerEntry : getPlayerEntries().values()) {
            if (playerEntry.getCellEntryMap().size() != 5) {
                return false;
            }
        }
        return true;
    }

    public int getWinScore() {
        int playerID = getPlayerID(1 - currentPlayer);
        return playerEntries.get(playerID).getScore();
    }

    public void pause() {
        gameStatus = GAME_STATUS.PAUSE;
    }

    public void resume() {
        gameStatus = GAME_STATUS.RUNNING;
    }

    public void save() {
        gameStatus = GAME_STATUS.SAVE;
    }

    public void reset() {
        gameStatus = GAME_STATUS.START;
        playerEntries.clear();

    }

    public void updateLoadComfire() {
        loadComfire++;
    }


    public Map<Integer, PlayerEntry> getPlayerEntries() {
        return playerEntries;
    }

    public synchronized GAME_STATUS getGameStatus() {
        return gameStatus;
    }

    public synchronized GAME_MODE getGameMode() {
        return gameMode;
    }

    public void setGameMode(GAME_MODE gameMode) {
        this.gameMode = gameMode;
    }

    public int getLoadComfire() {
        return loadComfire;
    }


}
