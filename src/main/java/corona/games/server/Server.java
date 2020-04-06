package corona.games.server;

import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.*;

import java.util.UUID;


public class Server  
{ 
    private long clientCounter;    
    private int port;
    private ArrayList<ClientHandler> chatMembers = new ArrayList<>();
    private ExecutorService executor;
    private Lobby lobby;
    
    public static void main(String[] args) throws IOException  
    { 
        int port = 80;      
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }
        Server s = new Server(port);
        s.start();
    } 
  
    private Server(int port){
        this.port = port;
        this.clientCounter = 0;
        int cores = Runtime.getRuntime().availableProcessors();        
        this.executor = Executors.newFixedThreadPool(cores);
        this.lobby = new Lobby(this);
    }
    
    public void run(Runnable task){
        executor.submit(task);
    }
    
    private void start(){        

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
                                 
                UUID clientID = UUID.randomUUID();
                clientCounter++;
                 
                System.out.println("A new client is connected : " + s); 
                System.out.println("Client ID = "+clientID.toString());
                 
                System.out.println("Assigning new thread for this client"); 
                // create a new thread object
                ClientHandler client = new ClientHandler(this,s, clientID,lobby);
                Thread t = new Thread(client); 
                // Invoking the start() method 
                t.start();  
            } 

        } 
        catch (Exception e){ 
            e.printStackTrace(); 
        } 
    }

}