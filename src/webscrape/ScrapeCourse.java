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

public class ScrapeCourse extends ScrapeCourseInfo {

    public ScrapeCourse() {

    }

    public void connectionOne() throws IOException, AWTException {

        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        Elements result = doc.select("ul.bodytext");
        Elements result2 = result.select("a[href]");
        absHref = result2.attr("abs:href");
        this.setabsHref(absHref);
        this.connectionTwo();
    }

    public void connectionTwo() throws IOException, AWTException {

        driver.get(this.getHref());
        select = driver.findElement(By.name("sessionPopUp"));
        sessionSelect = new Select(select);
        sessionSelect.selectByVisibleText(session); // selects the 'given session (Fall/Winter or Summer)' option

        select2 = driver.findElement(By.name("subjectPopUp"));
        List<WebElement> option = select2.findElements(By.tagName("option"));
        courseSelect = new Select(select2);

        printWriter = new PrintWriter(new FileWriter(fileLocation));

        submitCourse = driver.findElement(By.name("3.10.7.5"));

        // loop that clicks through all the course subjects in the list
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

                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                printWriter.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                this.scrapeCourses();
            }
            this.returnToSubject();
        }
    }

    public void scrapeCourses() throws IOException {
        List<WebElement> courseCode = getCourseCodeElement();

        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
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
                int b = 1; // counter used for searching section director HTML element
                for (int x = 0; x < getTermAndSectionElement().size(); x++) {
                    setCourseColumnCounter(x);
                    System.out.println("Availability: " + printTermAndSection(x)); // prints term & section
                    if (x == 0) {
                        /*
                        It's finding the "a href" tag b/c if a course is available there'll be a href tag,
                        if a section is cancelled, there's usually no href tags.
                         */
                        if (getSectionDirectorElement(1).isEmpty()) {
                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");
                        } else {
                            System.out.println("Section Director: " + printSectionDirector(1));

                            int z = 0;
                            for (int k = 1; k < getClassTypeSize(x); k++) {
                                
                                setCourseColumnCounter2(k);
                                
                                List<WebElement> catCode = getCourseTable().get(x).findElements(By.cssSelector("td[width='20%']"));
                                
                                System.out.println("Course Type: " + printClassType(k, x));
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

                                List<WebElement> catCode = getCourseTable().get(x).findElements(By.cssSelector("td[width='20%']"));
                                
                                System.out.println("Course Type: " + printClassType(k, x));
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
                if (driver.findElements(By.cssSelector("td[width='16%']")).isEmpty()) {
                    System.out.println("Page timed out"); // message to let me know if page has timed out
                    this.pageTimeOutFix();
                    break;
                }
            }
        }
    }

    public void pageTimeOutFix() throws IOException {

        this.returnToSubject();
        select2 = driver.findElement(By.name("subjectPopUp"));
        courseSelect = new Select(select2);
        String j = Integer.toString(keepTrack);
        courseSelect.selectByValue(j);

        submitCourse = driver.findElement(By.name("3.10.7.5"));
        submitCourse.click();

        List<WebElement> courseCode = getCourseCodeElement();
        if (courseCode.isEmpty()) {
            System.out.println("----------NO COURSES FOUND----------");
        } else {
            for (int i = keepTrack2; i < courseCode.size(); i++) {

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
                int b = 1; // counter used for searching section director HTML element
                for (int x = 0; x < getTermAndSectionElement().size(); x++) {
                    setCourseColumnCounter(x);
                    System.out.println("Availability: " + printTermAndSection(x)); // prints term & section
                    if (x == 0) {
                        if (getSectionDirectorElement(1).isEmpty()) {
                            System.out.println("Section Cancelled OR This is an Online Course. Please check York's Website for more information");
                        } else {
                            System.out.println("Section Director: " + printSectionDirector(1));

                            int z = 0;
                            for (int k = 1; k < getClassTypeSize(x); k++) {

                                setCourseColumnCounter2(k);

                                List<WebElement> catCode = getCourseTable().get(x).findElements(By.cssSelector("td[width='20%']"));

                                System.out.println("Course Type: " + printClassType(k, x));
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
                    }
                    b += 2;
                }
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
