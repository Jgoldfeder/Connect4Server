package corona.games.client;

import javafx.application.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.util.Message;
import corona.games.util.Message.MessageType;

public class ChatRoom extends Application {

    private static LinkedBlockingDeque<Message> chatMessages;
    private static LinkedBlockingDeque<Message> outgoingMessages;
    UUID clientID;
    String username;

    private TextArea transcript;
    private TextField messageInput;
    private Button sendButton;

    public ChatRoom(LinkedBlockingDeque<Message> chatMessages, LinkedBlockingDeque<Message> outgoingMessages,
            UUID clientID, String username) {
        this.chatMessages = chatMessages;
        this.clientID = clientID;
        this.username = username;
        this.outgoingMessages = outgoingMessages;
    }

    

    public ChatRoom(){}

    public Scene getScene() {
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
        return new Scene(root);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
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

        primaryStage.setScene( new Scene(root) );
        primaryStage.setTitle("Networked Chat");
        primaryStage.setResizable(false);
        // primaryStage.setOnHidden( e -> doQuit() );
        new Thread(){
            @Override
            public void run() {
                while(true) {
                    Message m = chatMessages.poll();
                    transcript.appendText(m.getUsername() + ": " + m.getMessage() + "\n\n");
                }
            }
        }.start();
        primaryStage.show();
	}
    public static void main(String[] args) {
        launch(args);
    }
}
