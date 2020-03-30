package corona.games.util;

import java.nio.ByteBuffer;

public class Message {

    public static enum MessageType {
        INIT_CLIENT, INIT_RESPONSE, CHAT_MSG, EXIT;
    }
    
    private long clientID = 0;
    private String msg = null;
    private MessageType messageType;
    private byte[] networkPayload;

    public Message(MessageType mt, String message,long clientID) {

        this.messageType = mt;
        this.msg = message;
    }

    public long getClientID(){
        return clientID;
    }
    
    public String getMessage(){
        return msg;
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
        = 16 + message
        */
        byte[] messageContent = this.msg.getBytes();
        int bufferSize = 16 + messageContent.length;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.clear();

        buffer.putInt(this.messageType.ordinal());
        buffer.putLong(this.clientID);
        buffer.putInt(messageContent.length);
        buffer.put(messageContent);
        buffer.flip();
        this.networkPayload = buffer.array();
        return this.networkPayload;
    }

    public Message(byte[] networkPayload) {
        ByteBuffer buffer = ByteBuffer.wrap(this.networkPayload);
        buffer.clear();

        this.messageType = MessageType.values()[buffer.getInt()];
        this.clientID = buffer.getLong();
        int msgSize = buffer.getInt();
        byte[] bits = new byte[msgSize];
        buffer.get(bits);
        this.msg = new String(bits);
        this.networkPayload = networkPayload;
    }
}