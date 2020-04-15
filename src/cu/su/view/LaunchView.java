package cu.su.view;

import cu.su.controller.GameClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static cu.su.Utils.*;
import static cu.su.Utils.GAME_MODE.*;

public class LaunchView extends GridPane implements EventHandler<ActionEvent> {
    private Button normalButton;
    private Button salvaButton;
    private Button dualButton;
    private Stage primaryStage;
    private GameClient client;
    private boolean hoster;

    public LaunchView(Stage primaryStage, GameClient client, boolean hoster) {
        super();
        this.primaryStage = primaryStage;
        this.client = client;
        this.hoster = hoster;
        setAlignment(Pos.CENTER);
        setHgap(5);
        setVgap(20);

        Label labelTitle = new Label("Welcome to The BattleShip Game");
        labelTitle.setFont(new Font(18));
        add(labelTitle, 0, 0, 4, 1);
        setHalignment(labelTitle, HPos.CENTER);


        normalButton = new Button("NORMAL MODE");
        salvaButton = new Button("SALVA MODE");
        dualButton = new Button("DUAL MODE");
        Button helpButton = new Button("HELP");
        if (!hoster) {
            normalButton.setDisable(true);
            salvaButton.setDisable(true);
        }

        add(normalButton, 0, 1);
        add(salvaButton, 1, 1);
        add(dualButton, 2, 1);
        add(helpButton, 3, 1);

        helpButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Please set the initial positions of five warships in the grids.",
                    ButtonType.YES);
            alert.showAndWait();
        });

        normalButton.setOnAction(this);
        salvaButton.setOnAction(this);
        dualButton.setOnAction(this);

    }

    @Override
    public void handle(ActionEvent event) {
        Object source = event.getSource();
        GAME_MODE mode = NORMAL;
        if (source.equals(salvaButton)) {
            mode = SALVA;
        } else if (source.equals(dualButton)) {
            mode = DUAL;
        }
        Scene scene = new Scene(new GameView(primaryStage, client, mode, hoster), GAME_WIDTH, GAME_HEIGHT);
        scene.getStylesheets().add("/css/battleship.css");
        //scene.getStylesheets().add(BattleShip.class.getResource("/css/battleship.css").toExternalForm());
        primaryStage.setScene(scene);
       // primaryStage.setScene(new Scene(new GameView(primaryStage, client, mode, hoster), GAME_WIDTH, GAME_HEIGHT));
    }
}
