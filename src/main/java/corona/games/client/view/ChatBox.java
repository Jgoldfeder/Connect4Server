package corona.games.client.view;

import javafx.application.Platform;
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
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.communication.Message;
import corona.games.communication.Message.MessageType;
public class ChatBox {

    private TextArea transcript;
    private TextField messageInputBox;
    private Button sendButton;
    private LinkedBlockingDeque<Message> chatMessages;
    private LinkedBlockingDeque<Message> outgoingMessages;
    private UUID clientID;
    private String username;

    public ChatBox(LinkedBlockingDeque<Message> chatMessages, LinkedBlockingDeque<Message> outgoingMessages, TextArea transcript,
                   UUID clientID, String username) {
        this.chatMessages = chatMessages;
        this.outgoingMessages = outgoingMessages;
        this.transcript = transcript;
        this.clientID = clientID;
        this.username = username;
    }
    public void display() {
        
        Stage stage = new Stage();
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        transcript.setPrefRowCount(30);
        transcript.setPrefColumnCount(60);
        transcript.setWrapText(true);
        transcript.setEditable(false);

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
                    System.out.println("here in the chat roon");
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

        stage.setScene(new Scene(root));
        stage.show();
    }
}