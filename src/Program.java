import Crawler.DocumentMaker;
import Crawler.SpiderLeg;
import Crawler.StateSaver;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Program  {
    public static void main(String[] args) {

        long t = System.currentTimeMillis();

        int maxDocumentCount = 5000;
        AtomicInteger crawlerDocumentCount = new AtomicInteger(0);
        AtomicInteger docMakerDocumentCount = new AtomicInteger(0);
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(maxDocumentCount);
        BlockingQueue<String> docQueue = new ArrayBlockingQueue<>(maxDocumentCount);
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        queue.addAll(Arrays.asList("https://en.wikipedia.org/wiki/Computer_science", "https://www.gcflearnfree.org/computerbasics/what-is-a-computer/1", "https://www.quora.com/What-are-different-fields-in-computer-science", "http://aihorizon.com/essays/basiccs/general/cs_areas.html"));
        //queue.add("https://yts.am/");
        ArrayList<SpiderLeg> spiderLegs = new ArrayList<>();
        ArrayList<DocumentMaker> documentMakers = new ArrayList<>();

        for(int i=0; i < 6; i++){
            spiderLegs.add(new SpiderLeg(queue, map, docQueue, crawlerDocumentCount, maxDocumentCount));
            spiderLegs.get(i).start();
        }
        for(int i=0; i < 4; i++){
            documentMakers.add(new DocumentMaker(docQueue,"172.28.178.39", docMakerDocumentCount,maxDocumentCount, 4));
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
        try {

            try (PrintWriter out = new PrintWriter("Map.txt")) {
                Map <String, Integer> l = sortByValue(map);
                for (Map.Entry<String,Integer> m : l.entrySet())
                    out.println(m.getKey() + "=" + m.getValue());
            }
        }catch (Exception e){

        }
        System.out.println(System.currentTimeMillis() - t);
    }
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(ConcurrentHashMap<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
