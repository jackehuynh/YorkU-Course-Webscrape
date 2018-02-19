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

        try {
            // initialize browser
//            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
//            WebDriver driver = new ChromeDriver();   // GUI (Chrome) browser
            HtmlUnitDriver driver = new HtmlUnitDriver(true);    // head-less browser w/ JS enabled
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); // waits to find a specified web element before moving on

            // remove error messages in console for testing purposes
            Logger logger = Logger.getLogger("");
            logger.setLevel(Level.OFF);

            ScrapeBrowser courseScraper = new ScrapeBrowser(driver); // initializes scraper
            courseScraper.startConnection();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
        printToFile = new WriteToFile();
        printToFile.setDeptSize(option.size());
        printToFile.setCourseArray(result);
        printToFile.setCourseAmount(courseCode.size());
        printToFile.printOutFile();

 */
