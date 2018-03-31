package Database;

import java.sql.SQLException;
import java.util.ArrayList;

public class IndexerDataManager extends DatabaseManager {
    public IndexerDataManager() throws SQLException, ClassNotFoundException {
        super();
    }

    private void addStemmedWords(ArrayList<String> stems) {
        String[] queries = new String[stems.size()];
        for (int i = 0; i < queries.length; i++)
        {
            long id = (long) executeQuery("select nextval('stemmed_word_id_seq') as id", "id");
            queries[i] = "insert into stemmed_word values( " + id + ", \'" + stems.get(i) + "\') on conflict(word) do nothing;";
        }
        executeBatch(queries);
    }

    private void addOriginalWords(ArrayList<String> originals) {
        String[] queries = new String[originals.size()];
        for (int i = 0; i < queries.length; i++) {
            long id = (long) executeQuery("select nextval('original_word_id_seq') as id", "id");
            queries[i] = "insert into original_word values( " + id + ", \'" + originals.get(i) + "\') on conflict(word) do nothing;";
        }
        executeBatch(queries);
    }

    public void addIndex(ArrayList<String> stemmedWords, ArrayList<String> originalWords, String url, ArrayList<Integer> positions) {
        addStemmedWords(stemmedWords);
        addOriginalWords(originalWords);
        String[] queries = new String[stemmedWords.size()];
        for (int i = 0; i < stemmedWords.size(); i++) {
            long id = (long) executeQuery("select nextval('inverted_index_id_seq') as id", "id");
            queries[i] = "INSERT INTO inverted_index(id, stemmed_id, original_id, url_id, position) VALUES (" + id + ",(SELECT id FROM stemmed_word WHERE word='" + stemmedWords.get(i) + "'),(SELECT id FROM original_word WHERE word='" + originalWords.get(i) + "'),(SELECT id FROM visited_links WHERE visited_links.url='" + url + "')," + positions.get(i) + ");";
        }
        executeBatch(queries);
    }

    public void deleteDocument(String url){
        executeUpdate("DELETE FROM inverted_index WHERE url_id=(SELECT id FROM visited_links WHERE visited_links.url='"+ url +"');");
    }
}
