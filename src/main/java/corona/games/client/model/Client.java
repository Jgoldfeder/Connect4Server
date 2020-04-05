package corona.games.client.model;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.UUID;

import corona.games.client.controller.GUIManager;
import corona.games.util.*;
import corona.games.util.Message.MessageType;

public class Client implements Runnable {

    private volatile boolean shutdown = false;

    private String host;
    private int port;
    private Socket socket;

    private MessageSender sender;

    private LinkedBlockingDeque<Message> outgoingMessages;
    private LinkedBlockingDeque<Message> chatMessages;
    private UUID clientID;
    private String username;

    public Client(String hostName, int port) {
        this.host = hostName;
        this.port = port;

        this.outgoingMessages = new LinkedBlockingDeque<>();
        this.chatMessages = new LinkedBlockingDeque<>();
        // set default to an invalid value
        this.clientID = null;
    }

    @Override
    public void run() {
        startGUI();
        this.username = getUsername();
        if(this.host == null) {
            this.host = getHostName();
            this.port = getPortNumber();
        }

        // connect to server
        try {
            this.socket = new Socket(this.host, this.port);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("could not determine the IP addeess of " + this.host + ":" + this.port);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not connect to the server at " + this.host + ":" + this.port);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Port number: " + port + " is not valid");
        }

        // start up threads to send data
        sender = new MessageSender(this.socket, this.outgoingMessages);
        new Thread(sender).start();

        // initial handshake
        // send username
        sendMessage(new Message(Message.MessageType.INIT_CLIENT, this.username, null, this.username));
        // wait for client id
        Message msg = MessageReceiver.read(socket);
        if (msg.getMessageType() != MessageType.INIT_CLIENT) {
            throw new IllegalArgumentException("Client does not conform to protocal!");
        }

        this.clientID = msg.getClientID();

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
                GUIManager.setQueuesAndID(chatMessages, outgoingMessages, clientID);
                javafx.application.Application.launch(GUIManager.class);
            }
        }.start();
    }

    private String getUsername() {
        return GUIManager.getUsername();
    }
    private String getHostName() {
        return GUIManager.getHostname();
    }
    private int getPortNumber() {
        return GUIManager.getPort();
    }

    public static void main(String[] args) {
        String host = (args.length == 2) ? args[0] : null;
        int port;
        try {
            port = (args.length == 2) ? Integer.parseInt(args[1]) : 0;
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("port number must be a valid integer");
        }
        Client client = new Client(host, port);
        new Thread(client).run();
    }
}