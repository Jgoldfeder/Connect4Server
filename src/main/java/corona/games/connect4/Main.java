package corona.games.connect4;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*; 
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.geometry.Bounds;
import java.net.*; 
import java.io.*; 
import java.util.concurrent.locks.ReentrantLock;
public class Main extends Application 
{
        
        private Client client;
        private Image[] sprites;
        private Group root;
        private GraphicsContext gc;
        private Scene theScene;
        private int[][] board = new int[7][6];
        private static  String ip_name = "localhost";
        private static int port = 80;
        private int ID = -1;
        private int color= -1;
    
    public static void main(String[] args) {
        if(args.length>0){
            ip_name = args[0];
        }
        if(args.length>1){
            port = Integer.parseInt(args[1]);
        }
 
        launch(args);
    }
    
    private ReentrantLock lock = new ReentrantLock();

    public void start(Stage theStage){
        
        
        
        theStage.setTitle( "Connect 4" );
        
        sprites = Util.loadImages();
        
        root = new Group();
        theScene = new Scene( root );
        theStage.setScene( theScene );
             
        Canvas canvas = new Canvas(800, 500);
        root.getChildren().add( canvas );
             
        gc = canvas.getGraphicsContext2D();
                          
        gc.drawImage(sprites[0],0,0); 
        
        
        theStage.show();
        
        //register mouse listener
        theScene.setOnMouseClicked(event -> click(event));

        
        new AnimationTimer(){
            public void handle(long currentNanoTime)
            {
                gameLoop();
            }
        }.start();
        
        //connect to server     
        client = new Client(ip_name,port);
        Info info = client.connect();
        color = info.color;
        ID = info.ID;
    }
    
    private void drawBoard(){
        for(int i = 0;i<board.length;i++){
            for(int j = 0;j<board[0].length;j++){
                if(board[i][j]==Util.RED){
                    gc.drawImage(sprites[Util.RED],170+i*70,48+j*70);
                }
                if(board[i][j]==Util.YELLOW){
                    gc.drawImage(sprites[Util.YELLOW],170+i*70,48+j*70);
                }                
            }
        }
    }
    
    private void gameLoop(){
        ArrayList<Info> infos = client.getInfo();
        for(Info i : infos){
            if(i.ID==ID){
                continue;
            }
            if(i.move){
                board[i.x][i.y] = i.color;
            }
        }
        drawBoard();

    }
    
    private void click(MouseEvent e){
        System.out.println((int)e.getX()+":"+(int)e.getY());
        int xOff = 164;
        int yOff = 40;
        int xUnit = 70;
        int yUnit = 70;
        int x = ((int)e.getX())-xOff;
        int y = ((int)e.getY())-yOff;
                        System.out.println(y+":"+y);
        if(x<0) return;
        x = x / xUnit;
        y = y / yUnit;
                System.out.println(y+":"+y);

        if(x>=0&&x<board.length){
            y = getLowestRow(x);
            if(y!=-1){
                board[x][y] = color;
                Info info = new Info();
                info.color = color;
                info.x=x;
                info.y=y;
                info.move=true;
                info.ID = ID;
                client.sendInfo(info);
            }
        }
    }

    private int getLowestRow(int col){
        int y;
        for(y = board[0].length-1;y>=0;y--){
            if(board[col][y]==0) break;
        }
        return y;
    }
    
    
}
