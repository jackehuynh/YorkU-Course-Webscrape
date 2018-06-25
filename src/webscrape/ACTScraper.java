package webscrape;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ACTScraper {

    private String timetableFaculty[];
    private String courseName;
    private String term;

    public static void main(String[] args) throws IOException {
        ACTScraper test = new ACTScraper();
        test.connectionTwo();
        test.connectionStart();
    }

    public void connectionTwo() throws IOException {
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        Elements result = doc.select("ul.bodytext")
                .select("a[href]");
        String link = "https://w2prod.sis.yorku.ca/" + result.get(result.size() - 1).attr("href"); // Active Course Timetable link
        Document connect = Jsoup.connect(link).userAgent("Mozilla").get();
        Elements getFW_Schedules = connect.select(":containsOwn(View Fall/Winter)"); // grabs all ACT (Active Course Timetable) links of F/W courses
        Elements getSummerSchedules = connect.select(":containsOwn(View Summer)"); // grabs all ACT links of Summer courses
        timetableFaculty = new String[getFW_Schedules.size()];
        for (int i = 0; i < getFW_Schedules.size(); i++) {
            timetableFaculty[i] = getFW_Schedules.get(i).attr("href");
        }
    }

    public void setCourseName(String input) {
        this.courseName = input;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setTerm(String input) {
        this.term = input;
    }

    public String getTerm() {
        return this.term;
    }

    public void connectionStart() throws IOException {
        for (int i = 0; i < timetableFaculty.length; i++) {
            try {
                Document doc = Jsoup.connect(timetableFaculty[i])
                        .timeout(6000)
                        .maxBodySize(0)
                        .get();
                Elements rows = doc.select("tr");

                for (int k = 2; k < rows.size(); k++) {
                    Elements tdTags = rows.get(k).select("td");
                    /*
                      does td tag have class 'bodytext' if so,
                      it implies we're grabbing faculty, dept, term, and course title information
                      store this into a string so we can use it to update the DB
                     */
                    if (tdTags.hasClass("bodytext")) {
//                    String faculty = tdTags.get(0).text();
//                    setTitle(faculty + "/" + term + "  " + tdTags.get(1).text() + "  " + tdTags.get(3).text() + "  ");
//                    System.out.print(faculty + "/" + term + "  " + tdTags.get(1).text() + "  " + tdTags.get(3).text() + "  ");
                        setTerm(tdTags.get(2).text());
                        setCourseName(tdTags.get(3).text());
                        System.out.print(getTerm() + " - " + getCourseName());
//                    System.out.print(rows.get(k).text());
                    } else {
                        if (tdTags.size() > 4) {
                            System.out.print("Term " + getTerm() + " - " + rows.get(k).text() + " ");
                        }
                    }
                    System.out.println();
                }
                System.out.println("--------------------------------------------------------------------------------------");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("\n No classes at " + timetableFaculty[i] + "\n\n");
                continue;
            }
        }
    }
}
