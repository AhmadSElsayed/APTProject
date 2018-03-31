package Database;

import java.sql.SQLException;

public class CrawlerDataManager extends DatabaseManager{
    public CrawlerDataManager() throws SQLException, ClassNotFoundException {
        super();
    }

    public long getDocumentCount() {
        return (long) executeQuery("select count(*) as c from visited_links;", "c");
    }

    public String getNextURL() {
        return (String) executeQuery("DELETE FROM seed_set WHERE id IN (SELECT id FROM seed_set ORDER BY id asc LIMIT 1) returning url;", "url");
    }

    public void addLinksToSeedSet(String[] urls) {
        String[] queries = new String[urls.length];
        for (int i = 0; i < queries.length; i++) {
            long id = (long) executeQuery("select nextval('seed_set_id_seq') as id", "id");
            queries[i] = "insert into seed_set values( "+ id +", \'" + urls[i] + "\') on conflict(url) do nothing;";
        }
    executeBatch(queries);
}

    public void visitURL(String currentURL) {
        long id = (long) executeQuery("select nextval('visited_links_id_seq') as id", "id");
        executeUpdate("insert into visited_links values("+ id + ", \'" + currentURL + "\') on conflict(url) do nothing;");
    }
}
