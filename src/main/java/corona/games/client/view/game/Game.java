package corona.games.client.view.game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by noamannenberg
 * on 4/6/20.
 */
public class Game implements Runnable {
    String path;
    public Game(String path) {
        this.path = path;
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p1 = runtime.exec(this.path);
            Process p2 = runtime.exec(this.path);
            ServerSocket ss = new ServerSocket(8000);
            Socket socket1 = ss.accept();
            InputStream in1 = socket1.getInputStream();
            OutputStream out1 = socket1.getOutputStream();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
            BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(out1));

            Socket socket2 = ss.accept();
            InputStream in2 = socket2.getInputStream();
            OutputStream out2 = socket2.getOutputStream();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(in2));
            BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(out2));

            new Thread(() -> {
                try {
                    while (true) {
                        String line1 = reader1.readLine();
                        writer2.write(line1);
                        writer2.flush();
                    }
                } catch (IOException e) {
                }
            }).start();

            new Thread(() -> {
                try {
                    while (true) {
                        String line2 = reader2.readLine();
                        writer1.write(line2);
                        writer1.flush();
                    }
                } catch (IOException e) {
                }
            }).start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
