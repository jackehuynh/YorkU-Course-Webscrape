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
        scrape.setFileLocation("courseTest.txt");
        scrape.scrapeCourseList();
    }

    private String year, session;
    private final List<String> courses = new ArrayList<>();
    private File file;

    public CourseScraper(String year, String session) {
        this.year = year;
        this.session = session;
    }

    void scrapeCourseList() throws FileNotFoundException, UnsupportedEncodingException, IOException {

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
            }
        }

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

    public void setFileLocation(String location) {
        this.file = new File(location);
    }

    private Elements getCourseCode(Document doc) {
        return doc.select("td[width='16%']");
    }

    private Elements getCourseName(Document doc) {
        return doc.select("td[width='24%']");
    }

    public void connectToCourseList(String faculty, String subject) throws IOException {
        final String SITE = "https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm.woa/wa/crsq1?"
                + "faculty=" + faculty + "&subject=" + subject + "&academicyear=" + this.year + "&studysession=" + this.session;

        Document doc = Jsoup.connect(SITE).userAgent("Mozilla")
                .timeout(6000)
                .maxBodySize(0)
                .get();

        if (doc.select("table.cellpadding").text().contains("parameter")) {
            throw new InputMismatchException("Error at: " + faculty + " and " + subject);
        }

        Elements courseCode = getCourseCode(doc);
        Elements courseName = getCourseName(doc);

//        Iterator<Element> courseCode = courseList.iterator();
//        Iterator<Element> courseTitle = courseLists.iterator();

        for (int i = 0; i < courseCode.size(); i++) {
            System.out.println(courseCode.get(i).text() + " " + courseName.get(i).text());
            courses.add(courseCode.get(i).text() + " " + courseName.get(i).text());
        }
    }

    public void writeToFile(String location) throws FileNotFoundException {
        this.file = new File(location);

        try (PrintWriter writer = new PrintWriter(file)) {
            for (int i = 0; i < courses.size(); i++) {
                writer.println(courses.get(i));
            }
        }
    }
}
