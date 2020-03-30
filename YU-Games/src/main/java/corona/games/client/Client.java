package corona.games.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.util.*;

public class Client implements Runnable {

    private volatile boolean shutdown = false;

    private final String host;
    private final int port;
    private Socket socket;

    private MessageReciever receiver;
    private MessageSender sender;

    private LinkedBlockingDeque<Message> outgoingMessages;
    private LinkedBlockingDeque<Message> incomingMessages;

    private long clientID;

    public Client(String hostName, int port) {
        this.host = hostName;
        this.port = port;

        this.outgoingMessages = new LinkedBlockingDeque<>();
        this.incomingMessages = new LinkedBlockingDeque<>();

        this.clientID = 0;
    }

    @Override
    public void run() {

        try {
            this.socket = new Socket(this.host, this.port);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender = new MessageSender(this.socket, this.outgoingMessages);
        receiver = new MessageReciever(this.socket, this.incomingMessages);

        new Thread(sender).start();
        new Thread(receiver).start();
        while(!shutdown) {

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
}