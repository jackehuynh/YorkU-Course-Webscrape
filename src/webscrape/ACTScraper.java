package webscrape;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        
    	Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm")
    						.userAgent("Mozilla")
    						.get();
        
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
                    Elements noLinks = rows.get(k).select("td").not("a");
                    /*  Grabs faculty, course title & department and term */
                    if (tdTags.hasClass("bodytext")) {
                        setTerm(tdTags.get(2).text());
                        setCourseName(tdTags.get(3).text());
                        String faculty = tdTags.get(0).text();
                        String department = tdTags.get(1).text();
                        System.out.println(faculty + "/" + department + "  " + getCourseName() + "\n");
                        System.out.print("Term\n");
                    } else {
                        if (tdTags.size() > 4) { // inserts Tutorial/Lab/Lecture times, instructor, and any additional course info to DB 
                        	
                    		Element span3 = tdTags.select("[colspan=3]").first();
                    		Element span5 = tdTags.select("[colspan=5]").first();
                    		
                    		String catNumber = null;
                    		String course = null;
                        	String courseType = tdTags.get(1).text();
                        	String section = "";
                        	String credit = "";
                    		
                        	System.out.print(" " + term + "    ");
                    		
                			if (courseType.length() == 11) { // regular length course info (ex: 1014 3.00 A)
                				section = "Section " + courseType.charAt(10);                				
                				credit = "Credit: " + courseType.charAt(5) + courseType.charAt(6) + courseType.charAt(7) + courseType.charAt(8);
                			} else if (courseType.length() == 12) { // special length course info (ex: 1020P 3.00 A)
                				section = "Section " + courseType.charAt(11);
                				credit = "" + courseType.charAt(6) + courseType.charAt(7) + courseType.charAt(8) + courseType.charAt(9);
                			}
                        	
                    		if (span3 != null) {
                    			catNumber = tdTags.get(5).text();
                            	course = credit + " | " + section + " | "
                            			+ tdTags.get(2).text() + " " // Language of instruction (EN - English, ....)
                            			+ tdTags.get(3).text() + " " // Class type (LECT, LAB, TUTR, ...)
                            			+ tdTags.get(4).text() + " ";		 // Class type number (LECT 01, LAB 01, ...)
                    		} else if (span5 != null) {
                    			catNumber = tdTags.get(3).text();
                            	course = tdTags.get(1).text() + " " // Class type (LECT, LAB, TUTR, ...)
                            		   + tdTags.get(2).text();		// Class type number (LECT 01, LAB 01, ...)
                    		}
                    		
                        	if(!catNumber.equals("")) {
                        		catNumber = " | CAT# " + catNumber;
                        	}
                        	                        	
                        	String additionalInfo = null;
                        	String additionalText = noLinks.get(noLinks.size()-1).text();
                        	
                    		Elements courseInfoLink = tdTags.select("a");
                    		
                    		String infoWithLink = tdTags.select("a[href]").text();
                    		
                        	if (courseInfoLink.isEmpty()) { // no links are embedded in additional course info section
                            	additionalInfo = "   " + tdTags.get(tdTags.size()-1).text();
                        	} else {
                        		/* prevent duplication of text */
                        		additionalText = additionalText.replace(infoWithLink, "");
                        		additionalInfo = "   " + additionalText + "   " + courseInfoLink; // text has embedded links in it.
                        	}
                        	
                        	String courseCat = course + catNumber + additionalInfo;
                        	System.out.print(courseCat);
                        	//db.insertCourseInfo(getCourseName(), courseCat);
                        	
                        	System.out.println();
                        	
                        	Element body = tdTags.select("tbody").first();
                        	if (body != null) {
                            	Elements day = body.select("[width=15%]"); // day
                            	Elements time = body.select("[width=25%]"); // time and duration
                            	Elements location = body.select("[width=35%]"); // room location
                            	Elements timeSlots = body.select("tr");
                            	Elements timeSlot = body.select("td");
                            	insertCourseSchedule(timeSlot, getCourseName());
                        	}
                        }
                    }
                    System.out.println();
                }
                System.out.println("--------------------------------------------------------------------------------------");
            } catch (IndexOutOfBoundsException e) {
            	e.printStackTrace();
                System.out.println("\n No classes at " + timetableFaculty[i] + "\n\n");
                continue;
            } catch (ParseException e) {
				e.printStackTrace();
			}
        }
    }

    public void setCourseName(String input) {
        this.courseName = input;
    }

    public String getCourseName() {
        return this.courseName;
    }

    private void setTerm(String input) {
        this.term = input;
    }

    private String getTerm() {
        return this.term;
    }
    
    private String getDays(String day) {
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
        	return "N\\A";
    	}
    }
    
    private String getLocation(String location) {
    	if (location.equals("")) {
        	return "N\\A      ";
    	}
    	return location;
    }
    
    private String convertTime(String time) throws ParseException {
    	/* these times usually mean the course
    	 * are online/thesis/supervised study/others */
    	if(time.equals("0:00") || time.equals("0")) {
    		return time;
    	}
    	
    	/* Convert 24-hour to 12-hour time format. */
    	if(!time.equals("")) {
            DateFormat date = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
            DateFormat f = new SimpleDateFormat("h:mma");
            Date d = date.parse(time);
            
            time = f.format(d).toLowerCase();
            return time;
    	} else {
        	return "N\\A      ";
    	}
    }
    
    private String convertMinutes(String time) {
    	
    	if(time.equals("0")) {
    		return "N\\A";
    	}
    	
    	int newTime = Integer.parseInt(time);
    	int hours = newTime / 60;
    	int minutes = newTime % 60;
    	
    	String newHour = Integer.toString(hours);
    	String newMin = Integer.toString(minutes);
    	
    	if (minutes == 0) {
    		time = newHour + "h ";
    	} else {
        	time = newHour + "h " + newMin + "m ";
    	}
    	
    	return time;
    }
    
    private void insertCourseSchedule(Elements times, String courseTitle) throws ParseException {
    	Elements time = times;
    	
    	String result = null;
    	
    	for(int a = 0; a < time.size(); a+=4) {
    		
    		result = "     ";
    		
    		if (time.get(a) != null) {
    			result += " Day: " + getDays(time.get(a).text()) + "    ";
    		}
    		if (time.get(a+1) != null) {
        		result += "Time: " + convertTime(time.get(a+1).text()) + "    ";
    		}
    		if (time.get(a+2) != null) {
        		result += "Duration: " + convertMinutes(time.get(a+2).text()) + "    ";
    		}
    		if (time.get(a+3) != null) {
    			result += "Location: " + getLocation(time.get(a+3).text());
    		}
    		
    		result += "\n";
    		
    		System.out.print(result);
    		/*
    		try {
				db.insertCourseInfo(courseTitle, result);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Course (" + courseTitle + ") schedule failed to insert");
			}
			*/
    	}
    }
}
