package webscrape;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ACTScraper {

    private String courseName, term;
    private Element lecture, lab_tutorial;
    private Database db;

    public static void main(String[] args) throws SQLException, IOException {
    	
        ACTScraper FallWinter_Scraper = new ACTScraper("Fall/Winter");
    	//ACTScraper scrape = new ACTScraper("test");
        
    }
    
    public ACTScraper(String session) throws IOException, SQLException {
    	db = new Database();
    	//db.dropInfoColumn();
    	
    	this.startConnection(session);
    	
    	//db.closeConn();
    }

    private void startConnection(String session) throws IOException, SQLException { 
    	Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        
    	Elements link = doc.select("ul.bodytext")
                		   .select("a[href]");
        
        final String actLink = "https://w2prod.sis.yorku.ca/" + link.get(link.size() - 1).attr("href"); // Active Course Timetable link
        
        Document connectToACTList = Jsoup.connect(actLink).userAgent("Mozilla").get();
        
        Elements ACT_SCHEDULE;
        
        if (session.equals("Fall/Winter")) {
            Elements FW_ACT = connectToACTList.select(":containsOwn(View Fall/Winter)"); // grabs all ACT (Active Course Timetable) links for F/W courses
            ACT_SCHEDULE = FW_ACT;
        } else if (session.equals("Summer")) {
            Elements SummerSchedules = connectToACTList.select(":containsOwn(View Summer)"); // grabs all ACT links for Summer courses
            ACT_SCHEDULE = SummerSchedules;
        } else {
        	System.out.println("Please only pass string input 'Fall/Winter' or 'Summer' in constructor parameter");
        	throw new InputMismatchException();
        }
        
        String timetableFaculty[] = new String[ACT_SCHEDULE.size()];
        
        for (int i = 0; i < ACT_SCHEDULE.size(); i++) {
            timetableFaculty[i] = ACT_SCHEDULE.get(i).attr("href");
        }
        this.scrapeACT(timetableFaculty);
    }
    
    private void scrapeACT(String ACTList[]) throws IOException, SQLException {
        for (int i = 0; i < ACTList.length; i++) {
            try {
                Document doc = Jsoup.connect(ACTList[i])
                        	   .timeout(6000)
                        	   .maxBodySize(0)
                        	   .get();
                Elements rows = doc.select("tr");
                for (int k = 2; k < rows.size(); k++) {
                	
                    Elements tdTags = rows.get(k).select("td");
                    initSpanElements(tdTags);
                    
                    /*  Grabs faculty, course title, department and term */
                    if (tdTags.hasClass("bodytext")) {
                        setTerm(tdTags.get(2).text());
                        setCourseName(tdTags.get(3).text());
                        String faculty = tdTags.get(0).text();
                        String department = tdTags.get(1).text();
                        System.out.println("\n" + faculty + "/" + department + "  " + getCourseName() + "\n");
                        System.out.print("Term\n");
                    	//db.insertCourseInfo(getCourseName(), "Term\n");
                    } else {
                        if (tdTags.size() > 4) { // inserts Tutorial/Lab/Lecture times, instructor, and any additional course info to DB 
                    		
                        	String section;
                        	String credit;
                        	String courseTerm;
                    		String catNumber = null;
                    		String course = null;
                        	String courseType = tdTags.get(1).text();
                        	String instructor = tdTags.get(tdTags.size()-2).text();
                        	String additionalInfo = tdTags.get(tdTags.size()-1).text();
                        	
                        	courseTerm = "\n " + getTerm() + "    ";
                        	additionalInfo = getAdditionalInfo(additionalInfo, tdTags);
                        	instructor = getInstructor(instructor);
                        	section = getSection(courseType);
                        	credit = getCredit(courseType);
                        	catNumber = getCatNumber(tdTags);
                        	course = concatCourseInfo(courseTerm, instructor, credit, section, tdTags);

                        	String courseInformation = "\n" + course + catNumber + additionalInfo + "\n";
                        	
                        	System.out.print(courseInformation);
                        	
                        	//db.insertCourseInfo(getCourseName(), courseInformation);
                        	
                        	Element tbodyTag = tdTags.select("tbody").first();
                        	if (tbodyTag != null) {
                            	Elements timeSlot = tbodyTag.select("td");
                            	insertCourseSchedule(timeSlot, getCourseName());
                        	}
                        }
                    }
                }
                System.out.println("--------------------------------------------------------------------------------------");
            } catch (IndexOutOfBoundsException e) {
            	e.printStackTrace();
                System.out.println("\n No classes at " + ACTList[i] + "\n\n");
                continue;
            } catch (ParseException e) {
				e.printStackTrace();
			}
        }
    }

	private void insertCourseSchedule(Elements times, String courseTitle) throws ParseException {
    	Elements time = times;
    	
    	String result = null;
    	
    	for(int i = 0; i < time.size(); i+=4) {
    		
    		result = "     ";
    		
    		if (time.get(i) != null) {
    			result += " Day: " + getDays(time.get(i).text()) + "    ";
    		}
    		if (time.get(i+1) != null) {
        		result += "Time: " + convertTime(time.get(i+1).text()) + "    ";
    		}
    		if (time.get(i+2) != null) {
        		result += "Duration: " + convertMinutes(time.get(i+2).text()) + "    ";
    		}
    		if (time.get(i+3) != null) {
    			result += "Location: " + getLocation(time.get(i+3).text());
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
    
    private String getAdditionalInfo(String input, Elements tdTags) {
    	String info = input;
    	
    	if (!input.equals("")) {
        	info = "\n      " + tdTags.get(tdTags.size()-1).html();
        	info = info.replace("<br>", "");
        	info = info.replace("&nbsp;", "");
    	}
    	return info;
    }
    
    private String getInstructor(String input) {
    	if(input.equals("")) {
    		return "Instructor: N\\A \n";
    	} else {
    		return "Instructor: " + input + "\n";
    	}
    }
    
    private String getSection(String courseType) {
    	if (courseType.length() == 11) {
    		return "Section " + courseType.charAt(10); 
    	} else if (courseType.length() == 12) {
    		return "Section " + courseType.charAt(11); 
    	}
    	return "N\\A      ";
    }
    
    private String getCredit(String courseType) {
    	
    	if (courseType.length() == 11) {
			return "      Credit: " + courseType.charAt(5) + courseType.charAt(6) + courseType.charAt(7) + courseType.charAt(8);
    	} else if (courseType.length() == 12) {
    		return "      Credit: " + courseType.charAt(6) + courseType.charAt(7) + courseType.charAt(8) + courseType.charAt(9); 
    	}
    	return "N\\A      ";
    }
    
    private String getCatNumber(Elements tdTags) {
    	
    	String catNum = "";
 
		if (getLecture() != null) {
			catNum = tdTags.get(5).text();
		} else if (getLab_Tutorial() != null) {
			catNum = tdTags.get(3).text();
		}
		
    	if(!catNum.equals("")) {
    		catNum = " | CAT#: " + catNum;
    	}
		
		return catNum;
    }
    
    private String concatCourseInfo(String courseTerm, String instructor, String credit, String section, Elements tdTags) {
    	
    	String course = "";
    	
		if (getLecture() != null) {
        	course = "---------------------------------------------------------------------------------\n" 
        			+ courseTerm + instructor + credit + " | " + section + " | "
        			+ tdTags.get(2).text() + " " // Language of instruction (EN - English, ....)
        			+ tdTags.get(3).text() + " " // Class type (LECT, LAB, TUTR, ...)
        			+ tdTags.get(4).text() + " ";		 // Class type number (LECT 01, LAB 01, ...)
		} else if (getLab_Tutorial() != null) {
        	course = "      " + instructor + "      "
        		   + tdTags.get(1).text() + " " // Class type (LECT, LAB, TUTR, ...)
        		   + tdTags.get(2).text();		// Class type number (LECT 01, LAB 01, ...)
		}
		
    	return course;
    }
     
    private String getLocation(String location) {
    	if (location.equals("")) {
        	return "N\\A      ";
    	}
    	return location;
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
    
    private String convertTime(String time) throws ParseException {
    	/* these times usually mean the course
    	 * is either online/thesis/supervised study/others */
    	if(time.equals("0:00") || time.equals("0")) {
    		return time;
    	}
    	
    	/* Converts 24-hour format to 12-hour time format. */
    	if(!time.equals("")) {
            DateFormat date = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
            DateFormat f = new SimpleDateFormat("h:mma");
            Date d = date.parse(time);
            
            String convertedTime = f.format(d).toLowerCase();
            return convertedTime;
    	} else {
        	return "N\\A      ";
    	}
    }
    
    private String convertMinutes(String times) {
    	
    	if(times.equals("0")) {
    		return "N\\A";
    	}
    	
    	int time = Integer.parseInt(times);
    	int hours = time / 60;
    	int minutes = time % 60;
    	
    	String newHour = Integer.toString(hours);
    	String newMin = Integer.toString(minutes);
    	
    	if (minutes == 0) {
    		return newHour + "h ";
    	} else {
        	return newHour + "h " + newMin + "m ";
    	}
    }
    
    private void initSpanElements(Elements tdTags) {
    	lecture = tdTags.select("[colspan=3]").first(); 	 /* This HTML element usually displays info for class/lectures */
    	lab_tutorial = tdTags.select("[colspan=5]").first(); /* This HTML element usually displays info for labs/tutorials */
    }
    
    private Element getLecture() {
    	return this.lecture;
    }
    
    private Element getLab_Tutorial() {
    	return this.lab_tutorial;
    }
    
    private void setCourseName(String input) {
        this.courseName = input;
    }

    private String getCourseName() {
        return this.courseName;
    }

    private void setTerm(String input) {
        this.term = input;
    }

    private String getTerm() {
        return this.term;
    }
}
