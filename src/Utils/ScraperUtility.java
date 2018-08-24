package webscrape;

import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.awt.AWTException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ScraperHelper {

    /*
    Helper class for various scrapers to access helper methods & variables
     */
//    private static ChromeDriver webDriver;
//
//    ChromeDriver getDriver() {
//        return this.webDriver;
//    }

    static ChromeDriver createDriver(String input) {
        ChromeOptions chromeOptions = new ChromeOptions();

        if (input.equals("headless") || input.equals("Headless")) {
            chromeOptions.addArguments("--headless");
        } else {
            throw new InputMismatchException("pass in only strings 'headless' or 'Headless' for headless driver, "
                    + "else leave blank for GUI driver");
        }

        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.setProxy(null);
        ChromeDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);

        return webDriver;
    }

    static ChromeDriver createDriver() {
        ChromeDriver webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);

        return webDriver;
    }

    public void connectToSubjectSelection(ChromeDriver webDriver) throws IOException, AWTException {
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm")
                .userAgent("Mozilla")
                .get();

        Elements result = doc.select("ul.bodytext").select("a[href]");
        String link = result.attr("abs:href");

        webDriver.get(link);
    }

    public WebElement getSessionButtonElement(ChromeDriver webDriver) {
        // locate the HTML element for the season session (Fall/Winter or Summer).
        WebElement sessionButton = webDriver.findElement(By.name("sessionPopUp"));
        return sessionButton;
    }

    public void selectSchoolSession(String session, ChromeDriver webDriver) {
        // selects the 'given session (Fall/Winter or Summer)' option/button
        
        WebElement sessionButton = getSessionButtonElement(webDriver);

        Select sessionSelect = new Select(sessionButton);

        sessionSelect.selectByVisibleText(session);
    }

}
