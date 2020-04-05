package corona.games.communication;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Message {

    private static final int UUID_BYTE_LENGTH = UUID.randomUUID().toString().getBytes().length;

    // this is essentially what we use if the ID passed in is Null. Otherwise, serializing would be difficult 
    private static final UUID NULL_ID = UUID.randomUUID();
    
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
    
    private UUID clientID = null;
    private String msg = null;
    private MessageType messageType;
    private byte[] networkPayload;
    private String username;
    public Message(MessageType mt, String message, UUID clientID, String username) {
        this.messageType = mt;
        this.msg = message;
        this.username = username;
        if(clientID == null){
            this.clientID = NULL_ID;
        }else{
            this.clientID = clientID;
        }
    }

    public UUID getClientID(){
        if(clientID == NULL_ID){
            return null;
        }
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
        1 int (size of message) = 4 bytes
        1 int (size of name) = 4 bytes
        1 UUID string (clientid) = UUID_BYTE_LENGTH bytes
        = 12 + UUID_BYTE_LENGTH + message + username
        */
        int bufferSize;
        byte[] messageContent;
        byte[] usernameBytes;
        byte[] uuidBytes;

        if(msg == null) msg = "";
        //maybe throw error
        if(this.username == null) this.username = "";
        messageContent = this.msg.getBytes();
        usernameBytes = this.username.getBytes();
        uuidBytes = this.clientID.toString().getBytes();
        bufferSize = 12 + UUID_BYTE_LENGTH + messageContent.length + usernameBytes.length;
        
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.clear();

        buffer.putInt(this.messageType.ordinal());
        buffer.put(uuidBytes);
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
        byte[] uuidBuffer = new byte[UUID_BYTE_LENGTH];
        buffer.get(uuidBuffer);
        this.clientID = UUID.fromString(new String(uuidBuffer));
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