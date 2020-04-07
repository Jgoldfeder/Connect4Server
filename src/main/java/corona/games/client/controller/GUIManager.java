package corona.games.client.controller;

import corona.games.client.view.ChatBox;
import corona.games.client.view.WelcomeBox;
import corona.games.client.view.breakout.GameRoom;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;
import java.util.UUID;

import corona.games.communication.GameInfo;
import corona.games.communication.Message;
import corona.games.logger.Loggable;


public class GUIManager extends Application implements Loggable{

    private static String username;
    private static String hostname;
    private static int port;
    private static final CountDownLatch haveStartUpInfo = new CountDownLatch(1);
    private static LinkedBlockingDeque<Message> chatMessages;
    private static LinkedBlockingDeque<Message> outgoingMessages;
    private static UUID clientID;
    
    private static WelcomeBox wb;
    private static ChatBox cr;
    private TextArea transcript;

    private Thread messagePollingThread;

    private Logger logger;
    
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

    public static void setQueues(LinkedBlockingDeque<Message> chatMessage, LinkedBlockingDeque<Message> outgoingMessage) {
        chatMessages = chatMessage;
        outgoingMessages = outgoingMessage;
    }

    public static void setID(UUID newClientID) {
        clientID = newClientID;
        cr.setID(clientID);
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
       messagePollingThread = new Thread("GUIManager Message Polling Thread"){
            @Override
            public void run() {
                while(true) {
                    Message m = chatMessages.poll();
                    if(m != null) {
                        transcript.appendText(m.getUsername() + ": " + m.getMessage() + "\n\n");
                    }
                }
            }
        };
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        wb = new WelcomeBox();
        wb.display();

        startMessagePolling();
        cr = new ChatBox(chatMessages, outgoingMessages, transcript, clientID, username);
        GameInfo selected = cr.display();

        GameRoom gm = new GameRoom(selected);
        gm.display();
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

    public static void main(String[] args) {
        // launch(args);
        new Thread("GUIManager main method Thread"){
            @Override
            public void run() {
                javafx.application.Application.launch(GUIManager.class);
            }
        }.start();
        String name = GUIManager.getUsername();
        System.out.println(name);

    }
}