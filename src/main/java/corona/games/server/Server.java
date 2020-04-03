package corona.games.server;

import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.locks.ReentrantLock;
import corona.games.util.*;
import com.google.gson.Gson;
import java.util.concurrent.LinkedBlockingDeque;
import static corona.games.util.Message.MessageType;
public class Server  
{ 
    private long clientIDCounter;    
    private ReentrantLock lock = new ReentrantLock();
    private int port;
    private ArrayList<ClientHandler> chatMembers = new ArrayList<>();
    
    public static void main(String[] args) throws IOException  
    { 
        int port = 80;      
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }
        Server s = new Server(port);
        s.run();
    } 
  
    private Server(int port){
        this.port = port;
        clientIDCounter = 0;
    }
      
    private void run(){        

        // server is listening on port
        try{ 
        ServerSocket ss = new ServerSocket(port); 
              
            // running infinite loop for getting 
            // client request 
            while (true)  
            { 
                Socket s = null; 
              
      
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                
                long clientID = clientIDCounter;
                clientIDCounter++;
                  
                System.out.println("Assigning new thread for this client"); 
                // create a new thread object 
                Thread t = new ClientHandler(this,s, clientID); 
  
                // Invoking the start() method 
                t.start(); 
                
                  
            } 

        } 
        catch (Exception e){ 
            e.printStackTrace(); 
        } 
    }


    // ClientHandler class 
    class ClientHandler extends Thread  
    { 
        private ObjectInputStream odis = null; 
        private ObjectOutputStream odos = null; 
        private final Socket s; 
        private long clientID;
        private Server server;
        private MessageReciever reciever;
        private MessageSender sender;
        private LinkedBlockingDeque<Message> outgoingMessages;
        private String username;
        
        // Constructor 
        private ClientHandler(Server server,Socket s,long clientID)  
        { 
            this.server= server;
            this.s = s; 
            this.clientID=clientID;
            this.outgoingMessages = new LinkedBlockingDeque<>();
            this.sender = new MessageSender(s,outgoingMessages);   
            Thread rThread = new Thread(reciever);
            rThread.start();
            Thread sThread = new Thread(sender);
            sThread.start();
        } 
      
        private void chatRoom(){
            // add this client to chat
            server.lock.lock();
            try{
                server.chatMembers.add(this);
            }finally{
                server.lock.unlock();
            }
            
            
            Message info = null;
            while(true){
                info = MessageReciever.read(s);
                if(info.getMessageType() == MessageType.EXIT){
                    //remove from chat list
                    server.chatMembers.remove(this);
                    break;
                }
                
                //broadcast info
                if(info.getMessageType() == MessageType.CHAT_MSG){
                    //TODO add check for broken connection and remove broken connections
                    for(ClientHandler ch: server.chatMembers){
                        if(ch==this) continue;
                        ch.writeMessage(info);
                    }
                }
                
            }     
            
        }
      
        public void writeMessage(Message m) {
            try {
                this.outgoingMessages.put(m);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        @Override
        public void run()  
        { 
            try
            { 
                // get initial client data. This consists of that client's username
                Message msg = MessageReciever.read(s);
                if(msg.getMessageType() != MessageType.INIT_CLIENT){
                    throw new IllegalArgumentException("Client does not conform to protocal!");
                }
                this.username = msg.getMessage();
                
                //send initial info to Client. Client should not send anything else until this initial handshake is completed
                Message m = new Message(MessageType.INIT_CLIENT,"",clientID,"server");
                writeMessage(m);
                
                //enter chat room
                chatRoom();
                             
                this.sender.shutdown(); 
                this.reciever.shutdown();
                s.close();                                 
            }catch(Exception e){ 
                e.printStackTrace(); 
            } 
        } 
    } 

}