package corona.games.client.view.lobby;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameConfigurator {
    private String nameOfGame;

    public String display() {
        Stage stage = new Stage();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(20);
        grid.setHgap(20);
        Label gameNameLabel = new Label("Enter name for your game:");
        GridPane.setConstraints(gameNameLabel, 0, 0);
        final TextField gameNameEntryField = new TextField();
        gameNameEntryField.setPromptText("John");
        gameNameEntryField.setFocusTraversable(true);
        gameNameEntryField.setPrefColumnCount(10);
        GridPane.setConstraints(gameNameEntryField, 0, 1);

        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 6);
        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.getChildren().addAll(gameNameLabel, gameNameEntryField, submit);

        submit.setOnMousePressed(e -> {
            this.nameOfGame = gameNameEntryField.getText();
            if(this.nameOfGame == null || this.nameOfGame.isEmpty()) {
                System.out.println("need to make pop up");
                return;
            }
            else {
                stage.close();
            }
        });

        stage.setScene(new Scene(grid));
        stage.showAndWait();
        return nameOfGame;
    }
}