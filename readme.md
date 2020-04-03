# Welcome to YU-GAMES!!!!

A joint project by Yehuda Goldfeder, Daniel Schaffel, and Noam Annenberg.

## Building and running the application
### If you are using Java 8/have Java FX already built in
#### To build:
> mvn compile

#### To run client:
> java -cp target/classes corona.games.client.Client localhost 80

#### To run server:
> java -cp target/classes corona.games.server.Server 80

### Otherwise you need to build a Jar file for the client
#### To Build:
> mvn clean compile assembly:single

#### To run client:
> java -jar target/YU-Games-1.0-SNAPSHOT-jar-with-dependencies.jar localhost 80

#### To run server:
> java -cp target/classes corona.games.server.Server 80


## Client Protocal

### Message Types

    - GARBAGE
    - INIT_CLIENT,CHAT_MSG
    - SHUT_DOWN
    - EXIT_PHASE
    - CREATE_GAME
    - JOIN_GAME
    - REQUEST_CHAT_LIST
    - CHAT LIST
    - REQUEST_GAME_LIST
    - GAME_LIST
    - START_GAME
    - GAMEPLAY_INFO
    - CONFIRM_READY
    - UNCONFIRM_READY
    - PLAYER_HAS_JOINED
    - PLAYER_HAS_LEFT

### Initial Handshake
Client sends Message of type INIT_CLIENT. contains username, and -1 for ID
Client waits for message back of tpye INIT_CLIENT containing its ID

### Phase 1


#### Client can recieve:
    - CHAT_MSG - post this msg to chat
    - SHUT_DOWN - shut this client down
    - GAME_LIST - a list of every game looking for players. The list is stored in the msg field
    - CHAT_LIST - a list of chat history. How inclusive it is is up to the server

#### Client can send:
    - 
    - SHUT_DOWN - tells the server we are shutting down
    - CHAT_MSG - we are writing to the chat 
    - CREATE_GAME - we are creating a new game, with this games info in the msg. This takes us to Phase 2. The created game is added to game list for other clients to see
    - JOIN_GAME - join a game from the list. This takes us to Phase 2
    - REQUEST_GAME_LIST - ask server for a list of games looking for players
    - REQUEST_CHAT_LIST - asks server for chat history

### Phase 2

#### Client can receive:
    - CHAT_MSG  - post this msg to chat
    - CONFIRM_READY - a different player is ready. Includes ClientID (As does every msg)
    - UNCONFIRM_READY - a player is not ready
    - PLAYER_HAS_JOINED - player has joined room
    - PLAYER_HAS_LEFT - player has left room
    - START_GAME - Go to Phase 3 
    - SHUT_DOWN 
    - CHAT_LIST - a list of chat history. How inclusive it is is up to the server

#### Client can send:
    - EXIT_PHASE - tells the server we are going back to Phase 1
    - CHAT_MSG - we are writing to the chat 
    - SHUT_DOWN - tells server we are shutting down
    - START_GAME - only valid if you are host, otherwise server will reject. Starts the game. Go to Phase 3
    - CONFIRM_READY - all players in room must do this for START_GAME to work
    - UNCONFIRM_READY - if you don't know what this means, WTH
    - REQUEST_CHAT_LIST - asks server for chat history

### Phase 3

#### Client can receive:
    - EXIT_PHASE - go back to Phase 2
    - CHAT_LIST - a list of chat history. How inclusive it is is up to the server
    - SHUT_DOWN 
    - CHAT_MSG
    - GAMEPLAY_INFO,

#### Client can send:
    - EXIT_PHASE 
    - REQUEST_CHAT_LIST 
    - SHUT_DOWN 
    - CHAT_MSG
    - GAMEPLAY_INFO

GAMEPLAY_INFO is broadcasted to all clients and the msg string is written to std::out of the Game plugin






