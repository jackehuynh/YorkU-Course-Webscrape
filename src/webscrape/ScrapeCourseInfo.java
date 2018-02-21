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
import java.io.File;
import java.io.FileWriter;

/*
    Scrape courses AND course description
 */
public class ScrapeCourseInfo {

    private WebDriver driver;
    private String absHref;
    private List<WebElement> courseCode;
    private List<WebElement> courseTitle;
    private WebElement select;
    private WebElement select2;
    private WebElement submitCourse;
    private Select courseSelect;
    private Select sessionSelect;
    private int courseCounter = 0;
    private File fileLocation = new File("C:/Users/Jackeh/Desktop/courseinfo.txt");
    private PrintWriter printWriter;

    public ScrapeCourseInfo(WebDriver driver) throws IOException {
        this.driver = driver;
    }

    public String getHref() {
        return this.absHref;
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

    public void startConnection() throws IOException { // initialize first connection
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get(); // first connection to site
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        this.setabsHref(absHref);
        /*
        To-Do: finish rewrite above with Selenium api as opposed to the JSoup one in the above to keep 
        code consistent with everything else
        
        driver.get("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm");
        WebElement ulBodyText = driver.findElement(By.tagName("ul"));
        WebElement ulBodyText2 = ulBodyText.findElement(By.name("bodytext"));
        WebElement ulBodyText3 = ulBodyText2.findElement(By.tagName("li"));
        WebElement ulBodyText4 = ulBodyText3.findElement(By.tagName("href"));
         */

        this.secondConnection();
    }

    public void secondConnection() throws IOException { // initialize second connection @ course/session page

        //another connection to go through the site      
        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        sessionSelect = new Select(select);  // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText("Summer 2018"); // selects the 'Summer 2018' option

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
                System.out.println("Loop is at: " + j);

                select2 = driver.findElement(By.name("subjectPopUp"));
                courseSelect = new Select(select2);
                courseSelect.selectByValue(j);
                System.out.println("Selecting course number: " + j);

                submitCourse = driver.findElement(By.name("3.10.7.5"));
                submitCourse.click();
                System.out.println("Clicking course at loop: " + j);

                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                printWriter.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                this.printCourses();
            }

            driver.navigate().back(); // simulates button press on 'back' button in the head-less browser
        }
//        printWriter.println("Courses offered in Summer 2018: " + courseCounter);
//        printWriter.println("Number of departments offering courses in Summer 2018: " + option.size());
//        printWriter.close();

        System.out.println("Courses offered in Summer 2018: " + courseCounter);
        System.out.println("Number of departments offering courses in Summer 2018: " + option.size());

    }

    public void printCourses() {
        courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        String[] result = new String[courseCode.size()];
        String[] courseInfo = new String[courseCode.size()];

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
//            printWriter.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = 0; i < courseCode.size(); i++) {
                result[i] = courseCode.get(i).getText() + " - " + courseTitle.get(i).getText();
                int j = 0;
                driver.findElements(By.cssSelector("td[width='30%']")).get(j).click();

//                driver.findElement(By.xpath("/html[1]/body[1]/table[1]/tbody[1]/tr[2]/td[2]/table[1]/tbody[1]/tr[2]/td[1]/table[1]/tbody[1]/tr[1]/td[1]/p[2]/b[1]")).getText();
//                driver.findElement(By.xpath("/html[1]/body[1]/table[1]/tbody[1]/tr[2]/td[2]/table[1]/tbody[1]/tr[2]/td[1]/table[1]/tbody[1]/tr[1]/td[1]/p[2]/b[1]")).toString();
//                System.out.println(result[i]);
//                printWriter.println(result[i]);
            }
        }

        courseCounter += courseCode.size();

//        printWriter.println("Number of courses offered in this department: " + courseCode.size());
//        printWriter.println("----------------------------------------------------------------------------------------------");
        System.out.println("Number of courses offered in this department: " + courseCode.size());
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    public void connectToSubjectSection() {
        driver.get(absHref); // connects to 'Subject' site
        WebElement select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        Select sessionSelect = new Select(select);  // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText("Summer 2018"); // selects the 'Summer 2018' option
    }
}