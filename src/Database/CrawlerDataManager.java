package Database;

import CrawlerUtilities.CrawlerUtilities;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CrawlerDataManager extends DatabaseManager{
    private boolean sitemap;
    public CrawlerDataManager(boolean sitemap) throws SQLException, ClassNotFoundException, IOException {
        super();
        this.sitemap = sitemap;
    }

    public long getDocumentCount() {
        return (long) executeQuery("select count(*) as c from visited_links;", "c");
    }

    public String getNextURL() {
        return (String) executeQuery("DELETE FROM seed_set WHERE id IN (SELECT id FROM seed_set ORDER BY id asc LIMIT 1) returning url;", "url");
    }

    public void addLinksToSeedSet(String[] urls) {
        if(urls.length == 0)
            return;
        urls = processLinks(urls);
        StringBuilder batchQuery = new StringBuilder();
        batchQuery.append("insert into seed_set values");
        for (String url : urls) {
            long id = (long) executeQuery("select nextval('seed_set_id_seq') as id", "id");
            batchQuery.append("(").append(id).append(", \'").append(url).append("\')").append(",");
        }
        batchQuery.deleteCharAt(batchQuery.length() - 1);
        batchQuery.append("on conflict(url) do nothing;");
        executeUpdate(batchQuery.toString());
    }

    public void visitURL(String currentURL) {
        long id = (long) executeQuery("select nextval('visited_links_id_seq') as id", "id");
        executeUpdate("insert into visited_links values("+ id + ", \'" + currentURL + "\') on conflict(url) do nothing;");
    }

    private String[] processLinks(String[] links) {
        ArrayList<String> urls =new ArrayList<>(Arrays.asList(links));
        ArrayList<String> processedLinks = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++) {
            urls.set(i, CrawlerUtilities.linkCleaner(urls.get(i)));
            if (urls.get(i).contains("'"))
                continue;
            try {
                String regex = (String) executeQuery("select regex from robots where base_url = \'"+ new URL(urls.get(i)).getHost() + "\';","regex");
                if(regex == null) {
                    regex = CrawlerUtilities.getRobotText(urls.get(i));
                    executeUpdate("insert into robots values(\'" + new URL(urls.get(i)).getHost() + "\'," +"\'" + regex + "\') on conflict(base_url) do nothing;");
                    if(sitemap)
                        processedLinks.addAll(CrawlerUtilities.getSiteMap(urls.get(i)));
                }
                if(!Pattern.compile(regex).matcher(urls.get(i)).matches())
                    processedLinks.add(urls.get(i));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return processedLinks.toArray(new String[0]);
    }
}
