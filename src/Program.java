import Crawler.DocumentMaker;
import Crawler.SpiderLeg;
import Crawler.StateSaver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//TODO: Remove Hashes from links
//TODO: SiteMap -> Abzo
//TODO: Robot.txt -> Abzo
//TODO: Save State -- Sleeps & Not in Clean File
//TODO: Save Frequency to save iterations -- No save
//TODO: Package

public class Program  {
    public static void main(String[] args) {

        int maxDocumentCount = 50;
        AtomicInteger crawlerDocumentCount = new AtomicInteger(0);
        AtomicInteger docMakerDocumentCount = new AtomicInteger(0);
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(maxDocumentCount);
        BlockingQueue<String> docQueue = new ArrayBlockingQueue<>(maxDocumentCount);
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        queue.addAll(Arrays.asList("https://en.wikipedia.org/wiki/Computer_science", "https://www.gcflearnfree.org/computerbasics/what-is-a-computer/1/", "https://www.quora.com/What-are-different-fields-in-computer-science", "http://aihorizon.com/essays/basiccs/general/cs_areas.html"));

        ArrayList<SpiderLeg> spiderLegs = new ArrayList<>();
        ArrayList<DocumentMaker> documentMakers = new ArrayList<>();

        for(int i=0; i < 6; i++){
            spiderLegs.add(new SpiderLeg(queue, map, docQueue, crawlerDocumentCount, maxDocumentCount));
            spiderLegs.get(i).start();
        }
        for(int i=0; i < 4; i++){
            documentMakers.add(new DocumentMaker(docQueue,"/home/ahmad/Desktop/Documents/", docMakerDocumentCount,maxDocumentCount, 4));
            documentMakers.get(i).start();
        }

        StateSaver x = new StateSaver(spiderLegs, documentMakers, queue, map, docQueue, crawlerDocumentCount, docMakerDocumentCount, maxDocumentCount);
        x.start();
        try {
            for(SpiderLeg s : spiderLegs){
                s.join();
            }
            for(DocumentMaker d : documentMakers){
                d.join();
            }
            x.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
