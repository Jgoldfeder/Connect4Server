package corona.games.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import corona.games.util.*;
import corona.games.util.Message.MessageType;

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
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a username:");
        String userName = scanner.nextLine();
        System.out.println();

        sendMessage(new Message(Message.MessageType.INIT_CLIENT, userName, 0));

        while (!shutdown) {
            Message m = null;
            try {
                m = incomingMessages.poll(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(m != null) {
                switch(m.getMessageType()) {
                    case CHAT_MSG:
                        System.out.println(m.getMessage());
                        String response = scanner.nextLine();
                        sendMessage(new Message(MessageType.CHAT_MSG,response,clientID));
                        break;
                    case INIT_RESPONSE:
                        this.clientID = m.getClientID();
                    case INIT_CLIENT:
                        this.clientID = m.getClientID();
                        System.out.println("From the server");
                        String response1 = scanner.nextLine();
                        sendMessage(new Message(MessageType.CHAT_MSG,response1,clientID));
                        break;
                    default:
                        System.out.println("Shouldnt be here");
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

    public static void main(String[] args) {
        Client client = new Client(args[0], Integer.parseInt(args[1]));

        new Thread(client).run();
    }
}