package Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class IndexerDataManager extends DatabaseManager {
    public IndexerDataManager() throws SQLException, ClassNotFoundException, IOException {
        super();
    }

    private void addStemmedWords(ArrayList<String> stems) {
        StringBuilder batchQuery = new StringBuilder();
        batchQuery.append("insert into stemmed_word values");
        for (String stem : stems) {
            long id = (long) executeQuery("select nextval('stemmed_word_id_seq') as id", "id");
            batchQuery.append("(").append(id).append(", \'").append(stem).append("\')").append(",");
        }
        batchQuery.deleteCharAt(batchQuery.length() - 1);
        batchQuery.append("on conflict(word) do nothing;");
        executeUpdate(batchQuery.toString());
    }

    private void addOriginalWords(ArrayList<String> originals) {
        StringBuilder batchQuery = new StringBuilder();
        batchQuery.append("insert into original_word values");
        for (String original : originals) {
            long id = (long) executeQuery("select nextval('original_word_id_seq') as id", "id");
            batchQuery.append("(").append(id).append(", \'").append(original).append("\')").append(",");
        }
        batchQuery.deleteCharAt(batchQuery.length() - 1);
        batchQuery.append("on conflict(word) do nothing;");
        executeUpdate(batchQuery.toString());
    }

    public void addIndex(ArrayList<String> stemmedWords, ArrayList<String> originalWords, String url, ArrayList<Integer> positions) {
        addStemmedWords(stemmedWords);
        addOriginalWords(originalWords);
        long urlId = (long) executeQuery("SELECT id FROM visited_links WHERE visited_links.url='" + url + "'", "id");
        StringBuilder batchQuery = new StringBuilder();
        batchQuery.append("INSERT INTO inverted_index(id, stemmed_id, original_id, url_id, position) VALUES");
        for (int i = 0; i < stemmedWords.size(); i++) {
            long id = (long) executeQuery("select nextval('inverted_index_id_seq') as id", "id");
            batchQuery.append("(").append(id).append(",(SELECT id FROM stemmed_word WHERE word='").append(stemmedWords.get(i)).append("'),(SELECT id FROM original_word WHERE word='").append(originalWords.get(i)).append("'),").append(urlId).append(",").append(positions.get(i)).append(")").append(",");
        }
        batchQuery.deleteCharAt(batchQuery.length() - 1);
        batchQuery.append(";");
        executeUpdate(batchQuery.toString());
    }

    public void deleteDocument(String url){
        executeUpdate("DELETE FROM inverted_index WHERE url_id=(SELECT id FROM visited_links WHERE visited_links.url='"+ url +"');");
    }
}
