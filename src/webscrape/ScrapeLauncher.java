package webscrape;

import java.awt.AWTException;
import static utils.ScraperUtility.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScrapeLauncher {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        /*
        Start SubjectScraper.java, CourseScraper.java, ACTScraper.java, and DB.java here
        Populate db
         */
        ChromeDriver webDriver = createDriver("Headless");
        SubjectScraper scrapeSubjects = new SubjectScraper("Fall/Winter 2018-2019", "2018", webDriver);

        try {
            scrapeSubjects.startConnection();
            scrapeSubjects.writeToFile("src/textfiles/subjects.txt");
        } catch (AWTException e) {
        } finally {
            webDriver.quit();
        }

        CourseScraper scrapeCourses = new CourseScraper("2018", "FW");
        scrapeCourses.scrapeCourseList();
        scrapeCourses.writeCourseList("src/textfiles/courseList.txt");
        scrapeCourses.writeCourseDescription("src/textfiles/courseDescriptions.txt");

        descriptionScraper();
    }

    public static void descriptionScraper() throws FileNotFoundException, UnsupportedEncodingException, IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/textfiles/courseDescriptions.txt"), "UTF-8"))) {

            String line;
            String courseDescPattern = "\\{(.*?)\\}";
            String courseTitlePattern = "-(.*?)\\{";
            String courseCodePattern = "([^-]+)";

            Pattern courseCodePat = Pattern.compile(courseCodePattern);

            Pattern courseDescriptionPat = Pattern.compile(courseDescPattern);

            Pattern courseTitlePat = Pattern.compile(courseTitlePattern);

            while ((line = reader.readLine()) != null) {
                Matcher codeMatch = courseCodePat.matcher(line);
                Matcher descriptionMatch = courseDescriptionPat.matcher(line);
                Matcher titleMatch = courseTitlePat.matcher(line);

                while (codeMatch.find() && titleMatch.find() && descriptionMatch.find()) {
                    String courseCode = codeMatch.group(1);
                    String courseTitle = titleMatch.group(1);
                    String courseDescription = descriptionMatch.group(1);

//                    System.out.println(courseCode);
//                    System.out.println(courseTitle);
//                    System.out.println(courseDescription);
                }
            }
        }
    }
}
