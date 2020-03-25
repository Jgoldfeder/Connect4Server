import java.io.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.locks.ReentrantLock;

public class Server  
{ 
    static int numPlayers = 0;
    static ReentrantLock lock = new ReentrantLock();
    static ArrayList<ObjectOutputStream> outStreams = new ArrayList<>();
    public static void main(String[] args) throws IOException  
    { 
        int clientId = 1;
        
        int port = 80;
        
        if(args.length>0){
            port = Integer.parseInt(args[0]);
        }

        // server is listening on port
        ServerSocket ss = new ServerSocket(port); 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                OutputStream dos = s.getOutputStream();  
                InputStream dis = s.getInputStream(); 
                 
                 
                System.out.println("Assigning new thread for this client"); 
  
                // create a new thread object 
                Thread t = new ClientHandler(s, new ObjectOutputStream(dos),new ObjectInputStream(dis), clientId); 
  
                // Invoking the start() method 
                t.start(); 
                
                clientId++;
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler extends Thread  
{ 
    private ObjectInputStream odis = null; 
    private ObjectOutputStream odos = null; 
    private final Socket s; 
    private int clientId;
  
    // Constructor 
    public ClientHandler(Socket s, ObjectOutputStream dos, ObjectInputStream dis,int clientId)  
    { 
        this.s = s; 

            this.odos = dos;
            this.odis = dis; 
            this.clientId=clientId;
        // add this client to list of streams
        Server.lock.lock();
        try{
            Server.outStreams.add(odos);
        }finally{
            Server.lock.unlock();
        }
        
    } 
  
    @Override
    public void run()  
    { 
        int color = 0;
        Server.lock.lock();
        try{
            if(Server.numPlayers>=2){
                try
                { 
                    // closing resources 
                    System.out.println("GAME IS FULL");
                    this.odis.close(); 
                    this.odos.close(); 
                      
                }catch(IOException e){ 
                    e.printStackTrace(); 
                }
                System.exit(0);
            }
            Server.numPlayers++;
            color = Server.numPlayers;

        }finally{
            Server.lock.unlock();
        }

        try
        { 
            //send color
            Info info = new Info();
            info.color = color;
            info.ID = clientId;
            odos.writeObject(info);
  
            while(true){
                info = (Info)odis.readObject();
                if(info.exit){
                    break;
                }
                //broadcast info
                for(ObjectOutputStream s: Server.outStreams){
                    s.writeObject(info);
                }
            }
        
            // closing resources 
            this.odis.close(); 
            this.odos.close(); 
              
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
    } 
} 