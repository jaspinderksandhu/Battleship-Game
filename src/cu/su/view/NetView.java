package cu.su.view;

import cu.su.controller.GameClient;
import cu.su.controller.GameServer;
import cu.su.exception.GameException;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static cu.su.Utils.LAUNCH_VIEW_HEIGHT;
import static cu.su.Utils.LAUNCH_VIEW_WIDTH;


public class NetView extends GridPane {
    private Stage primaryStage;

    public NetView(Stage primaryStage) {
        super();
        this.primaryStage = primaryStage;

        setAlignment(Pos.CENTER);
        setHgap(5);
        setVgap(10);
        setPadding(new Insets(10, 10, 10, 10));
        Label labelTitle = new Label("Welcome to The BattleShip Game");
        labelTitle.setFont(new Font(18));

        add(labelTitle, 0, 0, 4, 1);
        setHalignment(labelTitle, HPos.CENTER);

        RadioButton radioButtonNew = new RadioButton("Start a new game");
        Label labelPortListen = new Label("Listen on port:");
        TextField textFieldPortListen = new TextField();
        textFieldPortListen.setText("45071");  //for dev

        add(radioButtonNew, 0, 1, 4, 1);
        add(labelPortListen, 1, 2);
        add(textFieldPortListen, 2, 2);

        RadioButton radioButtonExisting = new RadioButton("Connect to existing game");
        Label labelComputer = new Label("Computer:");
        TextField textFieldComputer = new TextField();
        Label labelPort = new Label("Port Number:");
        TextField textFieldPort = new TextField();
        textFieldPort.setText("45071");  //for dev
        textFieldComputer.setText("localhost");

        add(radioButtonExisting, 0, 3, 4, 1);
        add(labelComputer, 1, 4);
        add(textFieldComputer, 2, 4);
        add(labelPort, 1, 5);
        add(textFieldPort, 2, 5);

        Button buttonOK = new Button("OK");
        Button buttonCancel = new Button("Cancel");
        buttonOK.setPrefWidth(80);
        buttonCancel.setPrefWidth(80);

        HBox hBoxButton = new HBox(20);
        hBoxButton.setAlignment(Pos.CENTER);
        hBoxButton.setPadding(new Insets(10, 10, 10, 10));
        hBoxButton.getChildren().addAll(buttonOK, buttonCancel);
        add(hBoxButton, 0, 6, 4, 1);

        ToggleGroup toggleGroupRadio = new ToggleGroup();
        radioButtonNew.setToggleGroup(toggleGroupRadio);
        radioButtonNew.setSelected(true);
        radioButtonExisting.setToggleGroup(toggleGroupRadio);
        textFieldComputer.setDisable(true);
        textFieldPort.setDisable(true);

        toggleGroupRadio.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) -> {
                    Object toggle = toggleGroupRadio.getSelectedToggle();
                    if (toggle.equals(radioButtonNew)) {
                        textFieldPortListen.setDisable(false);
                        textFieldComputer.setDisable(true);
                        textFieldPort.setDisable(true);
                    } else if (toggle.equals(radioButtonExisting)) {
                        textFieldPortListen.setDisable(true);
                        textFieldComputer.setDisable(false);
                        textFieldPort.setDisable(false);
                    }
                }
        );

        buttonCancel.setOnAction(event -> {
            System.exit(0);
        });

        buttonOK.setOnAction(event -> {
            GameClient client = null;
            boolean hoster = true;
            try {
                if (radioButtonNew.isSelected()) {
                    String portStr = textFieldPortListen.getText().trim();
                    int port = Integer.parseInt(portStr);
                    if (port < 0 || port > 65535) {
                        throw new GameException("Port must between 0 to 65535!");
                    }
                    new GameServer(port);
                    client = new GameClient("localhost", port);
                } else if (radioButtonExisting.isSelected()) {
                    String portStr = textFieldPort.getText().trim();
                    int port = Integer.parseInt(portStr);
                    if (port < 0 || port > 65535) {
                        throw new GameException("Port must between 0 to 65535!");
                    }
                    String computer = textFieldComputer.getText().trim();
                    if (computer.equals("")) {
                        throw new GameException("computer must be a valid id or computer name!");
                    }
                    client = new GameClient(computer, port);
                    hoster = false;
                }
                Scene scene = new Scene(new LaunchView(primaryStage, client, hoster), LAUNCH_VIEW_WIDTH, LAUNCH_VIEW_HEIGHT);
                scene.getStylesheets().add("/css/battleship.css");
                //scene.getStylesheets().add(BattleShip.class.getResource("/css/battleship.css").toExternalForm());
                primaryStage.setScene(scene);
               // primaryStage.setScene();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Port must be number!",
                        ButtonType.YES);
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        e.getMessage(),
                        ButtonType.YES);
                alert.showAndWait();
            }

        });
    }
}
