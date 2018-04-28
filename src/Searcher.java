package Searcher;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Searcher extends DatabaseManager {

    private static HashMap<String, String> stopWords = new HashMap<String, String>() {{
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


    public Searcher() throws SQLException, IOException, ClassNotFoundException {
        super();
    }


    public List<String> search(String[] phrases) throws SQLException {
        List<String> list = Arrays.asList(phrases);
        for (int i = 0; i < list.size(); i++) {
            if (stopWords.get(list.get(i)) != null) {
                list.remove(list.get(i));

            }
        }

        phrases = list.toArray(new String[0]);

        Object[] urlList;
        Object[] urlList3;
        List<String> urlList2 = new ArrayList<>();
        ArrayList<Long> idList = new ArrayList<>();

        List<Integer> urls = new ArrayList<>();
        List<Integer> words = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();


        for (int i = 0; i < phrases.length; i++) {
            long id = (long) executeQuery("SELECT id FROM original_word WHERE word ='" + phrases[i] + "'", "id");
            idList.add(id);
        }

        urlList = executeQueryList("SELECT url_id FROM inverted_index WHERE original_id IN '" + (idList) + "'", 1);
        Object[] resultSet = executeQueryList("SELECT url_id, COUNT(url_id),position,original_id  FROM inverted_index where url_id IN'" +
                (urlList) + "' GROUP BY(url_id) ORDER BY(url_id)", 4);


        for (int i = 0; i < resultSet.length; i += 4) {

            if (resultSet[1 + i].equals(phrases.length)) {
                urls.add((Integer) resultSet[0 + i]);
                words.add((Integer) resultSet[2 + i]);
                positions.add((Integer) resultSet[3 + i]);

            }

        }

        int cnt = 0;
        for (int i = 0; i < urls.size() - 1; i++) {
            if (idList.get(i).equals(words.get(i)) && idList.get(i + 1).equals(words.get(i + 1)) && urls.get(i) == urls.get(i + 1)) {
                if (positions.get(i + 1) - positions.get(i) <= 2) {
                    cnt++;
                }
                if (cnt == idList.size()) {
                    String currentUrl = (String) executeQuery("SELECT url from visited_links where id='" + urls.get(i) + "'", "url");
                    urlList2.add(currentUrl);
                    cnt = 0;
                }

            }
        }
        return urlList2;
    }


}

