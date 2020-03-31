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
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.util.Message;
import corona.games.util.Message.MessageType;

public class Welcome extends Application {

    private static String username;
    private static final CountDownLatch haveUserNameLatch = new CountDownLatch(1);
    private static LinkedBlockingDeque<Message> chatMessages;
    private static LinkedBlockingDeque<Message> outgoingMessages;
    private static long clientID;
    
    private TextArea transcript;
    private TextField messageInput;
    private Button sendButton;

    public static String getUsername() {
        try {
            haveUserNameLatch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return username;
    }

    public static void setQueuesAndID(LinkedBlockingDeque<Message> chatMessage, LinkedBlockingDeque<Message> outgoingMessage,
    long clientId) {
        chatMessages = chatMessage;
        outgoingMessages = outgoingMessage;
        clientID = clientId;
    }



    private static void setUserName(String uname) {
        username = uname;
        haveUserNameLatch.countDown();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage stage = new Stage();
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
        stage.setTitle("Welcome to YU Game Hub");
        stage.setScene(scene);
        submit.setOnMousePressed(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                // TODO Auto-generated method stub
                setUserName(name.getText());
                stage.close();
            }
        });
        stage.showAndWait();

        this.transcript = new TextArea();
        this.transcript.setPrefRowCount(30);
        this.transcript.setPrefColumnCount(60);
        this.transcript.setWrapText(true);
        this.transcript.setEditable(false);

        sendButton = new Button("send");
        messageInput = new TextField();
        messageInput.setPrefColumnCount(40);
        sendButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                String message = messageInput.getText();
                Message m = new Message(MessageType.CHAT_MSG, message, clientID, username);
                try {
                    chatMessages.put(m);
                    outgoingMessages.put(m);
                    System.out.println("put a message");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

        messageInput.setEditable(true);
        messageInput.setDisable(false);

        HBox bottom = new HBox(8, new Label("YOU SAY:"), messageInput, sendButton);
        HBox.setHgrow(messageInput, Priority.ALWAYS);
        // HBox.setMargin(quitButton, new Insets(0,0,0,50));
        bottom.setPadding(new Insets(8));
        bottom.setStyle("-fx-border-color: black; -fx-border-width:2px");
        BorderPane root = new BorderPane(transcript);
        root.setBottom(bottom);

        stage.setScene( new Scene(root) );
        stage.setTitle("Networked Chat");
        stage.setResizable(false);
        // primaryStage.setOnHidden( e -> doQuit() );
        new Thread(){
            @Override
            public void run() {
                while(true) {
                    Message m = chatMessages.poll();
                    if(m != null) {
                        transcript.appendText(m.getUsername() + ": " + m.getMessage() + "\n\n");
                    }
                }
            }
        }.start();
        stage.show();
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