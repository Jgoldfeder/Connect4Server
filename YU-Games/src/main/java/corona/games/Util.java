package corona.games;
import javafx.scene.image.Image;
import java.io.File;
class Util{
    
    //constants
    final static int RED = 1;
    final static int YELLOW = 2;
    
    
    static Image[] loadImages(){
        Image[] i = new Image[3];
        String p = new File(System.getProperty("user.dir")).getParent();
        System.out.println(p);
        i[0] = new Image( "background.png");
        i[RED] = new Image( "red.png");
        i[YELLOW] = new Image("yellow.png");
        return i;
    }
    
    
    
    
}