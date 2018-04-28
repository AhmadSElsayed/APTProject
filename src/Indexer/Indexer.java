package Indexer;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class Indexer extends Thread{

    private ServerSocket serverSocket;
    private int indexerCount;
    private boolean update;

    public Indexer(int indexerCount, int port, boolean update, int indexerPriority) throws IOException {
        this.setPriority(indexerPriority);
        serverSocket = new ServerSocket(port);
        this.indexerCount = indexerCount;
        this.update = update;
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < indexerCount; i++)
                new IndexerThread(serverSocket.accept(), update).start();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
