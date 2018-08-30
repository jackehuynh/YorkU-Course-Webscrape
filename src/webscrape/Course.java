package webscrape;

import java.util.ArrayList;
import java.util.List;

public class Course {

    static String credit;
    private String name;
    private String term;  // F, W, S1, S2, SU, etc..
    private List<Section> sections;

    public Course() {
        this.sections = new ArrayList<>();
    }

    public Course(String courseCredit, String name) {
        this.sections = new ArrayList<>();
        this.credit = courseCredit;
        this.name = name;
    }

    public void setCourseName(String name) {
        this.name = name;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getName() {
        return this.name;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void printSections() {
        System.out.println("Course: " + name + " " + credit + " ");
        for (Section s : sections) {
            System.out.println(s.toString());
        }
    }
}
