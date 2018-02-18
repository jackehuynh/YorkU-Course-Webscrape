package webscrape;

import java.io.IOException;
import org.jsoup.Connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Redirect {

    String url;

    public Redirect() {
        /*
        this.url = url;

        try {
            Document doc = Jsoup.connect(this.url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
    
    public void setUrl(String urls) {
        this.url = urls;
    }
    
    public String toString() {
        return this.url;
    }

}
