package webscrape;

import java.awt.AWTException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        System.out.println("src/" + dateFormat.format(date) + "-Summer2018.txt"); //2016/11/16 12:08:43

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
                driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);

                ScrapeCourse courseInfoScraper = new ScrapeCourse();
                courseInfoScraper.setDriver(driver);

                if (response2 == 1) {
                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                    courseInfoScraper.setFileLocation("src/" + dateFormat.format(date) + ".txt");
                } else if (response2 == 2) {
                    courseInfoScraper.setSession("Summer 2018");
                    courseInfoScraper.setFileLocation("src/" + dateFormat.format(date) + "-Summer2018.txt");
                }
                courseInfoScraper.connectionOne();

            } else if (response == 2) {

                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                ChromeDriver driver = new ChromeDriver();   // GUI (Chrome) browser
                driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);

                ScrapeCourse courseInfoScraper = new ScrapeCourse();

                courseInfoScraper.setDriver(driver);

                if (response2 == 1) {
                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                    courseInfoScraper.setFileLocation("src/fallwinter20172018-Test.txt");
                } else if (response2 == 2) {
                    courseInfoScraper.setSession("Summer 2018");
                    courseInfoScraper.setFileLocation("src/summer2018-Test.txt");
                }
                courseInfoScraper.connectionOne();

            } else if (response == 3) {

                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                ChromeDriver driver = new ChromeDriver();   // GUI (Chrome) browser
                driver.manage().timeouts().implicitlyWait(1500, TimeUnit.MILLISECONDS);

                ACTScrape courseInfoScraper = new ACTScrape();
                courseInfoScraper.setDriver(driver);
                courseInfoScraper.connect();

                if (response2 == 1) {
//                    courseInfoScraper.setSession("Fall/Winter 2017-2018");
                    courseInfoScraper.setFileLocation("src/ACT-Test1.txt");
                } else if (response2 == 2) {
//                    courseInfoScraper.setSession("Summer 2018");
                    courseInfoScraper.setFileLocation("src/ACT-Test2.txt");
                }
            }

            System.out.println("Scrape finished!");

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }
}
