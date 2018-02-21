package webscrape;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.WebDriver;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Webscrape {

    public static void main(String[] args) {
        // remove error messages in console for testing purposes
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);

        try {
            // initialize browser
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();   // GUI (Chrome) browser
//            HtmlUnitDriver driver = new HtmlUnitDriver(true);    // head-less browser w/ JS enabled
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // waits to find a specified web element before moving on

//            ScrapeBrowser courseScraper = new ScrapeBrowser(driver);
//            courseScraper.startConnection();

              ScrapeCourseInfo courseInfoScraper = new ScrapeCourseInfo(driver);
              courseInfoScraper.startConnection();
            System.out.println("Scrape finished!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
