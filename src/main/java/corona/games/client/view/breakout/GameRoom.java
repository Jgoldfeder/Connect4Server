package corona.games.client.view.breakout;

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
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("npm start --prefix ~/Documents/Git/MultiplayerOthello");
            InputStream in = p.getInputStream();
            OutputStream out = p.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
//            writer.write("((" + "hello" + ") && echo --EOF--) || echo --EOF--\n)");
//            writer.flush();
            System.out.println("Finished Writing");
            System.out.println("Process is alive: " + p.isAlive());
            Scanner scanner = new Scanner(in);
            new Thread(() -> {
                while (scanner.hasNext()) {
                    String input = scanner.nextLine();
                    System.out.println(input);
                }
            }).start();
            System.out.println("Process is alive: " + p.isAlive());
        } catch (IOException e) {
            e.printStackTrace();
        }
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