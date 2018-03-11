package Crawler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SpiderLeg extends Thread{

    private ConcurrentHashMap<String, Integer> urlMap;
    private BlockingQueue<String> urlQueue;
    private BlockingQueue<String> documentQueue;
    private AtomicInteger documentCount;
	private int maxDocumentCount;


	public SpiderLeg(BlockingQueue<String> urlQueue, ConcurrentHashMap<String, Integer> urlMap, BlockingQueue<String> documentQueue, AtomicInteger documentCount, int maxDocumentCount){
		this.urlQueue = urlQueue;
		this.urlMap = urlMap;
		this.documentQueue = documentQueue;
		this.documentCount = documentCount;
		this.maxDocumentCount = maxDocumentCount;
	}

	@Override
	public void run() {
        String currentURL;
        HashSet<String> newURLList;
        while (documentCount.get() < maxDocumentCount) {
            try {
                currentURL = urlQueue.take();
                Document doc = getDocument(currentURL);
                if (doc == null || doc.text().isEmpty())
                    continue;

                // TODO: Fix
                // TODO: Doesn't handle interrupted twice
                try {
                    documentQueue.put(currentURL + "||" + doc.text());
                } catch (InterruptedException e) {
                    documentQueue.put(currentURL + "||" + doc.text());
                    throw e;
                }

                documentCount.getAndIncrement();
                Elements newURLs = doc.select("a[href]");
                newURLList = toStringSet(newURLs);
                for (String url : newURLList){
                    if(urlMap.merge(url, 1, Integer::sum) == 1)
                        urlQueue.add(url);
                }
                if(this.isInterrupted())
                    throw new InterruptedException();
            } catch (InterruptedException e) {
                try {
                    sleep(Long.MAX_VALUE);
                } catch (InterruptedException e1) {
                    System.out.println("Error: SpiderLeg Interrupted during Interruption");
                    e1.printStackTrace();
                }
            }catch(IllegalStateException e){
                //e.printStackTrace();
                //System.out.println("Shared Queue might be full");
            }
        }
    }

    private HashSet<String> toStringSet(Elements elements) {
        HashSet<String> list = new HashSet<>();
        String temp;
        for (Element e : elements){
            temp = e.attr("abs:href");
            if(temp.startsWith("http")) {
                list.add(temp);
            }
        }
        return list;
    }

    private Document getDocument(String url) {
        try {
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .maxBodySize(Integer.MAX_VALUE);
            Document doc = connection.get();
            if(connection.response().statusCode() == 200){
                System.out.println("Success: " + url);
                return doc;
            }
        }catch (IOException e){
            System.out.println("Error: " + url);
            //e.printStackTrace();
        }
        return null;
    }
}
