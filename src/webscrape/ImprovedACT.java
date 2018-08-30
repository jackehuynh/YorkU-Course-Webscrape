package webscrape;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImprovedACT {

    private final List<String> facultyTimetables;
    private final List<Course> courses;
    private String NA = "N/A"; //"N&#47A"; // 'N/A' - Not Applicable 

    public static void main(String[] args) throws IOException {
        ImprovedACT newACT = new ImprovedACT();
        newACT.startConnection("Fall/Winter");
        newACT.printCourse();
    }

    public ImprovedACT() {
        courses = new ArrayList<>();
        facultyTimetables = new ArrayList<>();
    }

    public void startConnection(String session) throws IOException {

        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        Elements link = doc.select("ul.bodytext").select("a[href]");

        // Active Course Timetable link
        final String actLink = "https://w2prod.sis.yorku.ca/" + link.get(link.size() - 1).attr("href");

        Document connectToACTList = Jsoup.connect(actLink).userAgent("Mozilla").get();

        Elements ACT_SCHEDULE;

        if (session.equals("Fall/Winter")) {
            ACT_SCHEDULE = connectToACTList.select(":containsOwn(View Fall/Winter)");
        } else if (session.equals("Summer")) {
            ACT_SCHEDULE = connectToACTList.select(":containsOwn(View Summer)");
        } else {
            System.out.println("Please only pass string input 'Fall/Winter' or 'Summer' in constructor parameter.");
            throw new InputMismatchException();
        }

        for (int i = 0; i < ACT_SCHEDULE.size(); i++) {
            String ACT_link = ACT_SCHEDULE.get(i).attr("href");
            facultyTimetables.add(ACT_link);
        }

        scrapeACT();
    }

    void printCourse() {
        for (Course course : courses) {
            course.printSections();
        }
    }

    private void scrapeACT() {
        for (String link : facultyTimetables) {
            try {
                Document doc = Jsoup.connect(link).timeout(12000).maxBodySize(0).get();
                traverseACT(doc);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("No classes at " + link);
            } catch (IOException e) {
            }
        }
    }

    private void traverseACT(Document doc) {
        Elements rows = doc.select("tr");

        for (int i = 2; i < rows.size(); i++) {
            Elements tdTags = rows.get(i).select("td");
            Element lecture = tdTags.select("[colspan=3]").first();
            Element lab_tutorial = tdTags.select("[colspan=5]").first();
            extractCourseInfo(i, rows, tdTags, lecture, lab_tutorial);
        }
    }

    private String extractCourseCode(String input) {
        return input.substring(0, input.length() - 2);
    }

    private Course temp;

    private void extractCourseInfo(int index, Elements rows, Elements tdTags, Element lec, Element lab_tut) {
        String faculty = null, department = null, term = null, courseTerm = null;
        String catNumber = null;
        String courseType = null;
        String courseCode = null;
        String credit = null;
        String section = null;
        String instructor = null;
        String additionalInfo = null;
        String time = null;
        String location = null;

        if (tdTags.hasClass("bodytext")) {
            faculty = tdTags.get(0).text();
            department = tdTags.get(1).text();
            term = tdTags.get(2).text();

            courseTerm = "Term: " + term;

            tdTags = rows.get(index + 1).select("td");

            courseCode = faculty + "/" + department + " " + extractCourseCode(tdTags.get(1).text());

            this.temp = null;

            Course holdCourse = new Course();

            this.temp = holdCourse;

            this.temp.setCourseName(courseCode);
            this.temp.setTerm(term);

        } else if (tdTags.size() > 4) {

            catNumber = getCatNumber(tdTags, lec, lab_tut);

            if (lec != null) {
                courseType = tdTags.get(3).text() + tdTags.get(4).text();
            } else if (lab_tut != null) {
                courseType = tdTags.get(1).text() + tdTags.get(2).text();
            }

            credit = getCredit(tdTags.get(1).text());
            section = getSection(tdTags.get(1).text());

            String instructorTextField = tdTags.get(tdTags.size() - 2).text();
            instructor = "Instructor: " + getInstructor(instructorTextField);

            String additionalInfoTextField = tdTags.get(tdTags.size() - 1).text();
            additionalInfo = getAdditionalInfo(additionalInfoTextField, tdTags);

            Element tbodyTag = tdTags.select("tbody").first();
            if (tbodyTag != null) {
                // location and time elements are nested within timeSlot element tags
                Elements timeSlot = tbodyTag.select("td");
                time = extractTimes(timeSlot);
                location = getLocation(timeSlot);
            }

            temp.setCredit(credit);

            temp.addSection(new Section.Builder(section)
                    .setInstructor(instructor)
                    .setCatNumber(catNumber)
                    .setTime(time)
                    .setLocation(location)
                    .setClassType(courseType)
                    .addInfo(additionalInfo)
                    .build());

            courses.add(temp);
        }
    }

    private String getAdditionalInfo(String addInfo, Elements tdTags) {
        String info = addInfo;

        if (!addInfo.equals("")) {
            info = "<br>      " + tdTags.get(tdTags.size() - 1).html();
            info = info.replace("<br>", "");
            info = info.replace("&nbsp;", "");
        }

        return info;
    }

    private String getSection(String input) {
        if (input.length() == 11) {
            return "Section " + input.charAt(10);
        } else if (input.length() == 12) {
            return "Section " + input.charAt(11);
        } else {
            return "";
        }
    }

    private String getCredit(String input) {
        if (input.length() == 11) {
            return "Credit: " + input.charAt(5) + input.charAt(6) + input.charAt(7) + input.charAt(8);
        } else if (input.length() == 12) {
            return "Credit: " + input.charAt(6) + input.charAt(7) + input.charAt(8) + input.charAt(9);
        } else {
            return NA;
        }
    }

    private String extractTimes(Elements times) {
        String result = "";
        for (int i = 0; i < times.size(); i += 4) {
            if (times.get(i) != null) {
                result += "Day: " + convertDays(times.get(i).text()) + " ";
            }
            if (times.get(i + 1) != null) {
                result += "Time: " + times.get(i + 1).text() + " ";
            }
            if (times.get(i + 2) != null) {
                result += "Duration: " + times.get(i + 2).text() + " ";
            }
            if (times.get(i + 3) != null) {
                result += "Location: " + convertLocation(times.get(i + 3).text());
            }
        }
        return result;
    }

    private String convertLocation(String location) {
        if (location.equals("")) {
            return NA;
        }
        return location;
    }

    private String convertDays(String day) {
        if (day.equals("M")) {
            return "Mon";
        } else if (day.equals("T")) {
            return "Tue";
        } else if (day.equals("W")) {
            return "Wed";
        } else if (day.equals("R")) {
            return "Thu";
        } else if (day.equals("F")) {
            return "Fri";
        } else if (day.equals("S")) {
            return "Sat";
        } else if (day.equals("U")) {
            return "Sun";
        } else {
            return NA;
        }
    }

    private String getLocation(Elements timeSlotTag) {
        String result = "";

        for (int i = 0; i < timeSlotTag.size(); i += 4) {
            result += "Location: " + timeSlotTag.get(i + 3).text();
        }

        if (result.equals("")) {
            return "";
        } else {
            return result;
        }
    }

    private String getInstructor(String input) {
        /*
        Checks if instructor in the textfield is empty or not
         */
        if (input.equals("")) {
            return NA;
        } else {
            return input;
        }
    }

    private String getCatNumber(Elements tdTags, Element lecture, Element lab_tut) {
        String catNum = "";

        if (lecture != null) {
            catNum = tdTags.get(5).text();
        } else if (lab_tut != null) {
            catNum = tdTags.get(3).text();
        }
        if (!catNum.equals("")) {
            catNum = " CAT#: " + catNum;
        }
        return catNum;
    }

}
