package Ranker;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class Ranker extends Thread {
    private ServerSocket serverSocket;
    private int rankerCount;

    public Ranker(int rankerCount, int port, int rankerPriority) throws IOException {
        this.setPriority(rankerPriority);
        serverSocket = new ServerSocket(port);
        this.rankerCount = rankerCount;
    }

    @Override
    public void run() {
        try{
            for(int i = 0; i < rankerCount; i++)
                new RankerThread(serverSocket.accept());
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
