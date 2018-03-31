package Crawler;

import Database.CrawlerDataManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

class SpiderLeg extends Thread{

    private BlockingQueue<String> documentQueue;
	private long maxDocumentCount;
	private CrawlerDataManager databaseManager;

	SpiderLeg(BlockingQueue<String> documentQueue, long maxDocumentCount, int priority) throws SQLException, ClassNotFoundException {
		this.documentQueue = documentQueue;
		this.maxDocumentCount = maxDocumentCount;
		this.databaseManager = new CrawlerDataManager();
		this.setPriority(priority);
	}

	@Override
    public void run() {
        String currentURL;
        try {
            while (databaseManager.getDocumentCount() < maxDocumentCount) {
                // Get Next URL to be crawled
                currentURL = databaseManager.getNextURL();
                if(currentURL == null)
                    continue;
                // Get Document from the internet
                Document doc = getDocument(currentURL);
                // If the doc is invalid or empty skip the rest of this cycle
                if (doc == null || doc.text().isEmpty())
                    continue;
                // Insert url in visited table
                databaseManager.visitURL(currentURL);
                // Mark The document to be sent to the indexer
                documentQueue.put(currentURL + "!" + doc.text().replaceAll("[^a-zA-Z0-9 ]", " "));
                // Extract Links from the html document and send to the Database for other crawlers
                databaseManager.addLinksToSeedSet(extractLinkList(doc));
            }
            databaseManager.closeConnection();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String[] extractLinkList(Document document) {
        // Get Link Elements from the document
        Elements elements = document.select("a[href]");
        // A List for extracted links
        HashSet<String> list = new HashSet<>();
        // Temp variable to check that links start with http
        String temp;
        // Add Extracted Links to list
        for (Element e : elements){
            temp = e.attr("abs:href");
            if(temp.startsWith("http")) {
                list.add(temp);
            }
        }
        return list.toArray(new String[0]);
    }

    private Document getDocument(String url) {
        try {
            // Define JSoup Connection to crawl with
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(12000)
                    .followRedirects(true)
                    .maxBodySize(Integer.MAX_VALUE);
            // Download document
            Document document = connection.get();
            // Check successful download
            if(connection.response().statusCode() == 200){
                System.out.println("Success: " + url);
                return document;
            }
        }catch (Exception e){
            System.out.println("Error: " + url);
        }
        return null;
    }
}