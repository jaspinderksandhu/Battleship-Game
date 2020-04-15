package cu.su.controller;

import cu.su.Utils;
import cu.su.model.GameMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The score system of the game
 *
 * @author k4
 */
public class ScoreSystem {

    private final GameState gameState;
    private ScheduledExecutorService exec;
    private int wholeTime;
    private int turnTime;
    private ScheduledFuture<?> handle;
    private Consumer<Object> messageSendCallback;

    public ScoreSystem(GameState gameState, Consumer<Object> cb) {
        this.gameState = gameState;
        messageSendCallback = cb;
        exec = Executors.newScheduledThreadPool(1);
        handle = exec.scheduleAtFixedRate(() -> {
            if (gameState.getGameStatus().equals(Utils.GAME_STATUS.RUNNING)) {
                wholeTime++;
                turnTime++;
                GameMessage gameMessage = new GameMessage("time");
                gameMessage.setTimer(turnTime);
                if (gameState.getCurrentPlayer() == 1 && gameState.getGameMode().equals(Utils.GAME_MODE.SALVA)) {
                    if (turnTime > 9) {
                        gameMessage.setObject(gameState.getPlayTip());
                        if (turnTime % 2 == 0) {
                            gameMessage.setObjectSecond(true);
                        } else {
                            gameMessage.setObjectSecond(false);
                        }

                    }
                }
                messageSendCallback.accept(gameMessage);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Start to record the operation times of user and AI / enemy, and start a
     * timer for timing
     */
    public void startScoreSystem() {
        wholeTime = 0;
        turnTime = -1;
    }

    public synchronized void resetTurnTime() {
        turnTime = 0;
    }


    public synchronized int getTurnTime() {
        return turnTime;
    }


    public int getWholeTime() {
        return wholeTime;
    }

}
