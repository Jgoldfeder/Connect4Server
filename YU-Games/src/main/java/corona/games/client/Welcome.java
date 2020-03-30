package corona.games.client;

import java.security.Principal;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class Welcome extends Application {

    private static String username;
    private static final CountDownLatch haveUserNameLatch = new CountDownLatch(1);

    public static String getUsername() {
        try {
            haveUserNameLatch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return username;
    }

    private static void setUserName(String uname) {
        username = uname;
        haveUserNameLatch.countDown();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
       //Creating a GridPane container
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        //Defining the Name text field
        final TextField name = new TextField();
        name.setPromptText("Enter your first name.");
        name.setPrefColumnCount(10);
        name.getText();
        GridPane.setConstraints(name, 0, 0);
        grid.getChildren().add(name);

        //Defining the Submit button
        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 0);
        grid.getChildren().add(submit);

        Scene scene = new Scene(grid);
        primaryStage.setTitle("Welcome to YU Game Hub");
        primaryStage.setScene(scene);
        submit.setOnMousePressed(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                // TODO Auto-generated method stub
                setUserName(name.getText());
                primaryStage.close();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        // launch(args);
        new Thread(){
            @Override
            public void run() {
                javafx.application.Application.launch(Welcome.class);
            }
        }.start();
        String name = Welcome.getUsername();
        System.out.println(name);

    }
}