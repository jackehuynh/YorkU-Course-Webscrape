package webscrape;

import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.io.PrintWriter;
import java.awt.AWTException;
import java.io.File;
import java.io.FileWriter;
import org.openqa.selenium.chrome.ChromeDriver;

// Scrape courses AND course description via GUI browser (testing purposes only)
public class ScrapeCourseInfo {

    private String absHref, session;
    private List<WebElement> courseCode, courseTitle;
    private WebElement select, select2, submitCourse;
    private ChromeDriver driver;
    private Select courseSelect, sessionSelect;
    private File fileLocation;
    private PrintWriter printWriter;
    private int keepTrack = 0, keepTrack2 = 0, courseCounter = 0;

    public ScrapeCourseInfo(ChromeDriver driver) throws IOException {
        this.driver = driver;
    }

    public void setFileLocation(String location) {
        fileLocation = new File(location);
    }

    public String getHref() {
        return this.absHref;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return this.session;
    }

    public void setKeepTrack(int a) {
        keepTrack = a;
    }

    public void setKeepTrack2(int a) {
        keepTrack2 = a;
    }

    public int getTrack1() {
        return keepTrack;
    }

    public int getTrack2() {
        return keepTrack2;
    }

    public void setabsHref(String absHref) {
        this.absHref = absHref;
    }

    public void setCourseCounter(int counter) {
        this.courseCounter = counter;
    }

    public int getCourseCounter() {
        return this.courseCounter;
    }

    public void startConnection() throws IOException, AWTException { // initialize first connection
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get(); // first connection to site
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        this.setabsHref(absHref);
        /*
		 * To-Do: finish rewrite above with Selenium api as opposed to the JSoup one in
		 * the above to keep code consistent with everything else
		 * 
		 * driver.get("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm"); WebElement
		 * ulBodyText = driver.findElement(By.tagName("ul")); WebElement ulBodyText2 =
		 * ulBodyText.findElement(By.name("bodytext")); WebElement ulBodyText3 =
		 * ulBodyText2.findElement(By.tagName("li")); WebElement ulBodyText4 =
		 * ulBodyText3.findElement(By.tagName("href"));
         */
        this.secondConnection();
    }

    public void returnToSubject() throws IOException {
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get(); // first
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        this.setabsHref(absHref);
        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        sessionSelect = new Select(select); // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText(this.getSession()); // selects the 'given session' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> option = select2.findElements(By.tagName("option"));
        courseSelect = new Select(select2);
        submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button

    }

    public void secondConnection() throws IOException, AWTException { // initialize second connection @ course/session
        // page

        // another connection to go through the site
        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        sessionSelect = new Select(select); // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText(this.getSession()); // selects the 'given session' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> option = select2.findElements(By.tagName("option"));
        courseSelect = new Select(select2);

        printWriter = new PrintWriter(new FileWriter(fileLocation));

        submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button

        // For-loop that clicks through all the course options in the list
        for (int i = 0; i < option.size(); i++) {
            select2 = driver.findElement(By.name("subjectPopUp"));
            List<WebElement> options = select2.findElements(By.tagName("option"));

            System.out.println("Grabbing options, we are at option " + i + " in the list");
            System.out.println(i + 1 + ")" + " --> " + options.get(i).getText());
            printWriter.println(options.get(i).getText());

            for (int k = i; k < i + 1; k++) {
                String j = Integer.toString(k);
                // System.out.println("Loop is at: " + j);
                keepTrack = k;
                this.setKeepTrack(keepTrack);

                select2 = driver.findElement(By.name("subjectPopUp"));
                courseSelect = new Select(select2);
                courseSelect.selectByValue(j);

                submitCourse = driver.findElement(By.name("3.10.7.5"));
                submitCourse.click();

                // System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                printWriter.println(
                        "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                this.printCourses();
            }
            this.returnToSubject();
        }
        // printWriter.println("Courses offered in Summer 2018: " + courseCounter);
        // printWriter.println("Number of departments offering courses in Summer 2018: "
        // + option.size());
        // printWriter.close();

        System.out.println("Courses offered in Summer 2018: " + courseCounter);
        System.out.println("Number of departments offering courses in Summer 2018: " + option.size());

    }

