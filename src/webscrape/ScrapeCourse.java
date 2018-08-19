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
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ScrapeCourse extends ScrapeCourseInfo implements Runnable {

    private String link, threadMSG;
    private int start, counter;
    private Database db;

    public ScrapeCourse() {

    }

    public ScrapeCourse(String thread, int start, int end) {
        threadMSG = thread;
        setCounter(start, end);
    }

    public void setCounter(int val1, int val2) {
        start = val1;
        counter = val2;
    }

    @Override
    public void run() {
        try {
            db = new Database();

            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.setProxy(null);
            ChromeDriver driver = new ChromeDriver(chromeOptions);
            driver.manage().timeouts().implicitlyWait(400, TimeUnit.MILLISECONDS);
            setDriver(driver);
            setSession("Fall/Winter 2018-2019");

            Thread.sleep(3000);

            startConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(ScrapeCourse.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Shutting down drivers of " + threadMSG);
            driver.quit();
            try {
                db.closeConn();
            } catch (SQLException ex) {
                Logger.getLogger(ScrapeCourse.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getLink() {
        return this.link;
    }

    public int getStart() {
        return this.start;
    }

    public int getCounter() {
        return this.counter;
    }

    public void startConnection() throws IOException, AWTException, SQLException { // traverse to course subject page
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm")
                .userAgent("Mozilla")
                .get();
        Elements result = doc.select("ul.bodytext")
                .select("a[href]");
        link = result.attr("abs:href");
        driver.get(link);
        select = driver.findElement(By.name("sessionPopUp")); // locate the HTML element for the season session (Fall/Winter or Summer).
        sessionSelect = new Select(select);
        sessionSelect.selectByVisibleText(session); // selects the 'given session (Fall/Winter or Summer)' option

        // Initial click of the default selected subject in the list, which is 'ACTG - Accounting - ( SB )'
        select2 = driver.findElement(By.name("subjectPopUp"));
        //List<WebElement> option = select2.findElements(By.tagName("option"));

        Select courseSelect = new Select(select2);
        submitCourse = driver.findElement(By.name("3.10.7.5"));

        int id = start + 1;

        /* loop that clicks through all the course subjects in the list */
        for (int i = getStart(); i < getCounter(); i++) {
            select2 = driver.findElement(By.name("subjectPopUp"));
            List<WebElement> options = select2.findElements(By.tagName("option"));
            String subject = options.get(i).getText();
            System.out.println(subject);

            db.insertSubject(subject, id);
            for (int k = i; k < i + 1; k++) {
                String j = Integer.toString(k);
                keepTrack = k;
                this.setKeepTrack(keepTrack);

                select2 = driver.findElement(By.name("subjectPopUp"));
                courseSelect = new Select(select2);
                courseSelect.selectByValue(j);

                submitCourse = driver.findElement(By.name("3.10.7.5")); // finds html name of submit button on course page
                submitCourse.click();

                this.scrapeCourses();
            }
            this.returnToSubject();
        }
    }

    public void scrapeCourses() throws IOException, SQLException {
        List<WebElement> courseCode = getCourseCodeElement();

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = 0; i < courseCode.size(); i++) {
                String course = printCourseCode(i);
                String title = printCourseTitle(i);
                String code = "";

                System.out.println(course + " - " + title);

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

                if (i == 0) {
                    clickOnCourseScheduleLink(i);
                } else {
                    clickOnCourseScheduleLink(i + i);
                }
                String description = getCourseDescription();
                db.insertCourse(course, title, code, description);
                keepTrack2 = i;
                keepTrack2++;
                driver.navigate().back();
                driver.navigate().refresh();
                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
                    System.out.println("Page timed out"); // message to let me know if page has timed out
                    this.pageTimeOutFix();
                    break;
                }
            }
        }
    }

    // Janky method to restart the loop should the web page experiences a time out from the browser
    public void pageTimeOutFix() throws IOException, SQLException {

        this.returnToSubject();
        select2 = driver.findElement(By.name("subjectPopUp"));
        Select courseSelect = new Select(select2);
        String j = Integer.toString(keepTrack);
        courseSelect.selectByValue(j);
        submitCourse = driver.findElement(By.name("3.10.7.5"));
        submitCourse.click();

        List<WebElement> courseCode = getCourseCodeElement();
        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = keepTrack2; i < courseCode.size(); i++) {
                String course = printCourseCode(i);
                String title = printCourseTitle(i);
                String code = "";

                System.out.println(course + " - " + title);

                if (course.length() == 15) { // Faculty code is only 2 letters (Ex: BC - Bethune College)
                    code = "" + course.charAt(3) + course.charAt(4);
                } else if (course.length() == 16) { // Faculty code is 3 letters (Ex: CAT, CCY, ...)
                    code = "" + course.charAt(3) + course.charAt(4) + course.charAt(5);
                } else if (course.length() == 17 || course.length() == 18) {
                    code = "" + course.charAt(3) + course.charAt(4) + course.charAt(5) + course.charAt(6); // Faculty code is 4/5 letters (Ex: EECS, BIOL, etc.)
                }

                if (i == 0) {
                    clickOnCourseScheduleLink(i);
                } else {
                    clickOnCourseScheduleLink(i + i);
                }
                String description = getCourseDescription();

                db.insertCourse(course, title, code, description);
                // System.out.println("[" + getCourseDescription() + "]");
                keepTrack2 = i;
                keepTrack2++;
                driver.navigate().back();
                driver.navigate().refresh();
                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
                    System.out.println("Page timed out"); // message to let me know if page has timed out
                    this.pageTimeOutFix();
                    break;
                }
            }
        }
    }
}