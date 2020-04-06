package corona.games.client.view.breakout;

import corona.games.communication.GameInfo;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by noamannenberg
 * on 4/5/20.
 */
public class TestApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameRoom room = new GameRoom(
                new GameInfo("Othello",
                        "Noams Game",
                        "Noam",
                        1,
                        2,
                        2)
        );
        room.display();
    }

    public static void main(String[] args) {
        launch(TestApp.class);
    }
}