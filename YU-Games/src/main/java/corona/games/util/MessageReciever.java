package corona.games.util;

import java.io.InputStream;
import java.util.concurrent.LinkedBlockingDeque;

import corona.games.util.Message;

public class MessageReciever implements Runnable{

    
    private LinkedBlockingDeque<Message> incomingMessages;
    private volatile int shutdown = 0;

    public MessageReciever(InputStream in,LinkedBlockingDeque<Message> incomingMessages) {
        this.in = in;
        this.incomingMessages = incomingMessages;
    }
    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
