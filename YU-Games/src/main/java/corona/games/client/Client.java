package corona.games.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

    private volatile int shutdown = 0;

    private final String host;
    private final int port;
    private Socket socket;

    
    private MessageReciever receiver;
    private MessageSender sender;

    public Client(String hostName, int port) {
        this.host = hostName;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            socket = new Socket(this.host, this.port);
        } catch (UnknownHostException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    
    
}