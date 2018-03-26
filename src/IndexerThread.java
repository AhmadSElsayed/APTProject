import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.*;
public class IndexerThread extends Thread{

    private final Socket clientSocket;
    private final Indexer myIndexer;
    private AtomicInteger i;

    IndexerThread(Socket clientSocket, Indexer myIndexer, AtomicInteger i){
        this.clientSocket=clientSocket;
        this.myIndexer = myIndexer;
        this.i = i;
    }


  @Override
   public void run() {
        try {
            // From socket
            Scanner sc = new Scanner(clientSocket.getInputStream());
            while(sc.hasNextLine()) {
                //int docNumber = sc.nextInt();
                String url= sc.nextLine();
                String doc = sc.nextLine();
                synchronized (myIndexer) {
                    myIndexer.indexDoc(doc, i.getAndIncrement());
                }
              System.out.println(url+ " " + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


  }





}
