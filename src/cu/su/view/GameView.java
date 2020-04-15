package cu.su.view;

import cu.su.controller.GameClient;
import cu.su.model.CellEntry;
import cu.su.model.GameMessage;
import cu.su.model.PlayerEntry;
import cu.su.view.component.BoardPanel;
import cu.su.view.component.Cell;
import cu.su.view.component.Ship;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cu.su.Utils.*;


/**
 * The view class of the game.
 *
 * @author k4
 */
public class GameView extends Group {


    private static final int BUTTON_MARGIN = 100;
    private static ArrayList<Ship> shipList = new ArrayList<>();
    private final Group root = new Group();
    private final Group shipGroup = new Group();
    private final Group boardGroup = new Group();
    private BoardPanel enemyPanel, userPanel;
    private Button startButton = new Button("Start");
    private Button resetButton = new Button("Reset");
    private Button quitButton = new Button("Quit");
    private Button pauseButton = new Button("Pause");
    private Button saveButton = new Button("Save");
    private Label labelUserTimer = new Label("001");
    private Label labelTip = new Label("Select the ship and press 'R' to rotate the ship for put ship " +
            "to board then ready to start game");
    private Stage primaryStage;
    private GAME_MODE gameMode;

    private GameClient client;

    private boolean hoster;

    /**
     * Create the game view.
     *
     * @param primaryStage The main stage
     * @param client computer to which the connection is built.
     * @param mode mode of the game normal or salva or dual.
     * @param hoster LAN information
     */
    

    public GameView(Stage primaryStage, GameClient client, GAME_MODE mode, boolean hoster) {

        super();
        this.primaryStage = primaryStage;
        this.gameMode = mode;
        this.client = client;
        this.hoster = hoster;
        createGameLayout();
        createShip(new Ship.ShipInterface() {

            @Override
            public boolean isConflict(Ship ship) {
                return isConflictOnUserBoard(ship);
            }

            @Override
            public void addShip(Ship ship) {
                putShip(ship);
            }

            @Override
            public void removeShip(Ship ship) {
                removeTheShip(ship);
            }
        });

        createButtons();
        createUserTimerTextAndTips();

        getChildren().add(root);

        setApplyMessage();

        setOnStartButtonClicked();
        setOnPauseButtonClicked();
        setOnResetButtonClicked();
        setOnSaveButtonClicked();
        setOnQuitButtonClicked();

        querySaved();
    }

    private void querySaved() {
        GameMessage gameMessage = new GameMessage("query");
        gameMessage.setObject(gameMode);
        client.send(gameMessage);
    }

