package corona.games.client.view;

import java.util.logging.Logger;

import corona.games.client.controller.GUIManager;
import corona.games.logger.Loggable;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class WelcomeBox implements Loggable {

    private Logger logger;
    public void display() {
        Stage stage = new Stage();
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(20);
        grid.setHgap(20);
        //Defining the Name text field
        Label nameLabel = new Label("Enter your Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        grid.getChildren().add(nameLabel);
        final TextField nameEntryField = new TextField();
        nameEntryField.setPromptText("John");
        nameEntryField.setFocusTraversable(false);
        nameEntryField.setPrefColumnCount(10);
        GridPane.setConstraints(nameEntryField, 0, 1);
        grid.getChildren().add(nameEntryField);
        // host info
        Label hostLabel = new Label("Enter the host name:");
        GridPane.setConstraints(hostLabel, 0, 2);
        grid.getChildren().add(hostLabel);
        final TextField hostEntryField = new TextField();
        hostEntryField.setPrefColumnCount(10);
        GridPane.setConstraints(hostEntryField, 0, 3);
        grid.getChildren().add(hostEntryField);
        // port info
        Label portLabel = new Label("Enter the port number:");
        GridPane.setConstraints(portLabel, 0, 4);
        grid.getChildren().add(portLabel);
        final TextField portEntryField = new TextField();
        portEntryField.setPrefColumnCount(10);
        GridPane.setConstraints(portEntryField, 0, 5);
        grid.getChildren().add(portEntryField);

        // allow the user to submit by pressing enter on the name field
        nameEntryField.setOnKeyReleased(key->{
            if(key.getCode() == KeyCode.ENTER) {
                String name = nameEntryField.getText();
                if(name == null || name.equals("")) {
                    // TODO show in GUI
                    System.out.println("Can't be empty name");
                } else {
                    GUIManager.setUserInfo(nameEntryField.getText(), hostEntryField.getText(), portEntryField.getText());
                    stage.close();
                }
            }
        });

        //Defining the Submit button
        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 6);
        grid.getChildren().add(submit);

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        submit.setOnMousePressed(new EventHandler<Event>() {
            
            @Override
            public void handle(Event event) {
                // TODO Auto-generated method stub
                GUIManager.setUserInfo(nameEntryField.getText(), hostEntryField.getText(), portEntryField.getText());
                stage.close();
            }
        });
        
        stage.showAndWait();
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