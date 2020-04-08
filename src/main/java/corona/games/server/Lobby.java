package corona.games.server;
import java.util.*;
import java.util.concurrent.*;
import corona.games.communication.Message;
import corona.games.communication.GameInfo;
import corona.games.communication.Message.MessageType;
import com.google.gson.Gson;
public class Lobby{
    
    private ConcurrentHashMap<UUID,ClientHandler> clientList;
    private Server server;
    private ConcurrentHashMap<GameInfo,Boolean> gameList;
    
    Lobby(Server server){
        this.server = server;
        clientList = new ConcurrentHashMap<>();
        gameList = new ConcurrentHashMap<>();

    }
    
    void addClient(ClientHandler cl){
        clientList.put(cl.getID(),cl);
        ProcessIncomingMessages p = new ProcessIncomingMessages(cl,this);
        server.run(p);
    }    
    
    void addGame(GameInfo info){
        gameList.put(info,true);
    }
    
    ConcurrentHashMap<UUID,ClientHandler> getClientList(){
        return clientList;
    }
    
        
    ConcurrentHashMap<GameInfo,Boolean> getGameList(){
        return gameList;
    }
    
}

class ProcessIncomingMessages implements Runnable{
    private ClientHandler client;
    private boolean shutdown;
    private Lobby lobby;
    ProcessIncomingMessages(ClientHandler client,Lobby lobby){
        this.client = client;
        this.lobby =  lobby;
        shutdown = false;
    }
    
    public void shutdown(){
        shutdown = true;
    }
    
    public void run(){
        Gson gson = null;
        
        System.out.println("Entering danger Zone");
        try{
            //gson = new Gson();
        }catch(Exception e){                               
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        System.out.println("leaving Danger Zone");


        while(!shutdown){
            Message m = client.readMessage(1000);
            if(m==null) continue;
            switch(m.getMessageType()){
                case CHAT_MSG:
                    // broadcast

                    lobby.getClientList().forEachValue(60,(elem)-> elem.writeMessage(m));
                    break;
                case SHUT_DOWN:
                    shutdown();
                    lobby.getClientList().remove(client.getID());
                    client.shutdown();
                    break;
                case CREATE_GAME:
                    //String jsonReceived = m.getMessage();
                    //GameInfo info = gson.fromJson(jsonReceived, GameInfo.class);
                    //lobby.addGame(info);
                    break;
                case REQUEST_GAME_LIST:
                    //GameInfo[] infos = (GameInfo[])lobby.getGameList().keySet().toArray();
                    //String jsonToSend = gson.toJson(infos);
                    //Message response = new Message(MessageType.GAME_LIST ,jsonToSend,null,"server");
                    break;
                default:
                    continue;
            }
        }
    }
    
    
}