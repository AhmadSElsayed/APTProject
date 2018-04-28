package Crawler;

import Database.CrawlerDataManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

class DocumentMaker extends Thread {

    private BlockingQueue<String> documentQueue;
    private long maxDocumentCount;
    private Socket indexerSocket;
    private PrintWriter outputStream;
    private CrawlerDataManager databaseManager;

    DocumentMaker(BlockingQueue<String> documentQueue, long maxDocumentCount, String IP, int port, int priority, boolean sitemap) throws IOException, SQLException, ClassNotFoundException {
        this.setPriority(priority);
        this.documentQueue = documentQueue;
        this.maxDocumentCount = maxDocumentCount;
        databaseManager = new CrawlerDataManager(sitemap);
        indexerSocket = new Socket(IP, port);
        outputStream = new PrintWriter(indexerSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (databaseManager.getDocumentCount() < maxDocumentCount || !documentQueue.isEmpty()) {
                // Extract marked data to send to an indexer
                String URLandDocument = documentQueue.poll(10, TimeUnit.SECONDS);
                if (URLandDocument == null)
                    continue;
                // Extract link and document
                String[] strings = URLandDocument.split("!");
                // Send Link
                outputStream.println(strings[0]);
                outputStream.flush();
                // Send Document
                outputStream.println(strings[1]);
                outputStream.flush();
            }
            outputStream.println("DONE");
            outputStream.flush();
            outputStream.close();
            indexerSocket.close();
            databaseManager.closeConnection();
        } catch (SQLException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Maker Finished");

    }
}