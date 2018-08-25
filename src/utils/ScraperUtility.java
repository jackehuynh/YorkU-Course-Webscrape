package utils;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.awt.AWTException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class ScraperUtility {

    private final static String COURSE_SITE = "https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm";

    // Prevent instantiation of this class
    private ScraperUtility() {

    }

    public static ChromeDriver createDriver(String input) {
        ChromeOptions chromeOptions = new ChromeOptions();

        if (input.equals("headless") || input.equals("Headless")) {
            chromeOptions.addArguments("--headless");
        } else {
            throw new InputMismatchException("pass in only strings 'headless' or 'Headless' for headless driver, "
                    + "else leave blank for GUI driver");
        }

        System.setProperty("webdriver.chrome.driver", "linuxchromedriver.exe");

//        chromeOptions.addArguments("--window-size=1920,1080");
//        chromeOptions.addArguments("--disable-gpu");
//        chromeOptions.addArguments("--disable-extensions");
//        chromeOptions.setExperimentalOption("useAutomationExtension", false);
//        chromeOptions.addArguments("--proxy-server='direct://'");
//        chromeOptions.addArguments("--proxy-bypass-list=*");
//        chromeOptions.addArguments("--start-maximized");
        ChromeDriver webDriver = new ChromeDriver(chromeOptions);

        webDriver.manage().timeouts().implicitlyWait(400, TimeUnit.MILLISECONDS);

        return webDriver;
    }

    public static ChromeDriver createDriver() {

        System.setProperty("webdriver.chrome.driver", "linuxchromedriver.exe");

        ChromeDriver webDriver = new ChromeDriver();

        webDriver.manage().timeouts().implicitlyWait(400, TimeUnit.MILLISECONDS);

        return webDriver;
    }

    public static void connectToSubjectSelection(ChromeDriver webDriver) throws IOException, AWTException {
        Document doc = Jsoup.connect(COURSE_SITE)
                .userAgent("Mozilla")
                .get();

        Elements result = doc.select("ul.bodytext").select("a[href]");
        String link = result.attr("abs:href");

        webDriver.get(link);
    }

    public static void selectSchoolSession(String session, ChromeDriver webDriver) {
        // selects the 'given session (Fall/Winter or Summer)' option/button

        WebElement sessionButton = getSessionButtonElement(webDriver);

        Select sessionSelect = new Select(sessionButton);

        sessionSelect.selectByVisibleText(session);
    }

    public static WebElement getSessionButtonElement(ChromeDriver webDriver) {
        // locate the HTML element for the season session (Fall/Winter or Summer).

        WebElement sessionButton = webDriver.findElement(By.name("sessionPopUp"));

        return sessionButton;
    }

    public static String printCourseCode(int index, ChromeDriver webDriver) {
        List<WebElement> courseCode = webDriver.findElements(By.cssSelector("td[width='16%']"));
        return courseCode.get(index).getText();
    }

    public static String printCourseTitle(int index, ChromeDriver webDriver) {
        List<WebElement> courseTitle = webDriver.findElements(By.cssSelector("td[width='24%']"));
        return courseTitle.get(index).getText();
    }

    public static List<WebElement> getCourseList(ChromeDriver webDriver) {
        List<WebElement> courseList = webDriver.findElements(By.cssSelector("td[width='16%']"));
        return courseList;
    }

}
