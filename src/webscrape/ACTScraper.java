package webscrape;

import java.io.IOException;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ACTScraper {

    private String timetableFaculty[];
    private String courseName;
    private String term;
    private Database db;

    public static void main(String[] args) throws IOException, SQLException {
        ACTScraper scraper = new ACTScraper();
        //scraper.db.closeConn();
    }
    
    public ACTScraper() throws IOException, SQLException {
    	db = new Database();
    	//db.dropInfoColumn();
    	startConnection();
    }

    private void startConnection() throws IOException, SQLException {
        Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        Elements result = doc.select("ul.bodytext")
                .select("a[href]");
        
        final String link = "https://w2prod.sis.yorku.ca/" + result.get(result.size() - 1).attr("href"); // Active Course Timetable link
        
        Document connect = Jsoup.connect(link).userAgent("Mozilla").get();
        Elements getFW_Schedules = connect.select(":containsOwn(View Fall/Winter)"); // grabs all ACT (Active Course Timetable) links of F/W courses
        Elements getSummerSchedules = connect.select(":containsOwn(View Summer)"); // grabs all ACT links of Summer courses
        timetableFaculty = new String[getFW_Schedules.size()];
        for (int i = 0; i < getFW_Schedules.size(); i++) {
            timetableFaculty[i] = getFW_Schedules.get(i).attr("href");
        }
        scrapeACT();
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
    
    

    private void scrapeACT() throws IOException, SQLException {
        for (int i = 0; i < timetableFaculty.length; i++) {
            try {
                Document doc = Jsoup.connect(timetableFaculty[i])
                        	   .timeout(6000)
                        	   .maxBodySize(0)
                        	   .get();
                Elements rows = doc.select("tr");
                for (int k = 2; k < rows.size(); k++) {
                    Elements tdTags = rows.get(k).select("td");
                    /*  Grabs faculty, course title & department and term */
                    if (tdTags.hasClass("bodytext")) {
                        setTerm(tdTags.get(2).text());
                        setCourseName(tdTags.get(3).text());
                        String faculty = tdTags.get(0).text();
//                    setTitle(faculty + "/" + term + "  " + tdTags.get(1).text() + "  " + tdTags.get(3).text() + "  ");
                        System.out.println(faculty + "/" + term + "  " + tdTags.get(1).text() + "  " + tdTags.get(3).text() + "  ");
                        System.out.println();
                        System.out.println("Term");
                        //System.out.print(getTerm() + " - " + getCourseName());
                    //System.out.print(rows.get(k).text());
                    } else {
                        if (tdTags.size() > 4) { // inserts Tutorial/Lab/Lecture times, instructor, and any additional course info to DB 
//                        	for(int j = 1; j <= 5; i++) {
//                            	System.out.print(tdTags.get(j).text() + " ");
//                        	}
                        	
                    		Element span3 = tdTags.select("[colspan=3]").first();
                    		Element span5 = tdTags.select("[colspan=5]").first();
                    		
                    		String catNumber = null;
                    		String course = null;
                    		
                        	System.out.print(" " + term + "   ");
                    		
                    		if (span3 != null) {
                    			catNumber = tdTags.get(5).text();
                            	course = tdTags.get(1).text() + " " + tdTags.get(2).text() + " " + tdTags.get(3).text() + " " + tdTags.get(4).text();
                    		} else if (span5 != null) {
                    			catNumber = tdTags.get(3).text();
                            	course = tdTags.get(1).text() + " " + tdTags.get(2).text();
                    		}
                    		
                    		System.out.print(course);
                    		
                        	if(catNumber.equals("")) {
                            	System.out.print(catNumber);
                        	} else {
                        		catNumber = " CAT# " + catNumber;
                        		System.out.print(catNumber);
                        	}
                        	
                        	String additionalInfo = tdTags.get(tdTags.size()-1).text();
                    		Elements courseInfoLink = tdTags.select("a");
                        	if (courseInfoLink.isEmpty()) { // no links are embedded in additional course info section
                            	System.out.print("   " + additionalInfo);
                        		//System.out.print("<a href='" + link + "'> " + additionalInfo + "</a>");
                        	} else {
                           		System.out.print("   " + courseInfoLink);
                        	}
                        	
                        	System.out.println();
                        	
                        	Element body = tdTags.select("tbody").first();
                        	if (body != null) {
                            	Elements day = body.select("[width=15%]"); // day
                            	Elements time = body.select("[width=25%]"); // time and duration
                            	Elements location = body.select("[width=35%]"); // room location
                            	System.out.println("Day size: " + day.size() + " Time size: " + time.size() + " Loc Size: " + location.size());
                        		
                        		for(int a = 0; a < day.size(); a++) {
                        			String days = "\t\t\t  " + day.get(a).text() + "  ";
                        			for(int b = 0; b < time.size(); b+=2) {
                            			String times = time.get(b).text() + "  " + time.get(b+1).text() + "  ";
                        				for(int c = 0; c < location.size(); c++) {
                        					String place = location.get(c).text() + " \n ";
                        					System.out.print(days + times + place);
                        				}
                        			}
                        		}
                        		/*
                            	System.out.print("Days: " + day.get(0).text());
                            	System.out.print("   Time: " + time.get(0).text());
                            	System.out.print("   Location: " + location.get(0).text());
                            	*/
                        	}
                        	
//                            System.out.print(" " + getTerm() + " - " + rows.get(k).text() + " ");
//                            String courseInfo = "Term " + getTerm() + " - " + rows.get(k).text() + "\n";
                           //db.insertCourseInfo(getCourseName(), courseInfo);
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
