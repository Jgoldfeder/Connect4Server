package corona.games.server;
import java.util.*;
import java.util.concurrent.*;
import corona.games.communication.Message;
import corona.games.communication.Message.MessageType;

public class Lobby{
    
    ConcurrentHashMap<UUID,ClientHandler> clients;
    private Server server;
    
    Lobby(Server server){
        this.server = server;
        clients = new ConcurrentHashMap<>();
    }
    
    void addClient(ClientHandler cl){
        clients.put(cl.getID(),cl);
        ProcessIncomingMessages p = new ProcessIncomingMessages(cl,this);
        server.run(p);
    }    
    
}

class ProcessIncomingMessages implements Runnable{
    private ClientHandler client;
    private boolean shutDown;
    private Lobby lobby;
    ProcessIncomingMessages(ClientHandler client,Lobby lobby){
        this.client = client;
        this.lobby =  lobby;
        shutDown = false;
    }
    
    public void shutDown(){
        shutDown = true;
    }
    
    public void run(){
        while(!shutDown){
            Message m = client.readMessage(1000);
            if(m==null) continue;
            switch(m.getMessageType()){
                case CHAT_MSG:
                    // broadcast
                    lobby.clients.forEachValue(60,(elem)-> elem.writeMessage(m));
                    break;
                
            }
        }
    }
    
    
}