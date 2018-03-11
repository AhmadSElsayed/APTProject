package Crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Utilities {
    /**
     * Fetches all the disallowed links in robot.txt
     * @param theLink the Url of robot.txt
     * @return string ArrayList
     */
    static ArrayList<String> robotFetcher(String theLink) {
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
                            links.add(line.replaceAll("Disallow: ", ""));
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
     * @param robotLink the link of robot.txt
     * @return String link of siteMap if found or -1 if not found
     */
    static String getSiteMapLink(String robotLink) {
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
            ex.printStackTrace();
        }
        return siteMap;
    }

    /**
     * fetches all the links in the sitemap url
     * @param  xmlLink the link of sitemap
     * @return String ArrayList of links
     */
    static ArrayList<String> siteMapFetcher(String xmlLink)  {
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
            ex.printStackTrace();
        }
        return siteMapLinks;
    }
}
