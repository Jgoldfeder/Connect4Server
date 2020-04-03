package corona.games.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.util.*;
import corona.games.util.Message.MessageType;

public class Client implements Runnable {

    private volatile boolean shutdown = false;

    private final String host;
    private final int port;
    private Socket socket;

    private MessageSender sender;

    private LinkedBlockingDeque<Message> outgoingMessages;
    private LinkedBlockingDeque<Message> chatMessages;
    private long clientID;
    private String username;

    public Client(String hostName, int port) {
        this.host = hostName;
        this.port = port;

        this.outgoingMessages = new LinkedBlockingDeque<>();
        this.chatMessages = new LinkedBlockingDeque<>();
        // set default to an invalid value
        this.clientID = -1;
    }

    @Override
    public void run() {

        // connect to server
        try {
            this.socket = new Socket(this.host, this.port);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start up threads to send data
        sender = new MessageSender(this.socket, this.outgoingMessages);
        new Thread(sender).start();
        startGUI();
        this.username = getUsername();
        // initial handshake
        // send username
        sendMessage(new Message(Message.MessageType.INIT_CLIENT, this.username, -1, this.username));
        // wait for client id
        Message msg = MessageReceiver.read(socket);
        if (msg.getMessageType() != MessageType.INIT_CLIENT) {
            throw new IllegalArgumentException("Client does not conform to protocal!");
        }

        this.clientID = msg.getClientID();

        // start user input loop
        // Thread userLoop = new Thread(() -> userInputLoop());
        // userLoop.start();

        // enter server input loop
        while (!shutdown) {
            Message m = null;

            m = MessageReceiver.read(socket);

            if (m != null) {
                switch (m.getMessageType()) {
                    case CHAT_MSG:
                        try {
                            chatMessages.put(m);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case INIT_CLIENT:
                        //after handshake, we should never see this command
                        System.out.println("Shouldn't be here");
                        break;
                    default:
                        System.out.println(m.getMessageType());
                }
            }
        }
    }

    private void userInputLoop(){

        Scanner scanner = new Scanner(System.in);

        while (!shutdown) {  
            String response = scanner.nextLine();
            sendMessage(new Message(MessageType.CHAT_MSG,response,clientID,this.username));
        }
        
    }
    
    public void sendMessage(Message m) {
        try {
            this.outgoingMessages.put(m);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startGUI() {
        new Thread(){
            @Override
            public void run() {
                Welcome.setQueuesAndID(chatMessages, outgoingMessages, clientID);
                javafx.application.Application.launch(Welcome.class);
            }
        }.start();
    }

    private String getUsername() {
        return Welcome.getUsername();
    }

    public static void main(String[] args) {
        Client client = new Client(args[0], Integer.parseInt(args[1]));

        new Thread(client).run();
    }
}