import java.util.*; 
import java.net.*; 
import java.io.*; 
import java.util.concurrent.locks.ReentrantLock;

public class Client
{
    
    private static String ip_name;
    private static int port;
    private Socket socket            = null;  
    private ObjectInputStream odis;
    private ObjectOutputStream odos;
    private ReentrantLock lock = new ReentrantLock();
    private Queue<Info> infos = new LinkedList<>();

    Client(String ip_name,int port){
        this.ip_name=ip_name;
        this.port=port;
    }
    
    
    void sendInfo(Info info){
        try{
            odos.writeObject(info);
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    ArrayList<Info> getInfo(){
        ArrayList<Info> i = new ArrayList<>();
        lock.lock();
        try{
            while(!infos.isEmpty()){
                i.add(infos.remove());
            }
        }finally{
            lock.unlock();
        }
        return i;
    }
    
    Info connect(){
     
          try
        { 
            System.out.println("Connecting to network");  

            InetAddress ip = InetAddress.getByName(ip_name); 
      
            // establish the connection with server port  
            Socket s = new Socket(ip, port);
            System.out.println("Connected");

            // obtaining input and out streams             
            odos = new ObjectOutputStream(s.getOutputStream());
            odis = new ObjectInputStream(s.getInputStream()); 

            // get what color we are
            Info initialInfo = ((Info)odis.readObject());

            //input loop on new thread
            Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                inputLoop();
            }
            });  
            t1.start();
            
            return initialInfo;
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
         
        return null;
    }
    
    
    private void inputLoop(){
        try{
             Info info = null;
            //read in input
            
            while(true){
                info = (Info)odis.readObject();
                if(info.exit){
                    break;
                }
                lock.lock();
                try{
                    infos.add(info);
                }finally{
                    lock.unlock();
                }
                
                
            }
            
            
            
            // closing resources 
            odis.close(); 
            odos.close(); 
            
        }catch (Exception e){ 
            e.printStackTrace(); 
        } 
        
    }

}