    public void tempFix() throws IOException {
        this.returnToSubject();
        select2 = driver.findElement(By.name("subjectPopUp"));
        courseSelect = new Select(select2);
        String j = Integer.toString(this.getTrack1());
        courseSelect.selectByValue(j);

        submitCourse = driver.findElement(By.name("3.10.7.5"));
        submitCourse.click();

        courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        String[] result = new String[courseCode.size()];
        String[] courseInfo = new String[courseCode.size()];

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
            // printWriter.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = this.getTrack2(); i < courseCode.size(); i++) {
                courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
                courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));

                result[i] = courseCode.get(i).getText() + " - " + courseTitle.get(i).getText();
                System.out.println(result[i]);
                printWriter.println(result[i]);

                if (i == 0) {
                    driver.findElements(By.cssSelector("td[width='30%']")).get(i + i).click();
                } else {
                    driver.findElements(By.cssSelector("td[width='30%']")).get(i + i).click();
                }

                String description = driver.findElements(By.tagName("p")).get(3).getText();
                System.out.println("[" + description + "]");
                printWriter.println("[" + description + "]");

                List<WebElement> locateFirstTable = driver.findElements(By.cssSelector("table[width='100%']")); // finds the section in the HTML page that encapsulates all the course tables
                WebElement locateCourseTables = locateFirstTable.get(4); // grabs the specific table element corresponding to the course timetable
                List<WebElement> singleTable = locateCourseTables.findElements(By.tagName("tr"));

                List<WebElement> getTermAndSection = driver.findElements(By.cssSelector("font[color='#ffffff']")); // gets the HTML element stating the Term & Section
                List<WebElement> getSectionDirector = driver.findElements(By.cssSelector("td[colspan='3']"));

                int b = 1;
                for (int x = 0; x < getTermAndSection.size(); x++) {
                    System.out.println(getTermAndSection.get(x).getText()); // prints term & section
                    if (x == 0) {
                        /*
                        It's finding the "a href" tag b/c if a course is available there'll be a href tag,
                        if a section is cancelled, there's usually no href tags.
                         */
                        if (!getSectionDirector.get(x + 1).findElements(By.tagName("a")).isEmpty()) {
                            String director = getSectionDirector.get(x + 1).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + director);
                        } else {
                            System.out.println("Section Cancelled OR This is an Online Course, Please check the York's Website for more information");
                        }
                    } else {
                        if (!getSectionDirector.get(b).findElements(By.tagName("a")).isEmpty()) {
                            String director = getSectionDirector.get(b).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + director);
                        } else {
                            System.out.println("Section Cancelled OR This is an Online Course, Please check the York's Website for more information");
                        }
                    }
                    b += 2;
                }
                keepTrack2 = i;
                keepTrack2++;
                driver.navigate().back();
                driver.navigate().refresh();
                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
                    this.tempFix();
                    break;
                }
            }
        }
    }

    public void printCourses() throws AWTException, IOException {
        courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        String[] result = new String[courseCode.size()];
        String[] courseInfo = new String[courseCode.size()];

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
            // printWriter.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = 0; i < courseCode.size(); i++) {
                courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
                courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));

                result[i] = courseCode.get(i).getText() + " - " + courseTitle.get(i).getText();
                System.out.println(result[i]);
                printWriter.println(result[i]);

                if (i == 0) {
                    driver.findElements(By.cssSelector("td[width='30%']")).get(i + i).click();
                } else {
                    driver.findElements(By.cssSelector("td[width='30%']")).get(i + i).click();
                }

                String description = driver.findElements(By.tagName("p")).get(3).getText();
                System.out.println("[" + description + "]");
                printWriter.println("[" + description + "]");

                List<WebElement> locateFirstTable = driver.findElements(By.cssSelector("table[width='100%']")); // finds the section in the HTML page that encapsulates all the course tables
                WebElement locateCourseTables = locateFirstTable.get(4); // grabs the specific table element corresponding to the course timetable
                List<WebElement> singleTable = locateCourseTables.findElements(By.tagName("tr"));

                List<WebElement> getTermAndSection = driver.findElements(By.cssSelector("font[color='#ffffff']")); // gets the HTML element stating the Term & Section
                List<WebElement> getSectionDirector = driver.findElements(By.cssSelector("td[colspan='3']"));

                int b = 1;
                for (int x = 0; x < getTermAndSection.size(); x++) {
                    System.out.println(getTermAndSection.get(x).getText()); // prints term & section
                    if (x == 0) {
                        /*
                        It's finding the "a href" tag b/c if a course is available there'll be a href tag,
                        if a section is cancelled, there's usually no href tags.
                         */
                        if (!getSectionDirector.get(x + 1).findElements(By.tagName("a")).isEmpty()) {
                            String director = getSectionDirector.get(x + 1).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + director);
                        } else {
                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");
                        }
                    } else {
                        if (!getSectionDirector.get(b).findElements(By.tagName("a")).isEmpty()) {
                            String director = getSectionDirector.get(b).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + director);
                        } else {
                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");
                        }
                    }
                    b += 2;
                }

                keepTrack2 = i;
                keepTrack2++;
                driver.navigate().back();
                driver.navigate().refresh();
                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
                    this.tempFix();
                    break;
                }
            }
        }
//        courseCounter += courseCode.size();
//        System.out.println("Number of courses offered in this department: " + courseCode.size());
//        System.out.println(
//                "----------------------------------------------------------------------------------------------");
    }

    public void connectToSubjectSection() {
        driver.get(absHref); // connects to 'Subject' site
        WebElement select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        Select sessionSelect = new Select(select); // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText("Summer 2018"); // selects the 'Summer 2018' option
    }
}
