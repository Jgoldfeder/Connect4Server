package corona.games.server;

import java.io.*; 
import java.util.*; 
import java.net.*; 
import corona.games.communication.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import static corona.games.communication.Message.MessageType;
import java.util.UUID;


// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    private Socket socket; 
    private UUID clientID;
    private Server server;
    private MessageReceiver reciever;
    private MessageSender sender;
    private LinkedBlockingDeque<Message> outgoingMessages;
    private LinkedBlockingDeque<Message> incomingMessages;
    private String username;
    private Lobby lobby;
    
    // Constructor 
    ClientHandler(Server server,Socket s,UUID clientID,Lobby lobby)  
    { 
        this.server= server;
        this.socket = s; 
        this.clientID=clientID;
        this.outgoingMessages = new LinkedBlockingDeque<>();
        this.incomingMessages = new LinkedBlockingDeque<>();

        this.sender = new MessageSender(s,outgoingMessages);  
        this.reciever = new MessageReceiver(s,incomingMessages);
        
        this.lobby = lobby;
        server.run(sender);
        server.run(reciever);
    } 
  
    public Message readMessage(){
        return incomingMessages.poll();
    }
    
    public Message readMessage(long timeoutInMilliseconds){
        try{
            return incomingMessages.poll(timeoutInMilliseconds, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void writeMessage(Message m) {
        if(m.getClientID().equals(clientID)){
            // we should never send a message to thw same client it came from
            return;
        }       
        try {
            this.outgoingMessages.put(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public UUID getID(){
        return clientID;
    }
    
    @Override
    public void run()  
    { 
        try
        { 
            // perform initial handshake
            // first get username, then send back clientID        
            Message msg = incomingMessages.poll(1000, TimeUnit.MILLISECONDS);
            if(msg == null){
                //we timed out  before getting a response
                shutdown();
                throw new IllegalArgumentException("Initial Handshake timed out. Client is unresponsive. Shutting down connection to client.");
            }
            
            if(msg.getMessageType() != MessageType.INIT_CLIENT){
                shutdown();
                throw new IllegalArgumentException("Client does not conform to protocal!");
            }
 
            this.username = msg.getMessage();
            
            //send initial info to Client. Client should not send anything else until this initial handshake is completed
            Message m = new Message(MessageType.INIT_CLIENT,"",clientID,"server");
            writeMessage(m);
            
            lobby.addClient(this);
            
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 
    
    public void shutdown(){
        reciever.shutdown();
        sender.shutdown();
        try{
            socket.close();                                 
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    }
} 