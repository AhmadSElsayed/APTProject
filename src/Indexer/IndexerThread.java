package Indexer;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

public class IndexerThread extends Thread{

    private final Socket clientSocket;
    private IndexerLogic indexerLogic;
    private boolean update;

    IndexerThread(Socket clientSocket, boolean update) throws SQLException, ClassNotFoundException {
        this.clientSocket=clientSocket;
        this.indexerLogic = new IndexerLogic();
        this.update = update;
    }


  @Override
   public void run() {
        try {
            // From socket
            Scanner sc = new Scanner(clientSocket.getInputStream());
            while(sc.hasNext()) {
                String url = sc.nextLine();
                if (url.equals("DONE"))
                    break;
                String doc = sc.nextLine();
                if(update)
                    indexerLogic.deleteDocument(url);
                indexerLogic.indexDocument(doc, url);
            }
            indexerLogic.closeDatabaseConnection();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
  }
}
