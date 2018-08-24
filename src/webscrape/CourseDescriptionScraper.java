package webscrape;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class CourseScraper extends ScraperHelper {

    public static void main(String[] args) throws IOException, AWTException {

        ChromeDriver webDriver = createDriver("Headless");

        SubjectScraper scrape = new SubjectScraper("Fall/Winter 2018-2019", "2018", webDriver);

        try {
            scrape.startConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String year;
    private String session;
    private ChromeDriver driver;
    String prefix = "https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm.woa/wa/crsq?fa="
            + "LE" + "&sj=" + "EECS" + "&cn=" + "1012" + "&cr=" + "3.00" + "&ay=" + "2018" + "&ss=" + "FW";

    public CourseScraper(String session, String year, ChromeDriver driver) {
        this.session = session;
        this.driver = driver;
        this.year = year;
    }

    /*
    Inside course page
     */
    public void scrapeCourse() {

        List<WebElement> courseCode = driver.findElements(By.cssSelector("td[width='16%']"));

        for (int i = 0; i < courseCode.size(); i++) {

            String course = courseCode.get(i).getText();
            String code = "";

            switch (course.length()) {
                case 15:
                    // Faculty code is only 2 letters (Ex: BC - Bethune College)
                    code = "" + course.charAt(3) + course.charAt(4);
                    break;
                case 16:
                    // Faculty code is 3 letters (Ex: CAT, CCY, ...)
                    code = "" + course.charAt(3) + course.charAt(4) + course.charAt(5);
                    break;
                case 17:
                case 18:
                    code = "" + course.charAt(3) + course.charAt(4) + course.charAt(5) + course.charAt(6); // Faculty code is 4/5 letters (Ex: EECS, BIOL, etc.)
                    break;
                default:
                    break;
            }

            System.out.println(code);
        }
    }
}
