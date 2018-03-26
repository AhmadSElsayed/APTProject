import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client {





    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 6666);
        Socket s2=new Socket("localhost",6666);

        PrintWriter w = new PrintWriter(s.getOutputStream());
        PrintWriter w2 = new PrintWriter(s2.getOutputStream());

        String str1 = readDoc("/home/shazlola/Desktop/1.txt");
        String str2= readDoc("/home/shazlola/Desktop/2.txt");

        w.println("www.facebook.com");
        w.flush();
        w.println(str1);
        w.flush();


        w2.println("www.twitter.com");
        w2.flush();
        w2.println(str2);
        w2.flush();
        w2.close();
        s2.close();
        w.close();
        s.close();
    }
    static String readDoc(String filePath){

        try {
            return FileUtils.readFileToString(new File(filePath)).replaceAll("[^a-zA-Z0-9 ]", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}