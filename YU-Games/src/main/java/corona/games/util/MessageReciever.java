package corona.games.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.util.Message;

public class MessageReciever implements Runnable {

    private Socket socket;
    private LinkedBlockingDeque<Message> incomingMessages;
    private volatile boolean shutdown = false;

    public MessageReciever(Socket s, LinkedBlockingDeque<Message> incomingMessages) {
        this.socket = s;
        this.incomingMessages = incomingMessages;
    }

    public void shutdown() {
        this.shutdown = true;
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (!shutdown) {
            if (socket.isConnected()) {
                // This number is kinda random have to really fine tune ideal message size
                byte[] buffer = new byte[40960];
                int i;
                try {
                    i = socket.getInputStream().read(buffer);
                    if(i > 1) {
                        incomingMessages.put(new Message(buffer));
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

}
