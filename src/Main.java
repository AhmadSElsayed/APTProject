import Crawler.Spider;
import Database.CrawlerDataManager;
import Indexer.Indexer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    private static int indexerCount;
    private static int crawlerCount;
    private static int maxDocumentCount;
    private static int port;
    private static String indexerIP;
    private static boolean update;
    private static int crawlerPriority;
    private static int makerPriority;
    private static int indexerPriority;
    private static boolean sitemap;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
        parseArgs(args);
        String[] seedSet = new String[] {
            "https://yts.am",
            "https://en.wikipedia.org/wiki/Computer_science",
            "https://en.wikipedia.org/api/",
            "https://www.gcflearnfree.org/computerbasics/what-is-a-computer/1",
            "https://www.quora.com/What-are-different-fields-in-computer-science",
            "http://aihorizon.com/essays/basiccs/general/cs_areas.html"
        };

        CrawlerDataManager crawlerDataManager = new CrawlerDataManager(sitemap);
        if(crawlerDataManager.getDocumentCount() >= maxDocumentCount) {
            System.out.println("Already Crawled");
            return;
        }
        crawlerDataManager.closeConnection();
        Indexer i = new Indexer(indexerCount, port, update, indexerPriority);
        Spider s = new Spider(crawlerCount, indexerCount, maxDocumentCount, indexerIP, port, crawlerPriority, makerPriority, seedSet, sitemap);
        i.start();
        s.start();
    }

    private static void parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("i", "indexerCount",true,"Indexer Thread Count");
        options.addOption("c", "crawlerCount",true,"Crawler Thread Count");
        options.addOption("m", "maxDocumentCount",true,"Maximum Document Count to be crawled");
        options.addOption("p", "port",true,"Communication port number");
        options.addOption("ip", "indexerIP",true,"Communication IP");
        options.addOption("cpr", "crawlerPriority",true,"Crawler Thread Priority");
        options.addOption("mpr", "makerPriority",true,"Document Maker Thread Priority");
        options.addOption("ipr", "indexerPriority",true,"Indexer Thread Priority");
        options.addOption("u", "update",false,"Update(Recrawl); this must be given with option -f");
        options.addOption("f", "file",true,"file location");
        options.addOption("a", "autoconfigure",false,"AutoCofiguration");
        options.addOption("s", "sitemap",false,"SiteMap");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        int cores = Runtime.getRuntime().availableProcessors();
        indexerCount = cores * 4;
        crawlerCount = cores * 3;
        maxDocumentCount = 50;
        port = 6666;
        indexerIP = "localhost";
        update = false;
        crawlerPriority = 6;
        makerPriority = 4;
        indexerPriority = 10;

        sitemap = cmd.hasOption("s");
        if(cmd.hasOption("i"))
            indexerCount = Integer.parseInt(cmd.getOptionValue("i"));
        if(cmd.hasOption("c"))
            crawlerCount = Integer.parseInt(cmd.getOptionValue("c"));
        if(cmd.hasOption("m"))
            maxDocumentCount = Integer.parseInt(cmd.getOptionValue("m"));
        if(cmd.hasOption("p"))
            port = Integer.parseInt(cmd.getOptionValue("p"));
        if(cmd.hasOption("ip"))
            indexerIP = cmd.getOptionValue("i");
        if(cmd.hasOption("cpr"))
            crawlerPriority = Integer.parseInt(cmd.getOptionValue("cpr"));
        if(cmd.hasOption("mpr"))
            makerPriority = Integer.parseInt(cmd.getOptionValue("mpr"));
        if(cmd.hasOption("ipr"))
            indexerPriority = Integer.parseInt(cmd.getOptionValue("ipr"));
        if(cmd.hasOption("u"))
            update = true;
    }
}
