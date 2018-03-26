import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread{

    private ExecutorService threadPool;

    Server() {
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(6666);
            Mongo mongo = new Mongo("localhost", 27017);
            DB db = mongo.getDB("Shazlola");
            DBCollection collection  = db.getCollection( "batot" );

            Indexer myIndexer = new Indexer(mongo, db, collection);
            AtomicInteger i = new AtomicInteger(0);

            while (true) {
                this.threadPool.execute(new IndexerThread(server.accept(), myIndexer, i));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
