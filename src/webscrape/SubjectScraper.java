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
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubjectScraper extends ScraperHelper {

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

    public SubjectScraper(String session, String year, ChromeDriver driver) {
        this.session = session;
        this.driver = driver;
        this.year = year;
    }

    public String getYear() {
        return this.year;
    }

    public String getSession() {
        return this.session;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void startConnection() throws IOException, AWTException, SQLException {
        connectToSubjectSelection(driver);
        selectSchoolSession(this.session, driver);

        WebElement subjectSelect = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> subjectTags = subjectSelect.findElements(By.tagName("option"));

        extractFacultyAndSubjects(subjectTags);
    }

    public void extractFacultyAndSubjects(List<WebElement> tags) {
        String pattern = "[^-]+";

        Pattern p = Pattern.compile(pattern);

        for (int i = 0; i < tags.size(); i++) {
            String s = tags.get(i).getText();
//            Matcher m = p.matcher(subjectTags.get(i).getText());
//            System.out.println(m.group(0));
            String subj = tags.get(i).getText().substring(0, 4);

            if (subj.contains("-")) {
                subj = tags.get(i).getText().substring(0, 3);
            }

            String subject = subj.trim();
            String faculty = s.substring(s.lastIndexOf('-') + 1).trim();

            System.out.println(subject + " " + faculty);
//            System.out.println(subjectTags.get(i).getText());
//              System.out.println(subj.split("[^-]+"));
        }
    }

}
