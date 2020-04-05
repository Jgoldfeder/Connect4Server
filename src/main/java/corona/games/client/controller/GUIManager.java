package corona.games.client.controller;

import corona.games.client.view.ChatRoom;
import corona.games.client.view.WelcomeBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.UUID;

import corona.games.util.Message;


public class GUIManager extends Application {

    private static String username;
    private static String hostname;
    private static int port;
    private static final CountDownLatch haveStartUpInfo = new CountDownLatch(1);
    private static LinkedBlockingDeque<Message> chatMessages;
    private static LinkedBlockingDeque<Message> outgoingMessages;
    private static UUID clientID;
    
    private TextArea transcript;

    public static String getUsername() {
        try {
            haveStartUpInfo.await();
        } catch (InterruptedException e) {
        }
        return username;
    }

    public static String getHostname() {
        try {
            haveStartUpInfo.await();
        } catch (InterruptedException e) {
        }
        return hostname;
    }

    public static int getPort() {
        try {
            haveStartUpInfo.await();
        } catch (InterruptedException e) {
        }
        return port;
    }

    public static void setQueuesAndID(LinkedBlockingDeque<Message> chatMessage, LinkedBlockingDeque<Message> outgoingMessage,
    UUID clientId) {
        chatMessages = chatMessage;
        outgoingMessages = outgoingMessage;
        clientID = clientId;
    }



    public static void setUserInfo(String uname, String hname, String pnum) {
        username = uname;
        hostname = hname;
        // TODO: deal with error handling
        port = (pnum.length() == 0) ? 0 : Integer.parseInt(pnum);
        haveStartUpInfo.countDown();
    }

    private void startMessagePolling() {
        this.transcript = new TextArea();
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

        WelcomeBox.display();

        startMessagePolling();

        ChatRoom cr = new ChatRoom(chatMessages, outgoingMessages, transcript, clientID, username);
        cr.display();
    }

    public static void main(String[] args) {
        // launch(args);
        new Thread(){
            @Override
            public void run() {
                javafx.application.Application.launch(GUIManager.class);
            }
        }.start();
        String name = GUIManager.getUsername();
        System.out.println(name);

    }
}