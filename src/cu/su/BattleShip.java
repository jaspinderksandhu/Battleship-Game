package cu.su;

import cu.su.view.NetView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static cu.su.Utils.NET_VIEW_HEIGHT;
import static cu.su.Utils.NET_VIEW_WIDTH;


/**
 * BattleShip class is the entry point of game. It extends Application class.
 *
 * @author k4
 */
public class BattleShip extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Create the launch view first, if user clicks the start button, the game view will launch.
     *
     * @param primaryStage stage of the game.
     * @throws Exception checking exception.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("BattleShip");
        Scene scene = new Scene(new NetView(primaryStage), NET_VIEW_WIDTH, NET_VIEW_HEIGHT);
        scene.getStylesheets().add("/css/battleship.css");
        //scene.getStylesheets().add(BattleShip.class.getResource("/css/battleship.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }
}
