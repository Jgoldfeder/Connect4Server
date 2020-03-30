package corona.games.util;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class MessageSender implements Runnable {

    private Socket socket;
    private LinkedBlockingDeque<Message> outgoingMessages;
    private volatile boolean shutdown = false;

    public MessageSender(Socket s, LinkedBlockingDeque<Message> outgoingMessages) {
        this.socket = s;
        this.outgoingMessages = outgoingMessages;
    }


    public void shutdown() {
        this.shutdown = true;
    }    
    
    @Override
    public void run() {
        while (!shutdown) {
            Message m = null;
            try {
                m = outgoingMessages.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (m != null) {
                try {
                    socket.getOutputStream().write(m.getNetworkPayload());
                    socket.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

