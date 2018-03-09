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
                driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);

                HeadlessScrape courseInfoScraper = new HeadlessScrape(driver);

                if (response2 == 1) {
                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                    courseInfoScraper.setFileLocation("src/fallwinter20172018.txt");
                } else if (response2 == 2) {
                    courseInfoScraper.setSession("Summer 2018");
                    courseInfoScraper.setFileLocation("src/summer2018.txt");
                }
                courseInfoScraper.startConnection();

            } else if (response == 2) {

                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                ChromeDriver driver = new ChromeDriver();   // GUI (Chrome) browser
                driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);

                ScrapeCourseInfo courseInfoScraper = new ScrapeCourseInfo(driver);

                if (response2 == 1) {
                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                    courseInfoScraper.setFileLocation("src/fallwinter20172018-Test.txt");
                } else if (response2 == 2) {
                    courseInfoScraper.setSession("Summer 2018");
                    courseInfoScraper.setFileLocation("src/summer2018-Test.txt");
                }
                courseInfoScraper.startConnection();

            }

            System.out.println("Scrape finished!");

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }
}
