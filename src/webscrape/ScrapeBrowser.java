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

/* To-Do:
   - implement scanner to allow scraping of any subject entered by user
   - improve/implement another class to manually scrape all subjects and display them separately
*/

public class ScrapeBrowser {

    private WebDriver driver;
    private String absHref;
    private String[] result;
    private List<WebElement> courseCode;
    private List<WebElement> courseTitle;

    public ScrapeBrowser(WebDriver driver) throws IOException {
        this.driver = driver;
    }

    public void startConnection() throws IOException { // initialize first connection
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get(); // first connection to site
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        /*
        To-Do: finish rewrite above with Selenium api as opposed to the JSoup one in the above to keep 
        code consistent with everything else
        
        driver.get("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm");
        WebElement ulBodyText = driver.findElement(By.tagName("ul"));
        WebElement ulBodyText2 = ulBodyText.findElement(By.name("bodytext"));
        WebElement ulBodyText3 = ulBodyText2.findElement(By.tagName("li"));
        WebElement ulBodyText4 = ulBodyText3.findElement(By.tagName("href"));
        */
        
        this.secondConnection(); // call next method
    }

    public void secondConnection() throws IOException { // initialize second connection @ course/session page
        driver.get(absHref);    // connects to 'Subject' site
        WebElement select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        Select sessionSelect = new Select(select);  // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText("Summer 2018"); // selects the 'Summer 2018' option

        WebElement select2 = driver.findElement(By.name("subjectPopUp"));
        Select courseSelect = new Select(select2);
        courseSelect.selectByValue("54"); // selects 'EECS course' option w/ HtmlUnitDriver
        //courseSelect.selectByVisibleText("EECS - Electrical Engineering and Computer Science - ( LE ) "); // selects 'EECS course' option w/ ChromeDriver

        WebElement submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button
        submitCourse.click();   // clicks 'Choose course' button

        courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        
        /*
        placeholder int value for result[]
        need to rewrite this part later..
        */
        result = new String[courseCode.size()];

        for (int i = 0; i < courseCode.size(); i++) {
            result[i] = courseCode.get(i).getText() + " - " + courseTitle.get(i).getText();
        }
        this.outputToFile();
    }

    public void outputToFile() throws FileNotFoundException {
        WriteToFile printToFile = new WriteToFile();
        printToFile.setResult(result);
        printToFile.printOutFile();
    }

}
