package corona.games.util;

import java.nio.ByteBuffer;

public class Message {

    public static enum MessageType {
        GARBAGE,
        INIT_CLIENT,CHAT_MSG,
        SHUT_DOWN,
        EXIT_PHASE,
        CREATE_GAME,
        JOIN_GAME,
        REQUEST_CHAT_LIST,
        CHAT_LIST,
        REQUEST_GAME_LIST,
        GAME_LIST,
        START_GAME,
        GAMEPLAY_INFO,
        CONFIRM_READY,
        UNCONFIRM_READY,
        PLAYER_HAS_JOINED,
        PLAYER_HAS_LEFT;
    }
    
    private long clientID = 0;
    private String msg = null;
    private MessageType messageType;
    private byte[] networkPayload;
    private String username;
    public Message(MessageType mt, String message, long clientID, String username) {
        this.messageType = mt;
        this.msg = message;
        this.username = username;
    }

    public long getClientID(){
        return clientID;
    }
    
    public String getMessage(){
        return msg;
    }

    public String getUsername() {
        return this.username;
    }
    
    public MessageType getMessageType(){
        return messageType;
    }
    
    public byte[] getNetworkPayload() {

        if(this.networkPayload != null) {
            return this.networkPayload;
        }

        /*
        buffer size =
        1 int (msg type) = 4 bytes
        1 long (clientid) = 8 bytes
        1 int (size of message) = 4 bytes
        1 int (size of name) = 4 bytes
        = 20 + message + username
        */
        int bufferSize;
        byte[] messageContent;
        byte[] usernameBytes;
        if(msg == null) msg = "";
        //maybe throw error
        if(this.username == null) this.username = "";
        messageContent = this.msg.getBytes();
        usernameBytes = this.username.getBytes();
        bufferSize = 20 + messageContent.length + usernameBytes.length;
        
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.clear();

        buffer.putInt(this.messageType.ordinal());
        buffer.putLong(this.clientID);
        buffer.putInt(messageContent.length);
        buffer.put(messageContent);
        buffer.putInt(usernameBytes.length);
        buffer.put(usernameBytes);
        buffer.flip();
        this.networkPayload = buffer.array();
        return this.networkPayload;
    }

    public Message(byte[] networkPayload) {
        ByteBuffer buffer = ByteBuffer.wrap(networkPayload);
        buffer.clear();

        this.messageType = MessageType.values()[buffer.getInt()];
        this.clientID = buffer.getLong();
        int msgSize = buffer.getInt();
        byte[] bits = new byte[msgSize];
        buffer.get(bits);
        this.msg = new String(bits);
        int usernameSize = buffer.getInt();
        byte[] usernameBits = new byte[usernameSize];
        buffer.get(usernameBits);
        this.username = new String(usernameBits);
        this.networkPayload = networkPayload;
    }
}