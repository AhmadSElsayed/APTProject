package Crawler;

import Database.CrawlerDataManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Spider {
    private SpiderLeg[] legs;
    private DocumentMaker[] makers;

    public Spider(int legCount, int makerCount, int maxDocumentCount, String ip, int port, int crawlerPriority, int makerPriority, String[] seedSet, boolean sitemap) throws IOException, SQLException, ClassNotFoundException {
        CrawlerDataManager crawlerDataManager = new CrawlerDataManager(sitemap);
        crawlerDataManager.addLinksToSeedSet(seedSet);
        crawlerDataManager.closeConnection();
        BlockingQueue<String> documentQueue = new LinkedBlockingQueue<>();
        this.legs = new SpiderLeg[legCount];
        for(int i = 0; i < legs.length; i++){
            legs[i] = new SpiderLeg(documentQueue, maxDocumentCount, crawlerPriority, sitemap);
        }
        this.makers = new DocumentMaker[makerCount];
        for(int i =0; i < makers.length; i++){
            makers[i] = new DocumentMaker(documentQueue, maxDocumentCount, ip, port, makerPriority, sitemap);
        }
    }

    public void start(){
        for (SpiderLeg leg : legs) {
            leg.start();
        }
        for (DocumentMaker maker : makers) {
            maker.start();
        }

    }
}
