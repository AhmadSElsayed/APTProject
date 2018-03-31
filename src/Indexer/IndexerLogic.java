package Indexer;

import Database.IndexerDataManager;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class IndexerLogic {
    private IndexerDataManager databaseManager;
    private HashMap<String, String> stopWords = new HashMap<String, String>() {{
        put("ourselves", "yes");
        put("yes", "yes");
        put("hers", "yes");
        put("between", "yes");
        put("yourself", "yes");
        put("again", "yes");
        put("there", "yes");
        put("about", "yes");
        put("some", "yes");
        put("off", "yes");
        put("other", "yes");
        put("be", "yes");
        put("an", "yes");
        put("own", "yes");
        put("they", "yes");
        put("itself", "yes");
        put("with", "yes");
        put("out", "yes");
        put("having", "yes");
        put("very", "yes");
        put("after", "yes");
        put("during", "yes");
        put("once", "yes");
        put("who", "yes");
        put("from", "yes");
        put("as", "yes");
        put("am", "yes");
        put("s", "yes");
        put("is", "yes");
        put("or", "yes");
        put("most", "yes");
        put("of", "yes");
        put("into", "yes");
        put("such", "yes");
        put("yours", "yes");
        put("its", "yes");
        put("do", "yes");
        put("for", "yes");
        put("more", "yes");
        put("were", "yes");
        put("her", "yes");
        put("me", "yes");
        put("nor", "yes");
        put("done", "yes");
        put("through", "yes");
        put("his", "yes");
        put("your", "yes");
        put("these", "yes");
        put("we", "yes");
        put("our", "yes");
        put("are", "yes");
        put("below", "yes");
        put("until", "yes");
        put("themselves", "yes");
        put("each", "yes");
        put("the", "yes");
        put("him", "yes");
        put("at", "yes");
        put("no", "yes");
        put("all", "yes");
        put("she", "yes");
        put("had", "yes");
        put("to", "yes");
        put("ours", "yes");
        put("both", "yes");
        put("while", "yes");
        put("above", "yes");
        put("their", "yes");
        put("should", "yes");
        put("down", "yes");
        put("this", "yes");
        put("himself", "yes");
        put("i", "yes");
        put("why", "yes");
        put("over", "yes");
        put("so", "yes");
        put("not", "yes");
        put("what", "yes");
        put("because", "yes");
        put("on", "yes");
        put("yourselves", "yes");
        put("then", "yes");
        put("that", "yes");
        put("does", "yes");
        put("will", "yes");
        put("in", "yes");
        put("been", "yes");
        put("have", "yes");
        put("and", "yes");
        put("same", "yes");
        put("them", "yes");
        put("before", "yes");
        put("any", "yes");
        put("when", "yes");
        put("myself", "yes");
        put("those", "yes");
        put("which", "yes");
        put("than", "yes");
        put("too", "yes");
        put("only", "yes");
        put("where", "yes");
        put("just", "yes");
        put("has", "yes");
        put("herself", "yes");
        put("here", "yes");
        put("he", "yes");
        put("you", "yes");
        put("under", "yes");
        put("now", "yes");
        put("did", "yes");
        put("can", "yes");
        put("doing", "yes");
        put("by", "yes");
        put("was", "yes");
        put("how", "yes");
        put("further", "yes");
        put("it", "yes");
        put("a", "yes");
        put("against", "yes");
        put("my", "yes");
        put("theirs", "yes");
        put("if", "yes");
        put("t", "yes");
        put("being", "yes");
        put("whom", "yes");
        put("few", "yes");
        put("-", "yes");
        put("#", "yes");
        put("?", "yes");
        put("up", "yes");
    }};

    IndexerLogic() throws SQLException, ClassNotFoundException {
        this.databaseManager = new IndexerDataManager();
    }

    public void deleteDocument(String url) {
        databaseManager.deleteDocument(url);
    }

    public void indexDocument(String document, String url) {
        String[] wordList;
        document = document.toLowerCase();
        wordList = document.split("\\s+");
        ArrayList<String> originalWords = new ArrayList<>();
        ArrayList<String> stemmedWords = new ArrayList<>();
        ArrayList<Integer> positions = new ArrayList<>();

        for (int i = 0; i < wordList.length; i++) {
            if (stopWords.get(wordList[i]) == null) {
                originalWords.add(wordList[i]);
                stemmedWords.add(stemming(wordList[i]));
                positions.add(i);
            }
        }
        databaseManager.addIndex(stemmedWords, originalWords, url, positions);
    }

    private String stemming(String word) {
        SnowballStemmer snowballStemmer = new englishStemmer();
        snowballStemmer.setCurrent(word);
        snowballStemmer.stem();
        return snowballStemmer.getCurrent();
    }

    public void closeDatabaseConnection() throws SQLException {
        databaseManager.closeConnection();
    }
}