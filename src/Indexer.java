/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication7;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import java.io.*;
import java.util.*;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase; 
import com.mongodb.MongoClient; 
import com.mongodb.MongoCredential;  
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.net.UnknownHostException;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.util.ArrayList;
import java.util.List;
import javax.management.Query;
 
 

 

/**
 *
 * @author shazlola
 */
public class Indexer
{

    /**
     *
     */
    public static   FileWriter   file; // index file
    public static Mongo mongo = new Mongo("localhost", 27017);
    public static DB db = mongo.getDB("Shazlola");
    public static DBCollection collection  = db.getCollection( "batot" );
  /*   private static void regetCollection()

    {
     collection = db.getCollection( "newcol2" );

     collection.drop();

     collection = db.getCollection( "newcol2" );

   }*/
    /**
     *
     */
    public static Map<String, Vector< IntPair > > wordsPositions = new HashMap<>(); // words and their position in docs
    public static HashMap<String, String> StopWords = new HashMap<String, String>(){{
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

   
    
         public Indexer() throws IOException {
        Indexer.file = new FileWriter("/home/shazlola/NetBeansProjects/JavaApplication5/src/javaapplication5/indexfile.txt");
    }
  
     

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
     
         //   collection.createIndex("word");
      
    
      File [] myList= readFolder();
     try{   
    getContentofDoc(myList);
       //  removeUpdated();

     }catch(IOException e){
         
     }
     boolean flag=true;
       if(flag){
           String fileName= "/home/shazlola/Desktop/updated.txt";
           List<Integer> updatedList=new ArrayList<>();
            BufferedReader br = null;
		FileReader fr = null;

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
                        
			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
                         //   update(Integer.parseInt(sCurrentLine));
                           updatedList.add(Integer.parseInt(sCurrentLine));
				
			}
                        
                      //  System.out.println(updatedList);
                }catch(IOException e){
                }
           
          removeUpdated(updatedList);
        }    
    }
    public static File[] readFolder(){
       File directory =  new File("/home/shazlola/NetBeansProjects/JavaApplication2/project/Docs");
       File[] list= directory.listFiles();
       return list;
        
    }
    
    
   public static void getContentofDoc(File[] myList) throws IOException{
        String [] splitedArr ;
        String txt="";
        int position=1;
         Map< String, Vector<IntPair>  > myMap ;
         myMap= new HashMap <> ();
        Map<String, Integer> WordsMap; // word and frequency 
        WordsMap = new HashMap<>();
        
        int cnt=0;
        for (File myDoc: myList)
        {
            String fileName = myDoc.getPath();
            BufferedReader br= new BufferedReader(new FileReader(fileName));
            String line;
            while((line = br.readLine())!= null){
                txt+=line;
            }
            txt= txt.toLowerCase();
            splitedArr=txt.split("\\s+");
            
             int docNum= Integer.parseInt(fileName.replaceAll("[\\D]", ""))-20;
             for (int i=0;i<splitedArr.length;i++) {
               String word=splitedArr[i];
                if(StopWords.get(word) == null){
                    word= removeSpecialChar(word);
                    String theword=word;
                    word =  stemming(word);
                    
                    BasicDBObject query = new BasicDBObject();
                    query.put("word", word);
              DBObject cur = collection.findOne(query);
              if(cur==null){
              insertNewWord(word, docNum,position,theword);}
              else {
               insertNewDoc(word,docNum,position,theword);
              }
                  /*  DBCursor cursor = collection.find( new BasicDBObject("word", word),new BasicDBObject("docs.theword", theword));
                    //System.out.println(cursor);
                    DBObject ob=cursor.next();
                    System.out.println(ob);
                  if(!cursor.hasNext()){
                      System.out.println("ana hena");
                  }
                  else {
                    //System.out.println("d5lt hna");
                      //System.out.println(getIndexOfDoc(word,theword));
        //  insertNewPositionInWords( word, docNum, position, theword);

                  }
                  
                  
              }
             /* 
                            
                    
              }*/
              
        
         
                     
                    

                     position++;
                    
                }
                   
             }
          
             WordsMap.clear();
               txt=""; position=1;
               
        
              
   
        }
       
      
   }  

     
   public static String removeSpecialChar(String word){
   word = word.replaceAll("[^a-zA-Z0-9]", "");
   return word;
} 
    public static String stemming (String word)
    {
        SnowballStemmer x = new englishStemmer();
        x.setCurrent(word);
        x.stem();
        return x.getCurrent();  
    } 
    
    
    public static void insertNewWord(String word, int docNum, int wordpos, String theword){
        
        
       
        
        BasicDBObject document = new BasicDBObject();
	document.put("word", word);
            

	BasicDBList docs = new BasicDBList();
        BasicDBObject doc = new BasicDBObject();
	doc.put("theword", theword);
        
       BasicDBList positions = new BasicDBList();
        
        BasicDBObject position = new BasicDBObject();
        position.put("docnum",docNum);
        position.put("wordpos", wordpos);
        
        positions.add(position);
        
        
        doc.put("positions",position);
        docs.add(doc);
       
        document.put("docs", docs);
     
        try{
            
        collection.insert(document);
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
       

    }
    
    public static void insertNewDoc(String word, int docNum, int wordpos, String theword){
        
        DBObject searchObject =new BasicDBObject();
        searchObject.put("word", word);

     BasicDBList docs = new BasicDBList();
     BasicDBObject doc = new BasicDBObject();
     doc.put("theword", theword);
    BasicDBList positions = new BasicDBList();
        
       BasicDBObject position = new BasicDBObject();
        position.put("docnum",docNum);
        position.put("wordpos", wordpos);
        
      positions.add(position);
        doc.put("positions",position);
     DBObject modifiedObject =new BasicDBObject();
     modifiedObject.put("$addToSet", new BasicDBObject().append("docs", doc));
      collection.update(searchObject, modifiedObject);
       
       
       
    }
    
/* public static void insertNewPositionInWords(String word,int docNum,int wordpos,String theword){
        
        DBObject searchObject =new BasicDBObject(); 
		searchObject.put("word", word);
                searchObject.put("docs.theword",theword);
       
        BasicDBObject position = new BasicDBObject();
        position.put("docnum",docNum);
        position.put("wordpos", wordpos);
      
        DBObject modifiedObject =new BasicDBObject();
        modifiedObject.put("$addToSet", new BasicDBObject().append("docs.positions", position));
        collection.update(searchObject, modifiedObject);
          
    }
/*public static Integer getIndexOfDoc(String word,String theword){
   
    int idx=0;
    BasicDBObject ob= new BasicDBObject("word",word);
    DBCursor cur = collection.find(ob);
    DBObject myObj= cur.next();

    BasicDBList docs= ( BasicDBList) myObj.get("docs");
  
         for (int i=0;i<docs.size();i++)
         {
             BasicDBObject doc = (BasicDBObject) docs.get(i);
         
        if(doc.getString("theword").equals(theword))
        {
          
            idx=i;
             break;
        }
           
      
    }
         return idx;
         
         
}

/*    public static void getDoc(String word,String theword){
        DBObject query= new BasicDBObject();
        query.put("word", word);
      
        Document document = (Document) collection.findOne(query);

      ListIterator<Object> docs = ((BasicDBList) document.get("docs")).listIterator();
      
      

    }*/
    public static void removeUpdated (List <Integer> myList){
       /*  BasicDBObject b1 = new BasicDBObject();
            BasicDBObject b2 = new BasicDBObject();
            BasicDBObject b3 = new BasicDBObject();
            BasicDBObject b4 = new BasicDBObject();
            BasicDBObject b5 = new BasicDBObject();
            
            
            
             b2.put("docnum",2 );
             b2.put("wordpos", 1);
            b3.put("positions",b2);
            
            b4.put("$pull",b3);
            b5.put("multi","true");


            collection.update(b1,b4,false,true);*/
            DBCursor cur = collection.find();
            List<DBObject> documents= cur.toArray();
            for (int i=0;i< documents.size();i++){
                DBObject ob= documents.get(i);
                String word = (String) ob.get("word");
              List<DBObject> docs=  (List<DBObject>) ob.get("docs");
                for (int j=0;j<docs.size();j++) {
                    String theword= (String) docs.get(j).get("theword");
                     //System.out.println(theword);
                     DBObject pos= (DBObject) docs.get(j).get("positions");
                   //  System.out.println(pos);
                    int docnum= (int) pos.get("docnum");
                   // System.out.println(docnum);
                  if (myList.contains(docnum)){
                        BasicDBObject query = new BasicDBObject("word", word);
                     // System.out.println("ana hena h-delete aho");
                     BasicDBObject deletedOne =  (BasicDBObject) docs.get(j);
                      System.out.println(deletedOne);
                   BasicDBObject update = new BasicDBObject("$pull",deletedOne);
                    BasicDBObject obj1= new BasicDBObject();
                    BasicDBObject obj2= new BasicDBObject();
                    obj2.put("multi", "true");
                   collection.update( obj1, update,false,true );
                      
                      
                      //  collection.findAndRemove(deletedOne);
                    }
                }
               
             
                
                
            }
      
  

    }
    
    
    private Object key(boolean add) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}