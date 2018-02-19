package webscrape;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Webscrape {

    public static void main(String[] args) {
        try {
            // initialize browser
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
            //WebDriver driver = new HtmlUnitDriver();    // no-GUI browser
            WebDriver driver = new ChromeDriver();   // GUI (Chrome) browser
            ScrapeBrowser courseScraper = new ScrapeBrowser(driver); // initializes scraper
            courseScraper.startConnection();
            //courseScraper.outputToFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}