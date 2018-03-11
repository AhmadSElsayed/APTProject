package Crawler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StateSaver extends Thread {
    private final ArrayList<DocumentMaker> documentMakers;
    private ArrayList<SpiderLeg> spiderLegs;
    private ConcurrentHashMap<String, Integer> urlMap;
    private BlockingQueue<String> urlQueue;
    private BlockingQueue<String> documentQueue;
    private AtomicInteger crawlerDocumentCount;
    private AtomicInteger makerDocumentCount;
    private int maxDocumentCount;

    public StateSaver(ArrayList<SpiderLeg> spiderLegs, ArrayList<DocumentMaker> documentMakers, BlockingQueue<String> urlQueue, ConcurrentHashMap<String, Integer> urlMap, BlockingQueue<String> documentQueue, AtomicInteger crawlerDocumentCount, AtomicInteger makerDocumentCount, int maxDocumentCount) {
        this.urlQueue = urlQueue;
        this.urlMap = urlMap;
        this.documentQueue = documentQueue;
        this.crawlerDocumentCount = crawlerDocumentCount;
        this.makerDocumentCount = makerDocumentCount;
        this.maxDocumentCount = maxDocumentCount;
        this.spiderLegs = spiderLegs;
        this.documentMakers = documentMakers;
    }

    @Override
    public void run() {
        while (crawlerDocumentCount.get() < maxDocumentCount) {
            //TODO: Timeout Wrong
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (SpiderLeg s : spiderLegs) {
                s.interrupt();
            }
            for (DocumentMaker d : documentMakers) {
                d.interrupt();
            }
            try {
                //TODO: FIX
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                try (PrintWriter out = new PrintWriter("State.txt")) {
                    out.println(crawlerDocumentCount.get());
                    out.println(makerDocumentCount.get());
                    out.println(maxDocumentCount);
                    for (String s : documentQueue) {
                        out.print(s + "()");
                    }
                    out.println();
                    for (String s : urlQueue) {
                        out.print(s + " ");
                    }
                    out.println();
                    for (Map.Entry<String, Integer> s : urlMap.entrySet()) {
                        out.print(s.getKey() + "|" + s.getValue() + " ");
                    }
                    out.println();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (SpiderLeg s : spiderLegs) {
                s.interrupt();
            }
            for (DocumentMaker d : documentMakers) {
                d.interrupt();
            }
        }
    }
}