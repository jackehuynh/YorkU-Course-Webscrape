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
import java.awt.AWTException;
import java.io.File;
import java.io.FileWriter;
import org.openqa.selenium.chrome.ChromeDriver;

// Scrape courses AND course description via GUI browser (testing purposes only)
public class ScrapeCourseInfo {

    protected String absHref, session;
    protected WebElement select, select2, submitCourse;
    protected ChromeDriver driver;
    protected Select courseSelect, sessionSelect;
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

    public String getHref() {
        return this.absHref;
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

    public void returnToSubject() throws IOException {
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get(); // first
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        this.setabsHref(absHref);
        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        sessionSelect = new Select(select); // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText(session); // selects the 'given session' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> option = select2.findElements(By.tagName("option"));
        courseSelect = new Select(select2);
        submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button

    }
}

    /*
    IGNORE EVERYTHING BELOW THIS LINE, WORK IN PROGRESS...
    
//    public void findCourseTimetableElements() {
//        List<WebElement> locateFirstTable = driver.findElements(By.cssSelector("table[width='100%']")); // finds the section in the HTML page that encapsulates all the course tables
//        WebElement locateCourseTables = locateFirstTable.get(4); // grabs the specific table element corresponding to the course timetable
//    }
    public void startConnection() throws IOException, AWTException { // initialize first connection
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get(); // first connection to site
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        this.setabsHref(absHref);
        
		 * To-Do: finish rewrite above with Selenium api as opposed to the JSoup one in
		 * the above to keep code consistent with everything else
		 * 
		 * driver.get("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm"); WebElement
		 * ulBodyText = driver.findElement(By.tagName("ul")); WebElement ulBodyText2 =
		 * ulBodyText.findElement(By.name("bodytext")); WebElement ulBodyText3 =
		 * ulBodyText2.findElement(By.tagName("li")); WebElement ulBodyText4 =
		 * ulBodyText3.findElement(By.tagName("href"));
         
        this.secondConnection();
    }

    public void secondConnection() throws IOException, AWTException { // initialize second connection @ course/session
        // page

        // another connection to go through the site
        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp")); // find HTML/CSS selector name="sessionPopUp"
        sessionSelect = new Select(select); // create Select object with WebElement 'select' passed through
        sessionSelect.selectByVisibleText(session); // selects the 'given session' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> option = select2.findElements(By.tagName("option"));
        courseSelect = new Select(select2);

        printWriter = new PrintWriter(new FileWriter(fileLocation));

        submitCourse = driver.findElement(By.name("3.10.7.5")); // finds CSS selector element for 'Choose course' button

        // For-loop that clicks through all the course options in the list
        for (int i = 0; i < option.size(); i++) {
            select2 = driver.findElement(By.name("subjectPopUp"));
            List<WebElement> options = select2.findElements(By.tagName("option"));

            System.out.println(i + 1 + ")" + " --> " + options.get(i).getText());
            printWriter.println(options.get(i).getText());

            for (int k = i; k < i + 1; k++) {
                String j = Integer.toString(k);
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

    public void printCourses() throws AWTException, IOException {
        List<WebElement> courseCode = getCourseCodeElement();
//        List<WebElement> courseTitle = getCourseTitleElement();
//        String[] courseInfo = new String[courseCode.size()];

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
            // printWriter.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = 0; i < courseCode.size(); i++) {

                String[] result = new String[courseCode.size()];
                result[i] = printCourseCode(i) + " - " + printCourseTitle(i);
                System.out.println(result[i]);
                printWriter.println(result[i]);

                if (i == 0) {
                    clickOnCourseScheduleLink(i);
                } else {
                    clickOnCourseScheduleLink(i + i);
                }

                System.out.println("[" + getCourseDescription() + "]");

                List<WebElement> locateFirstTable = driver.findElements(By.cssSelector("table[width='100%']")); // finds the section in the HTML page (the main table) that encapsulates all the course tables
//                WebElement locateCourseTables = locateFirstTable.get(4); // grabs the specific table element corresponding to the course timetable
//                List<WebElement> singleTable = locateCourseTables.findElements(By.tagName("tr"));

//                List<WebElement> getTermAndSection = driver.findElements(By.cssSelector("font[color='#ffffff']")); // gets the HTML element stating the Term & Section
//                List<WebElement> getSectionDirector = driver.findElements(By.cssSelector("td[colspan='3']"));
                int b = 1;
                for (int x = 0; x < getTermAndSectionElement().size(); x++) {
                    setCourseColumnCounter(x);
                    System.out.println("Availability: " + printTermAndSection(x)); // prints term & section
                    if (x == 0) {
                        if (getSectionDirectorElement(1).isEmpty()) {
                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");
                        } else {
//                            String director = getSectionDirector.get(1).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + printSectionDirector(1));

                            int z = 0;
                            for (int k = 1; k < getClassTypeSize(x); k++) {
                                setCourseColumnCounter2(k);

                                System.out.println("Course Type: " + printClassType(k, x));
                                System.out.println("CourseColmunCounter: " + getCourseColumnCounter());
//                                List<WebElement> days = courseColumnWithDaysAndTimes().get(k).findElements(By.cssSelector("td[width='15%']")); // Day category
//                                List<WebElement> times = courseColumn.get(k).findElements(By.cssSelector("td[width='20%']")); // Start Time Category
//                                WebElement classLocation = courseColumn.get(k).findElement(By.cssSelector("td[width='45%']"));
//                                List<WebElement> instructor = getCourseTable().get(x).findElements(By.tagName("a"));
                                List<WebElement> catCode = getCourseTable().get(x).findElements(By.cssSelector("td[width='20%']"));

                                System.out.print("Days: " + getClassDays().get(0).getText());
                                System.out.print(" Times: " + getClassTimes().get(0).getText());
                                System.out.print(" Duration: " + getClassTimes().get(1).getText());
                                System.out.println(" Location: " + getClassLocation().getText());
                                System.out.print("Cat #: " + catCode.get(6).getText());
                                System.out.println(" Instructor: " + getCourseInstructorElement(x).get(z).getText());
                                z++;
                            }
                            System.out.println("------------------------------");
                        }
                    } else {

                        if (getSectionDirectorElement(b).isEmpty()) {
                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");
                        } else {
                            System.out.println("Section Director: " + printSectionDirector(b));

//                            List<WebElement> classType = getCourseTable().get(x).findElements(By.cssSelector("td[width='10%']"));
                            int z = 0;
                            for (int k = 1; k < getClassTypeSize(x); k++) {
                                setCourseColumnCounter2(k);
                                System.out.println("Course Type: " + printClassType(k, x));
//                                System.out.println("CourseColumnCounter: " + getCourseColumnCounter());
//                                System.out.println("CourseColumnCounter2: " + getCourseColumnCounter2());
//                                System.out.println("Counter of x in for-loop: " + x);
//                                System.out.println("Counter of k in for-loop: " + k);

//                                List<WebElement> days = courseColumnWithDaysAndTimes().get(k).findElements(By.cssSelector("td[width='15%']")); // Day category
                                List<WebElement> catCode = getCourseTable().get(x).findElements(By.cssSelector("td[width='20%']"));
//                                List<WebElement> catCode = getCourseInfo.get(x).findElements(By.tagName("tr"));
//                                WebElement catCode2 = catCode.get(3).findElement(By.cssSelector("td[width='20%']"));

                                System.out.print("Days: " + getClassDays().get(0).getText());
                                System.out.print(" Times: " + getClassTimes().get(0).getText());
                                System.out.print(" Duration: " + getClassTimes().get(1).getText());
                                System.out.println(" Location: " + getClassLocation().getText());
                                System.out.print("Cat #: " + catCode.get(6).getText());
                                System.out.println(" Instructor: " + getCourseInstructorElement(x).get(z).getText());
                                z++;
                            }
                            System.out.println("------------------------------");
                        }
                    }
                    b += 2;
                }
                keepTrack2 = i;
                keepTrack2++;
                driver.navigate().back();
                driver.navigate().refresh();
//                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
//                    System.out.println("Page timed out"); // message to let me know if page has timed out
//                    this.tempFix();
//                    break;
//                }
            }
        }
//        courseCounter += courseCode.size();
//        System.out.println("Number of courses offered in this department: " + courseCode.size());
    }
}

    public void tempFix() throws IOException {
        this.returnToSubject();
        select2 = driver.findElement(By.name("subjectPopUp"));
        courseSelect = new Select(select2);
        String j = Integer.toString(keepTrack);
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
            for (int i = keepTrack2; i < courseCode.size(); i++) {
                courseCode = driver.findElements(By.cssSelector("td[width='16%']"));
                courseTitle = driver.findElements(By.cssSelector("td[width='24%']"));

                result[i] = courseCode.get(i).getText() + " - " + courseTitle.get(i).getText();
                System.out.println(result[i]);
                printWriter.println(result[i]);

                if (i == 0) {
                    driver.findElements(By.cssSelector("td[width='30%']")).get(i).click();
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
                    System.out.println("Availability: " + getTermAndSection.get(x).getText()); // prints term & section
                    List<WebElement> getCourseInfo = driver.findElements(By.cssSelector("table[border='2']"));
                    List<WebElement> tableForCourse = getCourseInfo.get(x).findElements(By.cssSelector("table[border='0']"));
                    if (x == 0) {
                       
                        if (getSectionDirector.get(1).findElements(By.cssSelector("a[href^='/Apps/']")).isEmpty()) {

                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");

                        } else if (!getSectionDirector.get(1).findElements(By.tagName("a")).isEmpty() && !tableForCourse.get(1).findElements(By.cssSelector("td[width='15%']")).isEmpty()) {

                            String director = getSectionDirector.get(1).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + director);

                            List<WebElement> classType = getCourseInfo.get(x).findElements(By.cssSelector("td[width='10%']"));                  

                            int z = 0;
                            for (int k = 1; k < classType.size(); k++) {
                                System.out.println("Course Type: " + classType.get(k).getText());

                                List<WebElement> days = tableForCourse.get(k).findElements(By.cssSelector("td[width='15%']")); // Day category
                                List<WebElement> times = tableForCourse.get(k).findElements(By.cssSelector("td[width='20%']")); // Start Time Category
                                WebElement classLocation = tableForCourse.get(k).findElement(By.cssSelector("td[width='45%']"));
                                List<WebElement> instructor = getCourseInfo.get(x).findElements(By.tagName("a"));
                                List<WebElement> catCode = getCourseInfo.get(x).findElements(By.tagName("tr"));
                                List<WebElement> catCode2 = catCode.get(k).findElements(By.cssSelector("td[width='20%']"));

                                System.out.print("Days: " + days.get(0).getText());
                                System.out.print(" Times: " + times.get(0).getText());
                                System.out.print(" Duration: " + times.get(1).getText());
                                System.out.println(" Location: " + classLocation.getText());
                                System.out.print("Cat # : " + catCode.get(6).getText());
                                System.out.println(" Instructor: " + instructor.get(z).getText());
                                z++;
                            }
                            System.out.println("------------------------------");
                        }
                    } else {
                        if (getSectionDirector.get(b).findElements(By.cssSelector("a[href^='/Apps/']")).isEmpty()) {

                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");

                        } else if (!getSectionDirector.get(b).findElements(By.tagName("a")).isEmpty() && !tableForCourse.get(1).findElements(By.cssSelector("td[width='15%']")).isEmpty()) {

                            String director = getSectionDirector.get(b).findElement(By.tagName("a")).getText();
                            System.out.println("Section Director: " + director);

                            List<WebElement> classType = getCourseInfo.get(x).findElements(By.cssSelector("td[width='10%']"));

                            int z = 0;
                            for (int k = 1; k < classType.size(); k++) {
                                System.out.println("Course Type: " + classType.get(k).getText());

                                List<WebElement> days = tableForCourse.get(k).findElements(By.cssSelector("td[width='15%']")); // Day category
                                List<WebElement> times = tableForCourse.get(k).findElements(By.cssSelector("td[width='20%']")); // Start Time Category
                                WebElement classLocation = tableForCourse.get(k).findElement(By.cssSelector("td[width='45%']"));
                                List<WebElement> catCode = getCourseInfo.get(k).findElements(By.cssSelector("td[width='20%']"));
                                List<WebElement> instructor = getCourseInfo.get(x).findElements(By.tagName("a"));

                                System.out.println("tr tag size: " + catCode.size());

                                System.out.print("Days: " + days.get(0).getText());
                                System.out.print(" Times: " + times.get(0).getText());
                                System.out.print(" Duration: " + times.get(1).getText());
                                System.out.println(" Location: " + classLocation.getText());
                                System.out.print("Cat # : " + catCode.get(6).getText());
                                System.out.println(" Instructor: " + instructor.get(z).getText());
                                z++;
                            }
                            System.out.println("------------------------------");
                        }
                    }
                    b += 2;
                }

                keepTrack2 = i;
                keepTrack2++;
                driver.navigate().back();
                driver.navigate().refresh();
                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
                    System.out.println("Page timed out"); // message to let me know if page has timed out
                    this.tempFix();
                    break;
                }
            }
        }
    }
        public void findCourseTableElements() {
        List<WebElement> days = tableForCourse.get(k).findElements(By.cssSelector("td[width='15%']")); // Day category
        List<WebElement> times = tableForCourse.get(k).findElements(By.cssSelector("td[width='20%']")); // Start Time Category
        WebElement classLocation = tableForCourse.get(k).findElement(By.cssSelector("td[width='45%']"));
        List<WebElement> instructor = getCourseInfo.get(x).findElements(By.tagName("a"));
        List<WebElement> catCode = getCourseInfo.get(x).findElements(By.tagName("tr"));
        List<WebElement> catCode2 = catCode.get(k).findElements(By.cssSelector("td[width='20%']"));
    }
*/
