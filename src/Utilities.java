import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.StringBuilder;
import java.net.URL;


import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Utilities {

    /**
     * Fetches all the disallowed links in robot.txt
     * #@param String theLink the Url of robot.txt
     *
     * @return string ArrayList
     */
    public static ArrayList<String> robotFetcher(String theLink) {
        //array of links
        ArrayList<String> links = new ArrayList<>();
        try {
            //fetch the url.txt
            URL robotFile = new URL(theLink);

            //read robot.txt line by line
            Scanner robotScanner = new Scanner(robotFile.openStream());
            while (robotScanner.hasNextLine()) {

                //search for user-agent
                String line = robotScanner.nextLine();
                if (line.equals("User-agent: *")) {

                    //scan till end of file or another User-agent
                    while (robotScanner.hasNextLine()) {
                        line = robotScanner.nextLine();

                        //search for disallow
                        if (line.contains("Disallow:"))

                            //get all the disallowed links
                            links.add(line.replaceAll("Disallow:", ""));

                            //return if u found User-agent
                        else if (line.contains("User-agent:"))
                            return links;
                    }
                }
            }

        } catch (IOException ex) {

            ex.printStackTrace();
        }
        //return after finishing the file
        return links;
    }



    /**
     * gets the location (url) of the sitemap in robot.txt
     * #@param String robotLink the link of robot.txt
     *
     * @return String link of siteMap if found or -1 if not found
     */
    public static String getSiteMapLink(String robotLink) {

        String siteMap = "-1";
        try {
            URL robotFile = new URL(robotLink);

            //read robot.txt line by line
            Scanner robotScanner = new Scanner(robotFile.openStream());
            while (robotScanner.hasNextLine()) {
                String line = robotScanner.nextLine();

                //search for disallow
                if (line.contains("Sitemap:") || line.contains("sitemap:")) //websites have either one

                    siteMap = line.toLowerCase().replaceAll("sitemap: ", "");
            }
        }
        catch (IOException ex) {

           // ex.printStackTrace();
        }
        return siteMap;
    }



    /**
     * fetches all the links in the sitemap url
     * #@param  String xmlLink the link of sitemap
     *
     * @return String ArrayList of links
     */
    public static ArrayList<String> siteMapFetcher(String xmlLink)  {


        ArrayList<String> siteMapLinks = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(xmlLink).get();

            //get the urls between loc  tag
            Elements urls = doc.getElementsByTag("loc");

            //saving the urls
            for (Element url : urls) {

                //if url.text() is xml , call function again and save the result(array)
                if(url.text().contains("xml")){

                    siteMapLinks.addAll(siteMapFetcher(url.text()));
                }

                else{
                    siteMapLinks.add(url.text());
                }
            }
        }
        catch (IOException ex) {
          //  ex.printStackTrace();
        }

        return siteMapLinks;
    }



    /**
     * removes Hashes/question marks in links(that links to the same page)
     * #@param  String Link the link to be cleaned
     *
     * @return String newLink or -1 if it is already clean
     */
    @NotNull
    public static String linkCleaner(String link) {

        //remove hashing
       int index = link.indexOf("#");
       int index1 = link.indexOf("?"); //indexOf returns -1 if it didn't find the specific char/string

        //removing # in links if found
       if(index != -1){
           StringBuilder newLink = new StringBuilder(link);
           newLink.delete(index,link.length());
           return newLink.toString();
       }

       //removing ? in links if found
       else if(index1 != -1){
           StringBuilder newLink = new StringBuilder(link);
           newLink.delete(index1,link.length());
           return newLink.toString();
       }
      return "-1";
    }



    /**
     * checks for the link in the robot.txt
     * #@param  String Link the link u want to check
     * #@param  String one of the links in robot.txt
     *
     * @return boolean true if link is found in robot.txt
     */
    public static boolean stringMatcher(String link, String oneRobotLink){

        return (link.contains(oneRobotLink)) ;
    }



    /**
     * saves a HashMap of type String,Integer in the form of  "string" AKA URL,  "int" AKA document #
     * #@param  HashMap theMap of String,Integer
     * #@param  String fileName
     *
     * @return boolean true if link is found in robot.txt
     */
    public static void saveMap(HashMap< String,Integer> theMap ,String fileName) throws Exception
    {

        PrintWriter outFile = new PrintWriter(fileName + ".txt");

        for (HashMap.Entry<String, Integer> pair : theMap.entrySet()) {

            outFile.println(pair.getKey()+" "+pair.getValue());
        }
        outFile.close ();
    }



    /**
     * saves data(URL->#) of file in a map
     * #@param String textFilePath name without extension
     *
     * @return HashMap
     */
    public static HashMap<String, Integer> getMapFromFile(String textFilePath)throws Exception
    {
        // Open the file
        FileInputStream fstream = new FileInputStream(textFilePath+".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        //create the storage (map/array..etc)
        HashMap<String, Integer> fileMap = new HashMap<String , Integer>();
        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {

            //splitting the line
            String[] splitStr = strLine.split("\\s+"); //split by spaces

            // save to map
            fileMap.put(splitStr[0],Integer.parseInt(splitStr[1]));
        }

        //Close the input stream
        br.close();
        return fileMap;
    }



    /**
     * creates a pattern list from an ArrayList of Strings(URLS of robot.txt)
     * #@param ArrayList robotTxtLinks links fetched from robot.txt
     *
     * @return ArrayList Pattern
     */
    public static ArrayList<Pattern>  patternCreatort(ArrayList<String> robotTxtLinks)
    {
        //creating a pattern arraylist
        ArrayList<Pattern> disallowed = new ArrayList<Pattern>();

        //iterate over the links of robot.txt
        for(String robot : robotTxtLinks)
        {
            disallowed.add(Pattern.compile("(.*)"+robot+"(.*)"));
        }
        return disallowed;
    }



    /**
     * tests a given link if it's disallowed to be fetched (true if it's disallowed)
     * #@param String link , the link u are testing
     * #@param ArrayList Pattern disallowed ,a pattern list
     *
     * @return boolean
     */
    public static boolean isLinkInRobot(String link,ArrayList<Pattern> disallowed)
    {
        for (Pattern eachRobotLink : disallowed)

            if (eachRobotLink.matcher(link).matches()) return true;

        return false;

    }

}
