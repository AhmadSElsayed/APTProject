package Indexer;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Indexer extends Thread{

    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private int indexerCount;
    private boolean update;

    public Indexer(int indexerCount, int port, boolean update) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(indexerCount);
        this.indexerCount = indexerCount;
        this.update = update;
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i < indexerCount; i++)
                this.threadPool.execute(new IndexerThread(serverSocket.accept(), update));
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
