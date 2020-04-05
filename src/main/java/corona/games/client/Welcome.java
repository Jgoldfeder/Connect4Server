package corona.games.client;

import java.security.Principal;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
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
import javafx.stage.WindowEvent;

public class Welcome extends Application {

    private static String username;
    private static final CountDownLatch haveUserNameLatch = new CountDownLatch(1);
    private static LinkedBlockingDeque<Message> chatMessages;
    private static LinkedBlockingDeque<Message> outgoingMessages;
    private static long clientID;
    
    private TextArea transcript;
    private TextField messageInputBox;
    private Button sendButton;

    public static String getUsername() {
        try {
            haveUserNameLatch.await();
        } catch (InterruptedException e) {
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

    private Scene makeWelcomeWindow(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        //Defining the Name text field
        Label nameLabel = new Label("Enter your Name:");
        final TextField nameEntryField = new TextField();
        nameEntryField.setPromptText("John");
        nameEntryField.setFocusTraversable(false);
        nameEntryField.setPrefColumnCount(10);
        GridPane.setConstraints(nameLabel, 0, 0);
        grid.getChildren().add(nameLabel);
        GridPane.setConstraints(nameEntryField, 0, 1);
        grid.getChildren().add(nameEntryField);
        nameEntryField.setOnKeyReleased(key->{
            if(key.getCode() == KeyCode.ENTER) {
                String name = nameEntryField.getText();
                if(name == null || name.equals("")) {
                    // TODO show in GUI
                    System.out.println("Can't be empty name");
                } else {
                    setUserName(nameEntryField.getText());
                    stage.close();
                }
            }
        });
        //Defining the Submit button
        Button submit = new Button("Submit");
        GridPane.setConstraints(submit, 1, 1);
        grid.getChildren().add(submit);

        Scene scene = new Scene(grid);
        submit.setOnMousePressed(new EventHandler<Event>() {
            
            @Override
            public void handle(Event event) {
                // TODO Auto-generated method stub
                setUserName(nameEntryField.getText());
                stage.close();
            }
        });

        return scene;
    }

    private Scene makeChatWindow(Stage stage) {
        this.transcript = new TextArea();
        this.transcript.setPrefRowCount(30);
        this.transcript.setPrefColumnCount(60);
        this.transcript.setWrapText(true);
        this.transcript.setEditable(false);

        sendButton = new Button("send");
        messageInputBox = new TextField();
        messageInputBox.setPrefColumnCount(40);
        messageInputBox.setOnKeyReleased(key -> {
            if(key.getCode() == KeyCode.ENTER) {
                String message = messageInputBox.getText();
                messageInputBox.clear();

                if(message == null || message.equals("")) {
                    // TODO show in GUI
                    System.out.println("Can't be empty message");
                } else {
                    Message m = new Message(MessageType.CHAT_MSG, message, clientID, username);
                try {
                    chatMessages.put(m);
                    outgoingMessages.put(m);
                    // System.out.println("put a message");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                }
            }
        });

        sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                // TODO Auto-generated method stub
                String message = messageInputBox.getText();
                messageInputBox.clear();
                Message m = new Message(MessageType.CHAT_MSG, message, clientID, username);
                try {
                    chatMessages.put(m);
                    outgoingMessages.put(m);
                    // System.out.println("put a message");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });

        messageInputBox.setEditable(true);
        messageInputBox.setDisable(false);

        HBox bottom = new HBox(8, new Label(""), messageInputBox, sendButton);
        HBox.setHgrow(messageInputBox, Priority.ALWAYS);
        // HBox.setMargin(quitButton, new Insets(0,0,0,50));
        bottom.setPadding(new Insets(8));
        bottom.setStyle("-fx-border-color: black; -fx-border-width:2px");
        BorderPane root = new BorderPane(transcript);
        root.setBottom(bottom);
        
        return new Scene(root);
    }

    private void startMessagePolling() {
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
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        Stage stage = new Stage();

        Scene scene = makeWelcomeWindow(stage);
        stage.setTitle("Welcome to YU Game Hub");
        stage.setScene(scene);
        stage.showAndWait();

        Scene chatWindow = makeChatWindow(primaryStage);
        primaryStage.setScene( chatWindow );
        primaryStage.setTitle("Networked Chat");
        primaryStage.setResizable(false);
        startMessagePolling();
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