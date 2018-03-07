/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.javarticles.guava;


import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author shazlola
 */

public class Indexer implements Runnable {
   public static Map<String, Set<Integer>> InvertedIndex = new HashMap<String,Set<Integer>>(); // words and their document's number
   public static Set<Integer> h = new HashSet<Integer>(); // for docs
   public static   FileWriter   file; // index file
   public static HashMap<String, String> StopWords = new HashMap<String, String>(){{
    put("ourselves","yes");
    put("yes","yes");
    put("hers","yes");
    put("between","yes");
    put("yourself","yes");
    put("again" ,"yes");
    put("there","yes");
    put("about" ,"yes");
    put("some" ,"yes");
    put("off" ,"yes");
    put("other" ,"yes");
    put( "be","yes");
    put("an" ,"yes");
    put( "own","yes");
    put( "they","yes");
    put("itself" ,"yes");
    put( "with" ,"yes");
    put( "out","yes");
    put( "having","yes");
    put("very" ,"yes");
    put("after" ,"yes");
    put("during" ,"yes");
    put( "once","yes");
    put( "who","yes");
    put("from" ,"yes");
    put( "as","yes");
    put( "am","yes");
    put( "s","yes");
    put("is" ,"yes");
    put("or" ,"yes");
    put("most" ,"yes");
    put("of" ,"yes");
    put("into" ,"yes");
    put( "such","yes");
    put("yours" ,"yes");
    put( "its","yes");
    put( "do","yes");
    put(  "for","yes");
    put( "more","yes");
    put( "were","yes");
    put( "her","yes");
    put( "me","yes");
    put( "nor","yes");
    put("done" ,"yes");
    put( "through","yes");
    put( "his","yes");
    put( "your","yes");
    put("these" ,"yes");
    put( "we","yes");
    put("our" ,"yes");
    put("are" ,"yes");
    put("below" ,"yes");
    put( "until","yes");
    put("themselves" ,"yes");
    put( "each","yes");
    put( "the","yes");
    put("him" ,"yes");
    put("at" ,"yes");
    put( "no","yes");
    put("all" ,"yes");
    put( "she","yes");
    put( "had","yes");
    put("to" ,"yes");
    put( "ours","yes");
    put( "up","yes");
    put("both" ,"yes");
    put("while" ,"yes");
    put("above" ,"yes");
    put("their" ,"yes");
    put("should" ,"yes");
    put("down" ,"yes");
    put("this" ,"yes");
    put("himself" ,"yes");
    put( "i","yes");
    put( "why","yes");
    put("over" ,"yes");
    put("so" ,"yes");
    put("not" ,"yes");
    put("what" ,"yes");
    put( "because","yes");
    put( "on","yes");
    put( "yourselves","yes");
    put("then" ,"yes");
    put( "that","yes");
    put( "does" ,"yes");
    put("will" ,"yes");
    put( "in","yes");
    put("been" ,"yes");
    put("have" ,"yes");
    put( "and","yes");
    put( "same","yes");
    put("them" ,"yes");
    put( "before","yes");
    put( "any","yes");
    put("when" ,"yes");
    put( "myself","yes");
    put( "those","yes");
    put(  "which","yes");
    put( "than","yes");
    put(  "too","yes");
    put("only" ,"yes");
    put( "where","yes");
    put( "just" ,"yes");
    put("has" ,"yes");
    put("herself" ,"yes");
    put( "here","yes");
    put( "he" ,"yes");
    put("you" ,"yes");
    put( "under","yes");
    put( "now","yes");
    put("did" ,"yes");
    put("can" ,"yes");
    put(  "was","yes");
    put( "how","yes");
    put("further" ,"yes");
    put("it" ,"yes");
    put("doing" ,"yes");
    put( "by","yes");
    put( "a" ,"yes");
    put("against" ,"yes");
    put("my" ,"yes");
    put("theirs" ,"yes");
    put("if" ,"yes");
    put("t" ,"yes");
    put("being" ,"yes");
    put( "whom","yes");
    put("few" ,"yes");
    put("-","yes");
    put("#","yes");
    put("?","yes");
    }};

    public Indexer() throws IOException {
        this.file = new FileWriter("/home/shazlola/NetBeansProjects/JavaApplication5/src/javaapplication5/indexfile.txt");
    }
  
   
   
     @Override
    public void run() {
        
        File directory = new File("/home/shazlola/NetBeansProjects/JavaApplication2/project/Docs");
     File[] list= directory.listFiles();
     try{   
     getContent(list);
     }catch(IOException e){
         
         e.printStackTrace();
     }
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public static void main(String[] args) throws InterruptedException, IOException {
        // TODO code application logic here
        // get path of folder
     
         Thread ind1 = new Thread(new Indexer());
         Thread ind2 = new Thread(new Indexer());
         Thread ind3 = new Thread(new Indexer());
         Thread ind4 = new Thread(new Indexer());
         Thread ind5 = new Thread(new Indexer());
         Thread ind6 = new Thread(new Indexer());
         ind1.start(); ind2.start();
         ind3.start(); ind4.start();
         ind5.start(); ind6.start();
         ind1.join(); ind2.join();
         ind3.join(); ind4.join();
         ind5.join(); ind6.join();
    
       writeIntoFile();
     
    }
    
    // get content of text file
    public static void getContent (File[] list) throws IOException{
        String [] splitedArr ;
        String txt="";
        Map<String, Integer> WordsMap = new HashMap<String, Integer>(); // word and frequency 
        for (int i=0; i<list.length;i++)
        {
            String fileName =list[i].getPath();
            //reading from file
            BufferedReader br= new BufferedReader(new FileReader(fileName));
            String line =null;
            while((line = br.readLine())!= null){
                txt+=line;
            }
           txt= txt.toLowerCase();
            splitedArr=txt.split("\\s+");
            // calculate frequency  
            for (String word : splitedArr) {
                if(StopWords.get(word) == null){
                    word= removeSpecialChar(word);
                    word =  stemming(word);
                Integer count = WordsMap.get(word);
                   if (count == null) {
                   WordsMap.put(word, 1);
                   } else {
                   WordsMap.put(word, count + 1);
                   }
                
                }
                  
}
               makeInvertedIndex(  ( HashMap<String, Integer> )  WordsMap,fileName);   
         
            txt="";
            WordsMap.clear();
        }
        
    }
public static void makeInvertedIndex(HashMap<String, Integer> WordsMap, String fileName) throws FileNotFoundException, UnsupportedEncodingException{
 
  
   int docNum =Integer.parseInt(fileName.replaceAll("[\\D]", ""))-20;
                 
     for ( String key :WordsMap.keySet() ) {
       
           h.add(docNum);
          InvertedIndex.put(key,  h);  
} 
    
}
   
public static String removeSpecialChar(String word){
   word = word.replaceAll("[^a-zA-Z0-9]", "");
   return word;
}   
   
public static void writeIntoFile() throws IOException{
       try {
           BufferedWriter writer =new BufferedWriter(file);
           for(String key : InvertedIndex.keySet()){
             writer.write(key); 
             writer.write("  ");
              writer.write("[ ");
             for(int val : InvertedIndex.get(key)){
                 String str= String.valueOf(val);
                 int len=str.length();
              writer.write(str,0,len);
               writer.write(",");
              str=""; len=0; 
             }
             writer.write("]");
             writer.newLine();
           }
            writer.close();   
       } catch (IOException ex) {
           Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
       }  
}
public static String stemming (String word)
    {
        SnowballStemmer x = new englishStemmer();
        x.setCurrent(word);
        x.stem();
        return x.getCurrent();  
    } 
}
