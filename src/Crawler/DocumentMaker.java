package Crawler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentMaker extends Thread {

    private AtomicInteger documentCount;
    private BlockingQueue<String> queue;
    private String location;
    private int maxDocumentCount;

    public DocumentMaker(BlockingQueue<String> q, String loc, AtomicInteger documentCount, int maxDocumentCount, int priority) {
        this.setPriority(priority);
        location = loc;
        queue = q;
        this.documentCount = documentCount;
        this.maxDocumentCount = maxDocumentCount;
    }

    @Override
    public void run() {
        while (documentCount.get() < maxDocumentCount) {
            try {
                String s = queue.poll(10, TimeUnit.SECONDS);
                if (s == null || s.isEmpty())
                    continue;
                try (PrintWriter out = new PrintWriter(location + "Document[" + Integer.toString(documentCount.getAndIncrement()) + "].html")) {
                    out.println(s);
                }
                if(this.isInterrupted())
                    throw new InterruptedException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                try {
                    sleep(Long.MAX_VALUE);
                } catch (InterruptedException e1) {
                    System.out.println("Error: DocumentMaker Interrupted during Interruption");
                    e1.printStackTrace();
                }
            }
        }
    }
}