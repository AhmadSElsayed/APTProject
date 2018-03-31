import Crawler.Spider;
import Indexer.Indexer;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        String[] seedSet = new String[] {
            "https://yts.am",
            "https://en.wikipedia.org/wiki/Computer_science",
            "https://www.gcflearnfree.org/computerbasics/what-is-a-computer/1",
            "https://www.quora.com/What-are-different-fields-in-computer-science",
            "http://aihorizon.com/essays/basiccs/general/cs_areas.html"
        };
        Indexer i = new Indexer(2, 6666, false);
        Spider s = new Spider(3, 2, 50, "localhost", 6666, 6, 4, seedSet);
        i.start();
        s.start();
    }
}
