package webscrape;

import static utils.ScraperUtility.*;
import java.io.IOException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.awt.AWTException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.openqa.selenium.chrome.ChromeDriver;

public class SubjectScraper {

    public static void main(String[] args) throws IOException, AWTException {

//        ChromeDriver webDriver = createDriver("Headless");
        ChromeDriver webDriver = createDriver();
        SubjectScraper scrape = new SubjectScraper("Fall/Winter 2018-2019", "2018", webDriver);

        try {
            scrape.startConnection();
            scrape.writeToFile("src/textfiles/subjects.txt");
        } finally {
            webDriver.quit();
        }
    }

    private final String year;
    private final String session;
    private final ChromeDriver driver;
    private final List<String> subjects = new ArrayList<>();

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

    public ChromeDriver getDriver() {
        return this.driver;
    }

    public void startConnection() throws IOException, AWTException {

        connectToSubjectSelection(this.driver);
        selectSchoolSession(this.session, this.driver);

        WebElement subjectSelect = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> subjectTags = subjectSelect.findElements(By.tagName("option"));

        extractFacultyAndSubjects(subjectTags);
    }

    public void extractFacultyAndSubjects(List<WebElement> tags) {
        
        for (int i = 0; i < tags.size(); i++) {

            String s = tags.get(i).getText();

            System.out.println(s);
            subjects.add(s);
        }
    }

    public void writeToFile(String location) throws FileNotFoundException {
        File file = new File(location);

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String subject : subjects) {
                writer.println(subject);
            }
        }
    }
}
