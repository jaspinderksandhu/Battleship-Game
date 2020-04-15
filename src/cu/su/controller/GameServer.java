package cu.su.controller;

import cu.su.Utils;
import cu.su.model.GameMessage;
import cu.su.model.PlayerEntry;
import cu.su.net.Server;

import java.io.IOException;

import static cu.su.Utils.CODE_FAIL;
import static cu.su.Utils.CODE_SUCCESS;

public class GameServer extends Server {

    private GameState gameState;

    /**
     * Creates a Hub listening on a specified port, and starts a thread for
     * processing messages that are received from clients.
     *
     * @param port the port on which the server will listen.
     * @throws IOException if it is not possible to create a listening socket on the specified port.
     */
    public GameServer(int port) throws IOException {
        super(port);
        gameState = new GameState(this::sendToAll);
    }

    @Override
    protected void playerConnected(int playerID) {
        if (getPlayerList().length == 2) {
            shutdownServerSocket();
        }
    }

    @Override
    protected void messageReceived(int playerID, Object message) {
        System.out.println(getPlayerList().length);
        if (message instanceof GameMessage) {
            GameMessage gameMessage = (GameMessage) message;
            switch (gameMessage.getCmd()) {
                case "ready":
                    if (gameState.getPlayerEntries().replace(playerID, new PlayerEntry(gameMessage.getObject())) == null) {
                        gameState.getPlayerEntries().put(playerID, new PlayerEntry(gameMessage.getObject()));
                    }
                    gameState.setGameMode((Utils.GAME_MODE) gameMessage.getObjectSecond());
                    if (gameState.getGameMode().equals(Utils.GAME_MODE.DUAL)) {
                        if (getPlayerList().length != 2) {
                            gameMessage.setCode(CODE_FAIL);
                            gameMessage.setMessage("please wait other player to connect the game!");
                            if (gameState.getPlayerEntries().replace(playerID, new PlayerEntry(gameMessage.getObject())) == null) {
                                gameState.getPlayerEntries().put(playerID, new PlayerEntry(gameMessage.getObject()));
                            }
                            sendToOne(playerID, message);
                        } else {
                            if (!gameState.getUserReady()) {
                                gameMessage.setCode(CODE_FAIL);
                                gameMessage.setMessage("please wait other player to ready for the game!");
                                sendToOne(playerID, message);
                            } else {
                                gameState.startGame();
                                gameMessage.setCode(CODE_SUCCESS);
                                gameMessage.setObject(gameState.getCurrentPlayer());
                                sendToAll(gameMessage);
                            }
                        }
                    } else {
                        shutdownServerSocket();
                        gameState.startGame();
                        gameMessage.setCode(CODE_SUCCESS);
                        gameMessage.setObject(gameState.getCurrentPlayer());
                        sendToAll(gameMessage);
                    }
                    break;
                case "game":
                    if (!gameState.getGameStatus().equals(Utils.GAME_STATUS.RUNNING)) {
                        return;
                    }
                    if (gameState.getCurrentPlayer() != playerID) {
                        return;
                    }
                    int index = (int) gameMessage.getObject();
                    boolean result = gameState.tryShot(index);
                    if (!result) {
                        gameState.nextTurn();
                        gameMessage.setCode(CODE_SUCCESS);
                        gameMessage.setObjectSecond(gameState.shot(index));
                        int shipNumber = gameState.getShipNumber();
                        gameMessage.setObjectThird(shipNumber);
                        gameMessage.setObjectFourth(gameState.getCurrentPlayer());

                        if (shipNumber == 0) {
                            gameMessage.setCmd("gameend");
                            gameMessage.setObjectFifth(gameState.getWinScore());
                        }
                        sendToAll(gameMessage);
                        if (!gameState.getGameMode().equals(Utils.GAME_MODE.DUAL)) {
                            GameMessage enemyMessage = new GameMessage("game");
                            index = gameState.aiRunOnce();
                            gameState.nextTurn();
                            enemyMessage.setCode(CODE_SUCCESS);
                            enemyMessage.setObjectSecond(gameState.shot(index));
                            shipNumber = gameState.getShipNumber();
                            enemyMessage.setObjectThird(shipNumber);
                            enemyMessage.setObjectFourth(gameState.getCurrentPlayer());
                            enemyMessage.setObject(index);
                            if (shipNumber == 0) {
                                enemyMessage.setCmd("gameend");
                                enemyMessage.setObjectFifth(gameState.getWinScore());
                            }
                            sendToAll(enemyMessage);
                        }
                    }

                    break;
                case "pause":
                    if (!gameState.getGameStatus().equals(Utils.GAME_STATUS.RUNNING)) {
                        return;
                    }
                    gameState.pause();
                    sendToAll(gameMessage);
                    break;
                case "resume":
                    if (!gameState.getGameStatus().equals(Utils.GAME_STATUS.PAUSE)) {
                        return;
                    }
                    gameState.resume();
                    int shipNumber = gameState.getShipNumber();
                    gameMessage.setObjectThird(shipNumber);
                    gameMessage.setObjectFourth(gameState.getCurrentPlayer());
                    sendToAll(gameMessage);
                    break;
                case "reset":
                case "resetreply":
                    if (!gameState.getGameStatus().equals(Utils.GAME_STATUS.RUNNING)) {
                        return;
                    }
                    gameMessage.setObject(playerID);
                    sendToAll(gameMessage);
                    if (gameMessage.getCmd().equals("resetreply")) {
                        boolean reply = (boolean) gameMessage.getObjectSecond();
                        if (reply) {
                            gameState.reset();
                        }
                    }
                    break;
                case "save":
                    if (!gameState.getGameStatus().equals(Utils.GAME_STATUS.RUNNING)) {
                        return;
                    }
                    gameState.save();
                    sendToAll(gameMessage);
                    break;
                case "quit":
                    boolean running = gameState.getGameStatus().equals(Utils.GAME_STATUS.RUNNING);
                    gameMessage.setObject(running);
                    gameMessage.setObjectSecond(playerID);
                    sendToAll(gameMessage);
                    if (!running) {
                        gameState.reset();
                    }
                    break;
                case "quitreply":
                    boolean save = (boolean) gameMessage.getObject();
                    if (save) {
                        gameState.save();
                    } else {
                        gameState.reset();
                    }
                    gameMessage.setObjectSecond(playerID);
                    sendToAll(gameMessage);
                    break;
                case "query":
                    Utils.GAME_MODE gameMode = (Utils.GAME_MODE) gameMessage.getObject();
                    if (gameState.getGameStatus().equals(Utils.GAME_STATUS.SAVE) &&
                            gameMode.equals(gameState.getGameMode())) {
                        gameMessage.setCode(CODE_SUCCESS);
                    } else {
                        gameMessage.setCode(CODE_FAIL);
                    }
                    gameMessage.setObject(playerID);
                    sendToAll(gameMessage);
                    break;
                case "queryreply":
                    gameState.updateLoadComfire();
                    if (gameState.getLoadComfire() != 2 && gameState.getGameMode().equals(Utils.GAME_MODE.DUAL)) {
                        return;
                    }
                    gameMessage.setCmd("load");
                    gameMessage.setObject(gameState.getPlayerEntries());
                    sendToAll(gameMessage);
                    break;
                case "loadreply":
                    gameMessage.setObjectThird(gameState.getShipNumber());
                    gameMessage.setObjectFourth(gameState.getCurrentPlayer());
                    sendToAll(gameMessage);
                    gameState.resume();
                    break;
                case "restart":
                    gameState.reset();
                    sendToAll(gameMessage);
            }
        }
    }
}
