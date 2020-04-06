package corona.games.client.view.breakout;

import corona.games.client.view.game.Game;
import corona.games.communication.GameInfo;
import corona.games.logger.Loggable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by noamannenberg
 * on 4/5/20.
 */
public class GameRoom implements Loggable {
    Logger logger;
    GameInfo gameInfo;

    public GameRoom(GameInfo info) {
        this.gameInfo = info;
    }

    public void display() {
        Stage stage = new Stage();
        stage.setTitle(gameInfo.name);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(20);
        grid.setHgap(20);
        //Defining the Welcome Label
        Label welcomeLabel = new Label("Welcome to " + gameInfo.name + "!");
        GridPane.setConstraints(welcomeLabel, 0, 0);
        grid.getChildren().add(welcomeLabel);
        //Defining the Hosted Label
        Label hostedLabel = new Label(" Hosted by " + gameInfo.username);
        GridPane.setConstraints(hostedLabel, 0, 1);
        grid.getChildren().add(hostedLabel);
        //defining the Ready Button
        Button readyButton = new Button("Ready");
        GridPane.setConstraints(readyButton, 0, 2);
        grid.getChildren().add(readyButton);

        readyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                startGame();
            }
        });


        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();
    }

    private void startGame() {
        Game game = new Game("npm start --prefix ~/Projects/MultiplayerOthello");
        new Thread(game).start();
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(String msg) {
        if(this.logger == null) return;
        else this.logger.info(msg);
    }
}