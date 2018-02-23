package webscrape;

import java.awt.AWTException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import java.util.Scanner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Webscrape {

    public static void main(String[] args) {
        // remove error messages in console for testing purposes
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);
        Scanner reader = new Scanner(System.in);

        WebDriver chrome;
        System.out.println("GUI is mainly for debug/testing. Headless is for performance and final product.");
        System.out.println("Type 1 for Headless browser, 2 for GUI (Chrome): ");
        int response = reader.nextInt();
        System.out.println("Which session? (1 - Fall/Winter) (2 - Summer 2018): ");
        int response2 = reader.nextInt();

        try {
            if (response == 1) {
                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                ChromeDriver driver = new ChromeDriver(chromeOptions);
                
                HeadlessScrape courseInfoScraper = new HeadlessScrape(driver);

                if (response2 == 1) {
                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                } else if (response2 == 2) {
                    courseInfoScraper.setSession("Summer 2018");
                }
                courseInfoScraper.startConnection();

            } else if (response == 2) {

                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                chrome = new ChromeDriver();   // GUI (Chrome) browser
                chrome.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);
                ScrapeCourseInfo courseInfoScraper = new ScrapeCourseInfo(chrome);

                if (response2 == 1) {
                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                } else if (response2 == 2) {
                    courseInfoScraper.setSession("Summer 2018");
                }
                courseInfoScraper.startConnection();

            }

            System.out.println("Scrape finished!");

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }
}
