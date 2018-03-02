package webscrape;

import java.io.FileNotFoundException;
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

/* To-Do:
 Added on Feb 18th:
 - implement scanner to allow scraping of any subject entered by user
 - improve/implement another class to manually scrape all subjects and display them separately (partially done - Feb 19th)
 */
public class ScrapeBrowser {

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
    private File fileLocation = new File("src/courseList.txt");
    private PrintWriter printWriter;
    private String session;

    public ScrapeBrowser(WebDriver driver) throws IOException {
        this.driver = driver;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return this.session;
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
        sessionSelect.selectByVisibleText(this.getSession()); // selects the 'Summer 2018' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> option = select2.findElements(By.tagName("option"));
        courseSelect = new Select(select2);

        printWriter = new PrintWriter(new FileWriter(fileLocation));

        /*
         Note to self: Will have to rewrite this entire thing at a later time...
         */
//        printToFile = new WriteToFile();
//        printToFile.setDeptSize(option.size());
//        String[] result = new String[option.size()];
//
//        for (int i = 0; i < option.size(); i++) {
//            result[i] = option.get(i).getText();
//        }
//        printToFile.setDeptArray(result);
        submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button

        // For-loop that clicks through all the course options in the list
        for (int i = 0; i < option.size(); i++) {
            select2 = driver.findElement(By.name("subjectPopUp"));
            List<WebElement> options = select2.findElements(By.tagName("option"));

            System.out.println(i + 1 + ")" + " --> " + options.get(i).getText());
            printWriter.println("*" + options.get(i).getText() + "\n");

            for (int k = i; k < i + 1; k++) {
                String j = Integer.toString(k);

                select2 = driver.findElement(By.name("subjectPopUp"));
                courseSelect = new Select(select2);
                courseSelect.selectByValue(j);

                submitCourse = driver.findElement(By.name("3.10.7.5"));
                submitCourse.click();
                System.out.println("Clicking course at loop: " + j);
                printWriter.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                this.printCourses();
            }

            driver.navigate().back(); // simulates button press on 'back' button in the head-less browser
        }
        printWriter.println("Courses offered in Summer 2018: " + courseCounter);
        printWriter.println("Number of departments offering courses in Summer 2018: " + option.size());
        printWriter.close();

        System.out.println("Courses offered in Summer 2018: " + courseCounter);
        System.out.println("Number of departments offering courses in Summer 2018: " + option.size());

    }

    public void printCourses() {
        courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        String[] result = new String[courseCode.size()];

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
            printWriter.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = 0; i < courseCode.size(); i++) {
                courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
                driver.findElements(By.cssSelector("td[width='30%']"));
                result[i] = courseCode.get(i).getText() + "\n" + courseTitle.get(i).getText();
                System.out.println(result[i]);
                printWriter.println(result[i]);

//                driver.findElements(By.cssSelector("td[width='30%']")).get(i).click();
//                driver.navigate().back();
//                driver.findElement(By.xpath("/html[1]/body[1]/table[1]/tbody[1]/tr[2]/td[2]/table[1]/tbody[1]/tr[2]/td[1]/table[1]/tbody[1]/tr[1]/td[1]/p[2]/b[1]")).getText();
//                driver.findElement(By.xpath("/html[1]/body[1]/table[1]/tbody[1]/tr[2]/td[2]/table[1]/tbody[1]/tr[2]/td[1]/table[1]/tbody[1]/tr[1]/td[1]/p[2]/b[1]")).toString();
            }
        }

        courseCounter += courseCode.size();
    }

    public void connectToSubjectSection() {
        driver.get(absHref); // connects to 'Subject' site
        WebElement select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        Select sessionSelect = new Select(select);  // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText("Summer 2018"); // selects the 'Summer 2018' option
    }
}
