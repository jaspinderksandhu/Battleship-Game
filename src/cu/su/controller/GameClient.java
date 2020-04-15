package cu.su.controller;

import cu.su.net.Client;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.util.function.Consumer;

public class GameClient extends Client {
    private Consumer<Object> applyMessage;

    /**
     * Constructor opens a connection to a Hub.  This constructor will
     * block while waiting for the connection to be established.
     * @author k4
     *
     * @param hubHostName The host name (or IP address) of the computer where the Hub is running.
     * @param hubPort     The port number on which the Hub is listening for connection requests.
     * @throws IOException if any I/O exception occurs while trying to connect.
     */
    public GameClient(String hubHostName, int hubPort) throws IOException {
        super(hubHostName, hubPort);
    }

    public void setApplyMessage(Consumer<Object> func) {
        applyMessage = func;
    }

    @Override
    protected void messageReceived(Object message) {
        if (applyMessage == null) {
            return;
        }
        applyMessage.accept(message);
    }

    @Override
    protected void playerDisconnected(int departingPlayerID) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Somebody has left the game, game will quit now!.",
                    ButtonType.YES);
            alert.showAndWait();
            System.exit(-1);
        });
    }

    @Override
    protected void connectionClosedByError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Server has already closed, game will quit now!.",
                    ButtonType.YES);
            alert.showAndWait();
            System.exit(-1);
        });
    }
}
