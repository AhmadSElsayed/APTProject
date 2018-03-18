import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws Exception {
//        ** TESTS (note::: take care of http and https)**
//              testing robot.txt fetcher (uncomment to test)
//
//        ArrayList<String> links = new ArrayList<>();
//       links = Utilities.robotFetcher("https://twitter.com/robots.txt");//https://www.facebook.com/robots.txt/http://www.mbc.net/robots.txt
//        for(String lstitem : links){
//
//            System.out.println("List item: " + lstitem);
//        }



//                testing sitemaps(uncomment to test)
//      NOTE ,empty xml links aka empty urls throws an exception
//
//        ArrayList<String> links;
//        String abzo = Utilities.getSiteMapLink("https://jsoup.org/robots.txt");
//        System.out.println("the xml link is: " + abzo);
//        links = Utilities.siteMapFetcher(abzo);
//
//        for (String lstitem : links) {
//
//            System.out.println("List item: " + lstitem);
//        }
//
//                 testing linkCLeaner();
//        String link = "https://en.wikipedia.org/wiki/Crocodile#See_also";//https://www.youtube.com/watch?v=WsEmZU7U124
//       System.out.println( Utilities.linkCleaner(link));

        //Utilities.stringMatcher("ab");


//                  testing saveMap();
//        HashMap <String, Integer> hm = new HashMap<String, Integer>();
//        hm.put("abzo.com",2);
//        hm.put("fuckYou.net",3);
//
//        Utilities.saveMap(hm,"Abzo");

//                  testing getMapFromFile
//          HashMap <String, Integer> hm = new HashMap<String, Integer>();
//          hm = Utilities.getMapFromFile("Abzo");
//          Utilities.saveMap(hm,"NotAbzo");

//                  testing patternCreator() and isLinkInRobot()
//       ArrayList<Pattern> links = Utilities.patternCreatort(Utilities.robotFetcher("http://www.mbc.net/robots.txt"));
//       System.out.println(Utilities.isLinkInRobot("www.mbc.net/rss/abzo",links));

    }
}
