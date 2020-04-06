package corona.games.client.view;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import javafx.application.Application;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatBoxTest extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        // TODO Auto-generated method stub
        ChatBox bc = new ChatBox(new LinkedBlockingDeque<>(), new LinkedBlockingDeque<>(), new TextArea(), UUID.randomUUID(), "daniel");
        bc.display();
    }
    
public static void main(String[] args) {
    launch(ChatBoxTest.class);
}
}