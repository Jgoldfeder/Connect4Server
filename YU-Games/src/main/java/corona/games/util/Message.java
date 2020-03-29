package corona.games.util;

import java.nio.ByteBuffer;

public class Message {

    public static enum MessageType {
        INIT_CLIENT, INIT_RESPONSE, CHAT_MSG, EXIT;

        public char getChar() {
            switch(this) {
                case INIT_CLIENT:
                    return 'I';
                case INIT_RESPONSE:
                    return 'R';
                case CHAT_MSG:
                    return 'M';
                case EXIT:
                    return 'E';
            }
            return 'z';
        }

        public static MessageType getType(char c) {
            switch(c) {
                case 'I':
                    return INIT_CLIENT;
                case 'R':
                    return INIT_RESPONSE;
                case 'M':
                    return CHAT_MSG;
                case 'E':
                    return EXIT;
            }
            return null;
        }
    }


    long clientID = 0;
    String msg = null;
    char messageType;
    byte[] networkPayload;
    private final int maxSize = 40960;

    public Message(MessageType mt, String message) {


        this.messageType = mt.getChar();
        this.msg = message;
    }


    public byte[] getNetworkPayload() {

        if(this.networkPayload != null) {
            return this.networkPayload;
        }

        /*
        buffer size =
        1 char (msg type) = 2 bytes
        1 long (clientid) = 8 bytes
        1 int (size of message) = 4 bytes
        = 14 + message
        */
        byte[] messageContent = this.msg.getBytes();
        int bufferSize = 14 + messageContent.length;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.clear();

        buffer.putChar(this.messageType);
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

        this.messageType = buffer.getChar();
        this.clientID = buffer.getLong();
        int msgSize = buffer.getInt();
        byte[] bits = new byte[msgSize];
        buffer.get(bits);
        this.msg = new String(bits);
    }
}