package cz.cvut.fel.tk21.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ScrapingUtils {

    public static Document connectGETWithRetries(String link){
        Document doc = null;
        int i = 0;

        while( i < 3){
            try {
                doc = Jsoup.connect(link).get();
                break;
            } catch (IOException ignored) {
            }

            //Wait for 500 ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {

            }
            i++;
        }

        return doc;
    }

}
