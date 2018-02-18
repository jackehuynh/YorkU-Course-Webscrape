package webscrape;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.io.IOException;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.chrome.ChromeDriver;

public class Webscrape {

    public static void main(String[] args) {
        try {
            // initialize browser
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
            WebDriver driver = new HtmlUnitDriver();
            // WebDriver driver = new ChromeDriver();
            ScrapeBrowser courseScraper = new ScrapeBrowser(driver); // initializes scraper
            courseScraper.startConnection();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect(String url) throws IOException {
        Connection connect = Jsoup.connect(url);
        System.out.println(url);
        Document doc = Jsoup.connect(url).get();
        return connect;
    }

    public static Document parse(String html) throws IOException {
        Document doc = Jsoup.connect(html).get();
        return doc;
    }
}

/*
            Document doc2 = Jsoup.connect(absHref).userAgent("Mozilla").get();
            Elements summerResult = doc2.select("form[name]");  // searches for css selectors with form[name]
            Elements findSummerElement = summerResult.select("select.bodytext[name=sessionPopUp]"); // searches for <select> with class "bodytext" with attribute name="sessionPopUp"

            
            
            String summerSelectOption = "1";    // this is the option value for 'summer'
            Elements summerOption = findSummerElement.select("option");  // within the select tag, search for <option> tags
            String sessionPopup = findSummerElement.attr("[value=1]");
            //System.out.println("Session pop:" + summerOption);
            
            if (findSummerElement.attr("name").equals("sessionPopUp")) { // 
                for (Element summerOptions : summerOption) {
                    if (summerOptions.attr("value").equals(summerSelectOption)) {
                        summerOptions.attr("selected", "selected"); // selects the summer 2018 option
                    } else {
                        summerOptions.removeAttr("selected");   // removes the default selection on f/w option
                    }
                }
            }
           
            }
            Document docs = Jsoup.connect("http://espn.go.com/mens-college-basketball/conferences/standings/_/id/2/year/2012/acc-conference").get();
     
            for (Element table: docs.select("table.tablehead")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    if (tds.size() > 6) {
                        System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
                    }
                }
            }
 */
