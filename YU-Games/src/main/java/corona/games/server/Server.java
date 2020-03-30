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
        private LinkedBlockingDeque<Message> incomingMessages;

        // Constructor 
        private ClientHandler(Server server,Socket s,long clientID)  
        { 
            this.server= server;
            this.s = s; 
            this.clientID=clientID;
            this.outgoingMessages = new LinkedBlockingDeque<>();
            this.incomingMessages = new LinkedBlockingDeque<>();
            this.reciever = new MessageReciever(s,incomingMessages);
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
            
            
            Message[] msgs = null;
            while(true){
                msgs = readMessages();
                for(Message info: msgs){
                    if(info.getMessageType() == MessageType.EXIT){
                        //remove from chat list
                        server.chatMembers.remove(this);
                        break;
                    }
                    
                    //broadcast info
                    if(info.getMessageType() == MessageType.CHAT_MSG){
                        for(ClientHandler ch: server.chatMembers){
                            ch.writeMessage(info);
                        }
                    }
                }
            }     
            
        }
      
        private void writeMessage(Message m){
            outgoingMessages.add(m);
        }
        
        private Message[] readMessages(){
            ArrayList<Message> msgs = new ArrayList<>();
            incomingMessages.drainTo(msgs);
            return msgs.toArray(new Message[0]);
        }
      
        @Override
        public void run()  
        { 
            try
            { 
                //send initial info to Client
                Message m = new Message(MessageType.INIT_CLIENT,"",clientID);
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