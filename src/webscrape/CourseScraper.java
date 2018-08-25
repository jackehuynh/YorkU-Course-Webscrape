package webscrape;

import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CourseScraper {

    public static void main(String[] args) throws UnsupportedEncodingException, IOException {

        CourseScraper scrape = new CourseScraper("2018", "FW");
        scrape.scrapeCourseList();
//        scrape.writeCourseList("courseList.txt");
//        scrape.writeCourseDescription("courseDescriptions.txt");

        String pattern = "([^-{}]*)";
    }
    int count = 0;
    private String year, session;
    private final List<String> courses = new ArrayList<>();
    private final List<String> courseDescriptions = new ArrayList<>();

    public CourseScraper(String year, String session) {
        this.year = year;
        this.session = session;
    }

    public void scrapeCourseList() throws FileNotFoundException, UnsupportedEncodingException, IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("subjects.txt"), "UTF-8"))) {

//            String pattern = "([^(),]+)";
//            String pattern = "\\(([^)]*)\\)";
            // regex to isolate faculty name from text
            String pattern = "[^(),]+";
            Pattern p = Pattern.compile(pattern);

            String line = "";

            // Reads line by line from specified text file
            while ((line = reader.readLine()) != null) {

                String subject = extractSubject(line);
                String faculty = extractFaculty(line);

                Matcher match = p.matcher(faculty);

                while (match.find()) {
                    faculty = match.group().trim();
                    connectToCourseList(faculty, subject);
                }
                System.out.println("Count: " + count);
            }
        }
    }

    public void connectToCourseList(String faculty, String subject) throws IOException {
        final String SITE = "https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm.woa/wa/crsq1?"
                + "faculty=" + faculty + "&subject=" + subject + "&academicyear=" + this.year + "&studysession=" + this.session;

        Document doc = Jsoup.connect(SITE).userAgent("Mozilla")
                .timeout(15000)
                .maxBodySize(0)
                .get();

        if (doc.select("table.cellpadding").text().contains("parameter")) {
            throw new InputMismatchException("Error at: " + faculty + " and " + subject);
        }

        scrapeCourseDescription(doc);
//        Iterator<Element> courseCode = courseList.iterator();
//        Iterator<Element> courseTitle = courseLists.iterator();
    }

    public void scrapeCourseDescription(Document doc) throws IOException {
        String prefix = "https://w2prod.sis.yorku.ca/";
        Elements scheduleLinks = doc.select("td[width='30%'] > a");

        Elements courseCode = doc.select("td[width='16%']");
        Elements courseName = doc.select("td[width='24%']");

//        Elements courseCode = getCourseCode(doc);
//        Elements courseName = getCourseName(doc);
        for (int i = 0; i < scheduleLinks.size(); i++) {

            String link = scheduleLinks.get(i).attr("href");

            Document start = Jsoup.connect(prefix + link).userAgent("Mozilla")
                    .timeout(15000)
                    .maxBodySize(0)
                    .get();

//            String course = courseCode.get(i).text() + " - " + courseName.get(i).text();
//            String description = "{" + getCourseDescription(start) + "}";
//            System.out.println(course);
//            courses.add(course);
//            courseDescriptions.add(description);
//            System.out.println(courseCode.get(i).text() + " - " + courseName.get(i).text());
//            System.out.println(getCourseDescription(start));
            courses.add(courseCode.get(i).text() + " - " + courseName.get(i).text());
            courseDescriptions.add("{" + getCourseDescription(start) + "}");
            count++;
        }
    }

    private String getCourseDescription(Document doc) {
        Elements description = doc.select("p");

        String result = description.get(3).text();

        if (result.isEmpty()) {
            return "N/A";
        }

        return result;
    }

    private String extractFaculty(String faculty) {
        return faculty.substring(faculty.lastIndexOf('-') + 1).trim();
    }

    private String extractSubject(String subject) {
        if (subject.contains("-")) {
            subject = subject.replace("-", " ");
        }
        return subject.substring(0, 4);
    }

    public void scrapeCourses(Document doc) {

        Elements courseCode = getCourseCode(doc);
        Elements courseName = getCourseName(doc);

        for (int i = 0; i < courseCode.size(); i++) {
            System.out.println(courseCode.get(i).text() + " " + courseName.get(i).text());
            courses.add(courseCode.get(i).text() + " " + courseName.get(i).text());
        }
    }

    public void writeCourseList(String location) throws FileNotFoundException {
        File file = new File(location);

        try (PrintWriter writer = new PrintWriter(file)) {
            for (String course : courses) {
                writer.println(course);
            }
        }
    }

    public void writeCourseDescription(String location) throws FileNotFoundException {
        File courseDescFile = new File(location);

        try (PrintWriter writer = new PrintWriter(courseDescFile)) {
            for (int i = 0; i < courseDescriptions.size(); i++) {
                writer.println(courses.get(i) + " " + courseDescriptions.get(i));
            }
        }
    }

    private Elements getCourseCode(Document doc) {
        return doc.select("td[width='16%']");
    }

    private Elements getCourseName(Document doc) {
        return doc.select("td[width='24%']");
    }
}
