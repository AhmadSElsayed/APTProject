import com.mongodb.*;
import org.omg.PortableInterceptor.INACTIVE;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Indexer {


    private DBCollection collection;

    Indexer(Mongo mongo, DB db, DBCollection collection) throws UnknownHostException {
        this.collection  = collection;
    }



    private HashMap<String, String> StopWords = new HashMap<String, String>(){{
        put("ourselves","yes");put("yes","yes");put("hers","yes");put("between","yes");
        put("yourself","yes");put("again" ,"yes");put("there","yes");put("about" ,"yes");
        put("some" ,"yes");put("off" ,"yes");put("other" ,"yes");put( "be","yes");
        put("an" ,"yes");put( "own","yes");put( "they","yes");put("itself" ,"yes");
        put( "with" ,"yes");put( "out","yes");put( "having","yes");put("very" ,"yes");
        put("after" ,"yes");put("during" ,"yes");put( "once","yes");put( "who","yes");
        put("from" ,"yes");put( "as","yes");put( "am","yes");put( "s","yes");
        put("is" ,"yes");put("or" ,"yes");put("most" ,"yes");put("of" ,"yes");
        put("into" ,"yes");put( "such","yes");put("yours" ,"yes");put( "its","yes");
        put( "do","yes");put(  "for","yes");put( "more","yes");put( "were","yes");
        put( "her","yes");put( "me","yes");put( "nor","yes");put("done" ,"yes");
        put( "through","yes");put( "his","yes");put( "your","yes");put("these" ,"yes");
        put( "we","yes");put("our" ,"yes");put("are" ,"yes");put("below" ,"yes");
        put( "until","yes");put("themselves" ,"yes");put( "each","yes");put( "the","yes");
        put("him" ,"yes");put("at" ,"yes");put( "no","yes");put("all" ,"yes");
        put( "she","yes");put( "had","yes");put("to" ,"yes");put( "ours","yes");
        put("both" ,"yes");put("while" ,"yes");put("above" ,"yes");put("their" ,"yes");
        put("should" ,"yes");put("down" ,"yes");put("this" ,"yes");put("himself" ,"yes");
        put( "i","yes");put( "why","yes");put("over" ,"yes");put("so" ,"yes");
        put("not" ,"yes");put("what" ,"yes");put( "because","yes");put( "on","yes");
        put( "yourselves","yes");put("then" ,"yes");put( "that","yes");put( "does" ,"yes");
        put("will" ,"yes");put( "in","yes");put("been" ,"yes");put("have" ,"yes");put( "and","yes");
        put( "same","yes");put("them" ,"yes");put( "before","yes");put( "any","yes");
        put("when" ,"yes");put( "myself","yes");put( "those","yes");put(  "which","yes");
        put( "than","yes");put(  "too","yes");put("only" ,"yes");put( "where","yes");
        put( "just" ,"yes");put("has" ,"yes");put("herself" ,"yes");put( "here","yes");
        put( "he" ,"yes");put("you" ,"yes");put( "under","yes");put( "now","yes");
        put("did" ,"yes");put("can" ,"yes");put("doing" ,"yes");put( "by","yes");
        put(  "was","yes");put( "how","yes");put("further" ,"yes");put("it" ,"yes");
        put( "a" ,"yes");put("against" ,"yes");put("my" ,"yes");put("theirs" ,"yes");
        put("if" ,"yes");put("t" ,"yes");put("being" ,"yes");put( "whom","yes");
        put("few" ,"yes");put("-","yes");put("#","yes");put("?","yes");put( "up","yes");
    }};

    synchronized void indexDoc(String myDoc, int docNum){
        String [] splitedArr ;
        String txt;
        int position=1;
        txt= myDoc.toLowerCase();
        splitedArr=txt.split("\\s+");

        for (String aSplitedArr : splitedArr) {
            String word = aSplitedArr;
            if (StopWords.get(word) == null) {
                word = removeSpecialChar(word);
                String theword = word;
                word = stemming(word);
                BasicDBObject query = new BasicDBObject();
                query.put("word", word);
                DBObject cur = collection.findOne(query);
                if (cur == null) {
                    insertNewWord(word, docNum, position, theword);
                } else {
                    insertNewDoc(word, docNum, position, theword);
                }
                position++;

            }



        }


    }

    private  String removeSpecialChar(String word){
        word = word.replaceAll("[^a-zA-Z0-9]", "");
        return word;
    }
    private  String stemming(String word) {
        SnowballStemmer x = new englishStemmer();
        x.setCurrent(word);
        x.stem();
        return x.getCurrent();
    }

    private synchronized  void insertNewWord(String word, int docNum, int wordpos, String theword){




        BasicDBObject document = new BasicDBObject();
        document.put("word", word);


        BasicDBList docs = new BasicDBList();
        BasicDBObject doc = new BasicDBObject();
        doc.put("theword", theword);


        BasicDBObject position = new BasicDBObject();
        position.put("docnum",docNum);
        position.put("wordpos", wordpos);

        doc.put("positions",position);
        docs.add(doc);

        document.put("docs", docs);

        try{

            collection.insert(document);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }


    }

    private synchronized void insertNewDoc(String word, int docNum, int wordpos, String theword){

        DBObject searchObject =new BasicDBObject();
        searchObject.put("word", word);

        BasicDBObject doc = new BasicDBObject();
        doc.put("theword", theword);

        BasicDBObject position = new BasicDBObject();
        position.put("docnum",docNum);
        position.put("wordpos", wordpos);

        doc.put("positions",position);
        DBObject modifiedObject =new BasicDBObject();
        modifiedObject.put("$addToSet", new BasicDBObject().append("docs", doc));
        collection.update(searchObject, modifiedObject);



    }

    public  void removeUpdated (List <Integer> myList){
        DBCursor cur = collection.find();
        List<DBObject> documents= cur.toArray();
        for (DBObject ob : documents) {
            String word= (String) ob.get("word");
            List<DBObject> docs = (List<DBObject>) ob.get("docs");
            for (DBObject doc : docs) {
                String theword= (String) doc.get("theword");
                DBObject pos = (DBObject) doc.get("positions");
                int docnum = (int) pos.get("docnum");
                if (myList.contains(docnum)) {
                    BasicDBObject deletedOne = (BasicDBObject) doc;
                    BasicDBObject update = new BasicDBObject("$pull", deletedOne);
                    BasicDBObject obj1 = new BasicDBObject("word",word).append("docs.theword",theword);
                    collection.update(obj1, update, false, true);


                }
            }


        }



    }



}