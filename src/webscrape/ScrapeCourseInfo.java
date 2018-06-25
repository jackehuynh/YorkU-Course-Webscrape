package webscrape;

import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.io.PrintWriter;
import java.io.File;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScrapeCourseInfo {

    protected String absHref, session;
    protected WebElement select, select2, submitCourse;
    protected ChromeDriver driver;
    protected Select sessionSelect;
    protected File fileLocation;
    protected PrintWriter printWriter;
    protected int keepTrack, keepTrack2, courseCounter, classTypeSize;
    protected int courseColumnCounter; // this is the x counter in for-loop
    protected int courseColumnCounter2; // this is the k counter in for-loop

    public void setDriver(ChromeDriver driver) {
        this.driver = driver;
    }

    public void setFileLocation(String location) {
        fileLocation = new File(location);
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setKeepTrack(int a) { // keeps track of where the array counter is at
        keepTrack = a;
    }

    public void setKeepTrack2(int a) {
        keepTrack2 = a;
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

    public void setCourseColumnCounter(int counter) {
        this.courseColumnCounter = counter; // x
    }

    public void setCourseColumnCounter2(int counter) {
        this.courseColumnCounter2 = counter; // k
    }

    public int getCourseColumnCounter() {
        return this.courseColumnCounter;
    }

    public int getCourseColumnCounter2() {
        return this.courseColumnCounter2;
    }

    public String getCourseDescription() {
        String description = driver.findElements(By.tagName("p")).get(3).getText();
        return description;
    }

    public List<WebElement> getCourseCodeElement() {
        List<WebElement> courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        return courseCode;
    }

    public List<WebElement> getCourseTitleElement() {
        List<WebElement> courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        return courseTitle;
    }

    public String printCourseCode(int index) {
        List<WebElement> courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
        return courseCode.get(index).getText();
    }

    public String printCourseTitle(int index) {
        List<WebElement> courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));
        return courseTitle.get(index).getText();
    }

    public void clickOnCourseScheduleLink(int index) {
        driver.findElements(By.cssSelector("td[width='30%']")).get(index).click();
    }

    public List<WebElement> getSectionDirectorElement(int index) {
        List<WebElement> locateSectionDirectorElement = driver.findElements(By.cssSelector("td[colspan='3']"));
        List<WebElement> getSectionDirector = locateSectionDirectorElement.get(index).findElements(By.cssSelector("a[href^='/Apps/']"));
        return getSectionDirector;
    }

    public String printSectionDirector(int index) {
        List<WebElement> getSectionDirector = driver.findElements(By.cssSelector("td[colspan='3']"));
        String sectionDirector = getSectionDirector.get(index).findElement(By.tagName("a")).getText();
        return sectionDirector;
    }

    public List<WebElement> getMainCourseTable() {
        List<WebElement> locateFirstTable = driver.findElements(By.cssSelector("table[width='100%']")); // finds the section in the HTML page (the main table) that encapsulates all the course tables
        return locateFirstTable;
    }

    public List<WebElement> getTermAndSectionElement() {
        List<WebElement> getTermAndSection = driver.findElements(By.cssSelector("font[color='#ffffff']")); // gets the HTML element that has the Term & Section text
        return getTermAndSection;
    }

    public String printTermAndSection(int index) {
        return getTermAndSectionElement().get(index).getText();
    }

    public List<WebElement> getCourseTable() {
        List<WebElement> getCourseTable = driver.findElements(By.cssSelector("table[border='2']"));
        return getCourseTable;
    }

    public List<WebElement> getCourseInstructorElement(int index) {
        List<WebElement> instructor = getCourseTable().get(index).findElements(By.tagName("a"));
        return instructor;
    }

    public List<WebElement> courseColumnWithDaysAndTimes(int index) {
        List<WebElement> courseColumn = getCourseTable().get(index).findElements(By.cssSelector("table[border='0']"));
        return courseColumn;
    }

    public List<WebElement> courseColumnWithDaysAndTimes() {
        List<WebElement> courseColumn = getCourseTable().get(courseColumnCounter).findElements(By.cssSelector("table[border='0']"));
        return courseColumn;
    }

    public List<WebElement> getClassDays() {
        List<WebElement> classDays = courseColumnWithDaysAndTimes().get(courseColumnCounter2).findElements(By.cssSelector("td[width='15%']"));
        return classDays;
    }

    public List<WebElement> getClassTimes() {
        List<WebElement> classTimes = courseColumnWithDaysAndTimes().get(courseColumnCounter2).findElements(By.cssSelector("td[width='20%']"));
        return classTimes;
    }

    public WebElement getClassLocation() {
        WebElement classLocation = courseColumnWithDaysAndTimes().get(courseColumnCounter2).findElement(By.cssSelector("td[width='45%']"));
        return classLocation;
    }

    public String printClassType(int indexForClassType, int indexForCourseTable) {
        List<WebElement> classType = getCourseTable().get(indexForCourseTable).findElements(By.cssSelector("td[width='10%']"));
        return classType.get(indexForClassType).getText();
    }

    public int getClassTypeSize(int size) {
        List<WebElement> classType = getCourseTable().get(size).findElements(By.cssSelector("td[width='10%']"));
        return classType.size();
    }
    
    public String getHref() {
        return this.absHref;
    }

    public void returnToSubject() throws IOException {
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        Elements result = doc.select("ul.bodytext")
                             .select("a[href]");
        absHref = result.attr("abs:href");
        this.setabsHref(absHref);
        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        sessionSelect = new Select(select); // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText(session); // selects the 'given session' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        Select courseSelect = new Select(select2);
        submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button

    }
}
