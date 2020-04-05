package corona.games.communication;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageReceiver implements Runnable {

    private Socket socket;
    private LinkedBlockingDeque<Message> incomingMessages;
    private volatile boolean shutdown = false;

    public MessageReceiver(Socket s, LinkedBlockingDeque<Message> incomingMessages) {
        this.socket = s;
        this.incomingMessages = incomingMessages;
    }

    public void shutdown() {
        this.shutdown = true;
    }
    
    
    public static Message read(Socket socket){
        Message  msg = null;
        // This number is kinda random have to really fine tune ideal message size
        byte[] buffer = new byte[40960];
        int i = 0;
        try {
            while(i <= 1){
                i = socket.getInputStream().read(buffer);
                if(i > 1) {
                    msg = new Message(buffer);
                    if(msg.getMessageType()==Message.MessageType.GARBAGE){
                        msg = null;
                        i = 0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 

        return msg;
    }
    
    
    
    @Override
    public void run() {
        while (!shutdown) {
            if (socket.isConnected()) {
                try {
                    Message msg = read(socket);
                    incomingMessages.put(msg);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