    private void setApplyMessage() {
        client.setApplyMessage(message -> {

            Platform.runLater(() -> {
                if (message instanceof GameMessage) {
                    GameMessage gameMessage = (GameMessage) message;
                    if (!gameMessage.getCmd().equals("time")) {
                        System.out.println(String.format("game client recv msg: %s", message.toString()));
                    }
                    int playerID;
                    switch (gameMessage.getCmd()) {
                        case "ready":
                            if (gameMessage.getCode() == CODE_FAIL) {
                                labelTip.setText(gameMessage.getMessage());
                            } else {
                                startButton.setDisable(true);
                                pauseButton.setDisable(false);
                                saveButton.setDisable(false);
                                resetButton.setDisable(false);
                                labelTip.setText(String.format("Game start, %s turn now!",
                                        gameMessage.getObject().equals(client.getID()) ? "You" : "Other"));
                                hideUserShips();
                            }
                            break;
                        case "game":
                        case "gameend":
                            int index = (int) gameMessage.getObject();
                            boolean isShip = (boolean) gameMessage.getObjectSecond();
                            int shipNumber = (int) gameMessage.getObjectThird();
                            playerID = (int) gameMessage.getObjectFourth();

                            if (client.getID() == playerID) {
                                Cell cell = userPanel.getCell(index);
                                cell.shot(isShip);
                            } else {
                                Cell cell = enemyPanel.getCell(index);
                                cell.shot(isShip);
                            }
                            if (gameMessage.getCmd().equals("gameend")) {
                                labelTip.setText("");
                                int score = (int) gameMessage.getObjectFifth();
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                        String.format("%s has won the game with socre: %d! Do you want to play again?",
                                                (client.getID() != playerID) ? "You" : "Other", score));
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK) {
                                    gameMessage.setCmd("restart");
                                    client.send(gameMessage);
                                } else {
                                    System.exit(-1);
                                }

                            } else {
                                labelTip.setText(String.format("%s turn now! %s",
                                        (client.getID() == playerID) ? "You" : "Other",
                                        (client.getID() != playerID) ? String.format("enemy still have %d ship.",
                                                shipNumber) : ""));
                            }
                            break;
                        case "time":
                            labelUserTimer.setText(String.format("%d", gameMessage.getTimer()));
                            if (gameMessage.getObject() != null && gameMessage.getObjectSecond() != null) {
                                int tip = (int) gameMessage.getObject();
                                boolean show = (boolean) gameMessage.getObjectSecond();
                                if (tip != -1) {
                                    enemyPanel.getCell(tip).showTip(show);
                                }
                            }

                            break;
                        case "pause":
                            pauseButton.setText("Resume");
                            saveButton.setDisable(true);
                            labelTip.setText("Game pause!");
                            break;
                        case "resume":
                        case "loadreply":
                            startButton.setDisable(true);
                            pauseButton.setText("Pause");
                            saveButton.setDisable(false);
                            playerID = (int) gameMessage.getObjectFourth();
                            labelTip.setText(String.format("%s turn now! %s",
                                    (client.getID() == playerID) ? "You" : "Other",
                                    (client.getID() != playerID) ? String.format("enemy still have %d ship.",
                                            (int) gameMessage.getObjectThird()) : ""));
                            break;
                        case "reset":
                            playerID = (int) gameMessage.getObject();
                            if (client.getID() == playerID && gameMode.equals(GAME_MODE.DUAL)) {
                                return;
                            }
                            GameMessage replyMessage = new GameMessage("resetreply");
                            if (gameMode.equals(GAME_MODE.DUAL)) {
                                replyMessage.setObjectSecond(makeAQuestion("Other want to reset the game, Do you agree?"));
                            } else {
                                replyMessage.setObjectSecond(true);
                            }
                            client.send(replyMessage);
                            break;
                        case "resetreply":
                            playerID = (int) gameMessage.getObject();
                            boolean reply = (boolean) gameMessage.getObjectSecond();
                            if (client.getID() == playerID && gameMode.equals(GAME_MODE.DUAL)) {
                                new Alert(Alert.AlertType.INFORMATION,
                                        String.format("Other %sagree to reset the game!", reply ? "" : "do not "),
                                        ButtonType.YES).showAndWait();
                            }
                            if (reply) {
                                startButton.setDisable(false);
                                pauseButton.setDisable(true);
                                resetButton.setDisable(true);
                                saveButton.setDisable(true);
                                reset();
                            }
                            break;
                        case "save":
                            new Alert(Alert.AlertType.INFORMATION,
                                    "Game saved! back to launch view now.",
                                    ButtonType.YES).showAndWait();

                            Scene scene = new Scene(new LaunchView(primaryStage, client, hoster), LAUNCH_VIEW_WIDTH, LAUNCH_VIEW_HEIGHT);
                            scene.getStylesheets().add("/css/battleship.css");
                            //scene.getStylesheets().add(BattleShip.class.getResource("/css/battleship.css").toExternalForm());
                            primaryStage.setScene(scene);

                            //primaryStage.setScene();
                            break;
                        case "query":

                            if (gameMessage.getCode() == CODE_SUCCESS) {
                                playerID = (int) gameMessage.getObject();
                                if (playerID == client.getID()) {
                                    if (makeAQuestion("Your previous game had been saved game, Do you want to load it?")) {
                                        client.send(new GameMessage("queryreply"));
                                        labelTip.setText("Waiting for other to load the game!");
                                    }
                                }
                            }
                            break;
                        case "load":
                            Map<Integer, PlayerEntry> playerEntries = (Map<Integer, PlayerEntry>) gameMessage.getObject();
                            load(playerEntries);
                            startButton.setDisable(true);
                            pauseButton.setDisable(false);
                            resetButton.setDisable(false);
                            GameMessage queryMessage = new GameMessage("loadreply");
                            client.send(queryMessage);
                            break;
                        case "quit":
                            playerID = (int) gameMessage.getObjectSecond();
                            if (playerID != client.getID()) {
                                return;
                            }
                            boolean running = (boolean) gameMessage.getObject();
                            if (running) {
                                GameMessage quitMessage = new GameMessage("quitreply");
                                quitMessage.setObject(makeAQuestion("Do you want to save the game before you quit?"));
                                client.send(quitMessage);
                            } else {
                                 scene = new Scene(new LaunchView(primaryStage, client, hoster), LAUNCH_VIEW_WIDTH, LAUNCH_VIEW_HEIGHT);
                                scene.getStylesheets().add("/css/battleship.css");
                                //scene.getStylesheets().add(BattleShip.class.getResource("/css/battleship.css").toExternalForm());
                                primaryStage.setScene(scene);
                            }
                            break;
                        case "quitreply":
                            playerID = (int) gameMessage.getObjectSecond();
                            boolean save = (boolean) gameMessage.getObject();
                            new Alert(Alert.AlertType.INFORMATION,
                                    String.format("%s %squit the game, back to launch view now.", (playerID == client.getID()) ? "You" : "Other", save ? "save and " : ""),
                                    ButtonType.YES).showAndWait();
                             scene = new Scene(new LaunchView(primaryStage, client, hoster), LAUNCH_VIEW_WIDTH, LAUNCH_VIEW_HEIGHT);
                            scene.getStylesheets().add("/css/battleship.css");
                            //scene.getStylesheets().add(BattleShip.class.getResource("/css/battleship.css").toExternalForm());
                            primaryStage.setScene(scene);
                            break;
                        case "restart":
                            reset();
                            startButton.setDisable(false);
                            pauseButton.setDisable(true);
                            saveButton.setDisable(true);
                            resetButton.setDisable(true);
                    }
                }
            });


        });
    }

    private void createUserTimerTextAndTips() {
        labelUserTimer.setText("0");
        labelUserTimer.setFont(new Font(24));
        labelUserTimer.setMinWidth(GAME_WIDTH);
        labelUserTimer.setAlignment(Pos.CENTER);
        VBox vBox = new VBox();

        labelTip.setAlignment(Pos.CENTER);
        labelTip.setMinWidth(GAME_WIDTH);
        labelTip.setFont(new Font(15));

        vBox.getChildren().addAll(labelUserTimer, labelTip);

        root.getChildren().addAll(vBox);
    }


    private boolean makeAQuestion(String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                question);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Create the buttons at the right of the user board.
     */
    private void createButtons() {
        startButton.setMinWidth(90);
        resetButton.setMinWidth(90);
        quitButton.setMinWidth(90);
        pauseButton.setMinWidth(90);
        saveButton.setMinWidth(90);

        startButton.setFont(new Font(18));
        startButton.setLayoutX(USER_BOARD_AREA_START_X);
        startButton.setLayoutY(USER_BOARD_AREA_START_Y + BOARD_AREA_SIDE_LEN + 30);

        pauseButton.setFont(new Font(18));
        pauseButton.setLayoutX(USER_BOARD_AREA_START_X + BUTTON_MARGIN);
        pauseButton.setLayoutY(USER_BOARD_AREA_START_Y + BOARD_AREA_SIDE_LEN + 30);

        resetButton.setFont(new Font(18));
        resetButton.setLayoutX(USER_BOARD_AREA_START_X + 2 * BUTTON_MARGIN);
        resetButton.setLayoutY(USER_BOARD_AREA_START_Y + BOARD_AREA_SIDE_LEN + 30);

        saveButton.setFont(new Font(18));
        saveButton.setLayoutX(USER_BOARD_AREA_START_X + 3 * BUTTON_MARGIN);
        saveButton.setLayoutY(USER_BOARD_AREA_START_Y + BOARD_AREA_SIDE_LEN + 30);

        quitButton.setFont(new Font(18));
        quitButton.setLayoutX(USER_BOARD_AREA_START_X + 4 * BUTTON_MARGIN);
        quitButton.setLayoutY(USER_BOARD_AREA_START_Y + BOARD_AREA_SIDE_LEN + 30);

        root.getChildren().add(startButton);
        root.getChildren().add(pauseButton);
        root.getChildren().add(resetButton);
        root.getChildren().add(saveButton);
        root.getChildren().add(quitButton);

        pauseButton.setDisable(true);
        resetButton.setDisable(true);
        saveButton.setDisable(true);
    }


    /**
     * Put a ship into the placements array.
     *
     * @param newShip the instance of Ship class.
     */
    public void putShip(Ship newShip) {
        if (newShip.orientation == Ship.VERTICAL_SHIP) {
            for (int i = newShip.row; i < newShip.row + newShip.longGridNum; i++) {
                userPanel.updateUserShipPlacements(i, newShip.col, true);
                getUserPanel().getCell(i, newShip.col).setShip(newShip.getIndex());
            }

        } else {
            for (int i = newShip.col; i < newShip.col + newShip.longGridNum; i++) {
                userPanel.updateUserShipPlacements(newShip.row, i, true);

                getUserPanel().getCell(newShip.row, i).setShip(newShip.getIndex());
            }
        }

        userPanel.updateShipNumOnBoard(1);
        getUserPanel().addOneShip();
    }

    /**
     * Remove the ship from the placements array.
     *
     * @param ship passes every ship in loop for placement
     */
    public void removeTheShip(Ship ship) {
        if (ship.row == -1 || ship.col == -1) return;

        if (ship.orientation == 0) {
            for (int i = ship.row; i < ship.row + ship.longGridNum; i++) {
                userPanel.updateUserShipPlacements(i, ship.col, false);

                getUserPanel().getCell(i, ship.col).setShip(-1);
            }
        } else {
            for (int i = ship.col; i < ship.col + ship.longGridNum; i++) {
                userPanel.updateUserShipPlacements(ship.row, i, false);

                getUserPanel().getCell(ship.row, i).setShip(-1);
            }
        }

        userPanel.updateShipNumOnBoard(-1);
        getUserPanel().removeOneShip();
    }

    public boolean isConflictOnUserBoard(Ship newShip) {
        return userPanel.isConflictOnUserBoard(newShip.row, newShip.col, newShip.orientation, newShip.longGridNum);
    }

    /**
     * Used to set the mouse event handler to the start button.
     */
    public void setOnStartButtonClicked() {
        startButton.setOnMouseClicked(event -> {
            Map<Integer, List<CellEntry>> shipMap = userPanel.buildShipMap();
            if (shipMap.size() == 5) {
                GameMessage message = new GameMessage("ready");
                message.setObject(shipMap);
                message.setObjectSecond(gameMode);
                client.send(message);
            } else {
                labelTip.setText("Please put all ship on board before start game!");
            }
        });
    }

    /**
     * Used to set the mouse event handler to the start button.
     * 
     */
   
    public void setOnPauseButtonClicked() {
        pauseButton.setOnMouseClicked(event -> {
            if (pauseButton.getText().trim().equals("Pause")) {
                GameMessage message = new GameMessage("pause");
                client.send(message);
            } else {
                GameMessage message = new GameMessage("resume");
                client.send(message);
            }

        });
    }

    public void setOnResetButtonClicked() {
        resetButton.setOnMouseClicked(event -> {
            GameMessage gameMessage = new GameMessage("reset");
            client.send(gameMessage);
        });
    }


    /**
     * Used to set the mouse event handler to the start button.
     */
    public void setOnSaveButtonClicked() {
        saveButton.setOnMouseClicked(event -> {
            GameMessage gameMessage = new GameMessage("save");
            client.send(gameMessage);
        });
    }

    /**
     * Used to set the mouse event handler to the start button.
     */
    public void setOnQuitButtonClicked() {
        quitButton.setOnMouseClicked(event -> {
            GameMessage gameMessage = new GameMessage("quit");
            client.send(gameMessage);
        });
    }

    /**
     * Create the layout of  It includes ships and boards.
     *
     * @param
     */
    private void createGameLayout() {
        // Create the user board
        userPanel = new BoardPanel(USER_BOARD_AREA_START_X, USER_BOARD_AREA_START_Y,
                false, null);
        // Create the enemy board
        enemyPanel = new BoardPanel(ENEMY_BOARD_AREA_START_X, ENEMY_BOARD_AREA_START_Y,
                true, event -> {

            System.out.print("cell clicked with index: ");
            Cell cell = (Cell) event.getSource();
            int index = cell.getIndex();
            System.out.println(index);
            GameMessage gameMessage = new GameMessage("game");
            gameMessage.setObject(index);
            client.send(gameMessage);

        });


        boardGroup.getChildren().add(userPanel.getPanelView());
        boardGroup.getChildren().add(enemyPanel.getPanelView());

        root.getChildren().add(boardGroup);
    }

    /**
     * Create five ships
     *
     * @param shipInterface The interface ship used to add, remove itself to the controller.
     */
    private void createShip(Ship.ShipInterface shipInterface) {
        // Carrier
        Ship carrier = new Ship(0, 5, 1, Ship.HORIZONTAL_SHIP,
                SHIP_DISPLAY_AREA_X, SHIP_DISPLAY_AREA_Y, shipGroup,
                shipInterface);
        // Battleship
        Ship battleShip = new Ship(1, 4, 1, Ship.HORIZONTAL_SHIP,
                SHIP_DISPLAY_AREA_X, (int) carrier.homeY + MARGIN_BETWEEN_SHIPS, shipGroup,
                shipInterface);
        // Cruiser
        Ship cruiser = new Ship(2, 3, 1, Ship.HORIZONTAL_SHIP,
                SHIP_DISPLAY_AREA_X, (int) battleShip.homeY + MARGIN_BETWEEN_SHIPS,
                shipGroup, shipInterface);
        // Submarine
        Ship submarine = new Ship(3, 3, 1, Ship.HORIZONTAL_SHIP,
                SHIP_DISPLAY_AREA_X, (int) cruiser.homeY + MARGIN_BETWEEN_SHIPS, shipGroup,
                shipInterface);
        // Destroyer
        Ship destroyer = new Ship(4, 2, 1, Ship.HORIZONTAL_SHIP,
                SHIP_DISPLAY_AREA_X, (int) submarine.homeY + MARGIN_BETWEEN_SHIPS, shipGroup,
                shipInterface);

        root.getChildren().add(shipGroup);

        shipList.add(carrier);
        shipList.add(battleShip);
        shipList.add(cruiser);
        shipList.add(submarine);
        shipList.add(destroyer);
    }

    /**
     * Hide all of five ships after starting the
     */
    public void hideUserShips() {
        shipGroup.getChildren().clear();
        userPanel.hiddenPlayShip();
    }

    private void startGame() {
        hideUserShips();
        getUserPanel().buildShipMap();
        getEnemyPanel().buildShipMap();
        startGame();
    }

    public void closeStage() {
        primaryStage.close();
    }

    public BoardPanel getEnemyPanel() {
        return enemyPanel;
    }

    public BoardPanel getUserPanel() {
        return userPanel;
    }

    private void redrawShips() {
        for (Ship ship : shipList) {
            ship.reset();
        }
    }

    private void load(Map<Integer, PlayerEntry> playerEntries) {
        hideUserShips();

        for (Map.Entry<Integer, PlayerEntry> entry : playerEntries.entrySet()) {
            if (entry.getKey() != client.getID()) {
                enemyPanel.load(entry.getValue());
            } else {
                userPanel.load(entry.getValue());
            }
        }
    }

    private void reset() {
        redrawShips();
        userPanel.reset();
        enemyPanel.reset();
        labelTip.setText("Select the ship and press 'R' to rotate the ship for put ship " +
                "to board then ready to start game");
        labelUserTimer.setText("0");
    }
}
