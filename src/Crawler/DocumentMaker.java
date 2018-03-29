package Crawler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentMaker extends Thread {

    private AtomicInteger documentCount;
    private BlockingQueue<String> queue;
    private int maxDocumentCount;
    private Socket indexerSocket;
    private PrintWriter outputStream;

    public DocumentMaker(BlockingQueue<String> q, String IP, AtomicInteger documentCount, int maxDocumentCount, int priority) {
        this.setPriority(priority);
        queue = q;
        this.documentCount = documentCount;
        this.maxDocumentCount = maxDocumentCount;
        try {
            indexerSocket = new Socket(IP, 6666);
            outputStream = new PrintWriter(indexerSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (documentCount.get() < maxDocumentCount) {
            try {
                String URLandDocument = queue.poll(10, TimeUnit.SECONDS);
                if (URLandDocument == null || URLandDocument.isEmpty())
                    continue;
                String[] strings = URLandDocument.split("!");
                outputStream.println(strings[0]);
                outputStream.flush();
                outputStream.println(strings[1]);
                outputStream.flush();
                System.out.println("Document Is: " + documentCount.getAndIncrement());
                if(this.isInterrupted())
                    throw new InterruptedException();
            } catch (InterruptedException e) {
                try {
                    sleep(Long.MAX_VALUE);
                } catch (InterruptedException e1) {
                }
            }
        }
        try {
            indexerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}