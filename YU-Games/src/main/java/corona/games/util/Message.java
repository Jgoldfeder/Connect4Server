package corona.games.util;
public class Message {
    public static enum MessageType {
        INIT_CLIENT, INIT_RESPONSE, CHAT_MSG, EXIT;
    }
    public long clientID = 0;
    public String msg = null;
}