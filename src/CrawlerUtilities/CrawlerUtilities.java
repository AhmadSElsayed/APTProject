package CrawlerUtilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class CrawlerUtilities {

    public static String getRobotText(String url) {
        //array of links
        StringBuilder regex = new StringBuilder();
        try {
            //fetch the url.txt
            HttpURLConnection robotFile = (HttpURLConnection) (new URL("https://" +  new URL(url).getHost() + "/robots.txt")).openConnection();
            robotFile.addRequestProperty("User-Agent", "Mozilla/4.0");
            //read robot.txt line by line
            Scanner robotScanner = new Scanner(robotFile.getInputStream());
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
                            regex.append(line.replaceAll("Disallow:", "")).append("|");
                        //return if u found User-agent
                        else if (line.contains("User-agent:"))
                        {
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //return after finishing the file
        if(regex.toString().isEmpty())
            return "(?=a)b";
        regex.deleteCharAt(regex.length() - 1);
        return regex.toString().replaceAll(" ", "").replaceAll("\\*", ".*");
    }

    private static String getSiteMapLink(String url) {
        try {
            HttpURLConnection robotFile = (HttpURLConnection) (new URL("https://" +  new URL(url).getHost() + "/robots.txt")).openConnection();
            robotFile.addRequestProperty("User-Agent", "Mozilla/4.0");
            //read robot.txt line by line
            Scanner robotScanner = new Scanner(robotFile.getInputStream());
            while (robotScanner.hasNextLine()) {
                String line = robotScanner.nextLine();
                //search for disallow
                if (line.contains("Sitemap:") || line.contains("sitemap:")) //websites have either one
                    return line.toLowerCase().replaceAll("sitemap: ", "");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getSiteMap(String s) {
        String xmlLink = getSiteMapLink(s);
        ArrayList<String> siteMapLinks = new ArrayList<>();
        if (xmlLink == null)
            return siteMapLinks;
        try {
            Document doc = Jsoup.connect(xmlLink).get();
            //get the urls between loc  tag
            Elements urls = doc.getElementsByTag("loc");
            //saving the urls
            for (Element url : urls) {
                //if url.text() is xml , call function again and save the result(array)
                if(url.text().contains("xml")){
                    siteMapLinks.addAll(getSiteMap(url.text()));
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

    public static String linkCleaner(String link) {
            //remove hashing
            int hashIndex = link.indexOf("#");
            int queryIndex = link.indexOf("?"); //indexOf returns -1 if it didn't find the specific char/string

            //removing # in links if found
            if (hashIndex != -1) {
                StringBuilder newLink = new StringBuilder(link);
                newLink.delete(hashIndex, link.length());
                link = newLink.toString();
            }

            //removing ? in links if found
            if (queryIndex != -1) {
                StringBuilder newLink = new StringBuilder(link);
                newLink.delete(queryIndex, link.length());
                link = newLink.toString();
            }

            if (link.endsWith("/"))
                link = link.substring(0, link.length() - 1);
        return link;
    }

}
