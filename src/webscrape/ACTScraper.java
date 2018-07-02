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

    private String courseName, term;
    private Database db;

    public static void main(String[] args) throws SQLException, IOException {
    	
        ACTScraper scraper = new ACTScraper();
        
        try {
        	scraper.startConnection();
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
            scraper.db.closeConn();
        }
    }
    
    public ACTScraper() throws IOException, SQLException {
    	db = new Database();
    	//db.dropInfoColumn();
    }

    private void startConnection() throws IOException, SQLException { 
    	Document doc = Jsoup.connect("https://w2prod.sis.yorku.ca/Apps/WebObjects/cdm").userAgent("Mozilla").get();
        
    	Elements result = doc.select("ul.bodytext")
                			 .select("a[href]");
        
        final String link = "https://w2prod.sis.yorku.ca/" + result.get(result.size() - 1).attr("href"); // Active Course Timetable link
        
        Document connect = Jsoup.connect(link).userAgent("Mozilla").get();
        
        /* TODO: Implement code to allow for either Fall-Winter or Summer schedules to be used   */
        Elements getFW_Schedules = connect.select(":containsOwn(View Fall/Winter)"); // grabs all ACT (Active Course Timetable) links of F/W courses
        Elements getSummerSchedules = connect.select(":containsOwn(View Summer)"); // grabs all ACT links of Summer courses
        
        String timetableFaculty[] = new String[getFW_Schedules.size()];
        
        for (int i = 0; i < getFW_Schedules.size(); i++) {
            timetableFaculty[i] = getFW_Schedules.get(i).attr("href");
        }
        scrapeACT(timetableFaculty);
    }
    
    public String getAdditionalInfo(String input, Elements tdTags) {
    	String info = "";
    	
    	if (!input.equals("")) {
        	info = "\n      " + tdTags.get(tdTags.size()-1).html();
        	info = info.replace("<br>", "");
        	info = info.replace("&nbsp;", "");
    	}
    	return info;
    }
    
    private String getInstructor(String input, Elements tdTags) {
    	String instructor = "";
    	
    	if(instructor.equals("")) {
    		instructor = "Instructor: N\\A \n";
    	} else {
    		instructor = "Instructor: " + instructor + "\n";
    	}
    	
    	return instructor;
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
            		Element span3 = tdTags.select("[colspan=3]").first();
            		Element span5 = tdTags.select("[colspan=5]").first();
                    
                    /*  Grabs faculty, course title & department and term */
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
                    		
                    		String catNumber = null;
                    		String course = null;
                        	String courseType = tdTags.get(1).text();
                        	String section = "";
                        	String credit = "      ";
                        	String instructor = tdTags.get(tdTags.size()-2).text();
                        	String courseTerm = "\n " + getTerm() + "    ";
                        	String additionalInfo = tdTags.get(tdTags.size()-1).text();
                        	
                        	additionalInfo = getAdditionalInfo(additionalInfo, tdTags);
                        	instructor = getInstructor(instructor, tdTags);

                			if (courseType.length() == 11) { // regular length course info (ex: 1014 3.00 A)
                				section = "Section " + courseType.charAt(10);                				
                				credit += "Credit: " + courseType.charAt(5) + courseType.charAt(6) + courseType.charAt(7) + courseType.charAt(8);
                			} else if (courseType.length() == 12) { // special length course info (ex: 1020P 3.00 A)
                				section = "Section " + courseType.charAt(11);
                				credit += "Credit: " + courseType.charAt(6) + courseType.charAt(7) + courseType.charAt(8) + courseType.charAt(9);
                			}
                        	
                    		if (span3 != null) { /* This section usually displays info for the class/lecture */
                    			catNumber = tdTags.get(5).text();
                            	course = "---------------------------------------------------------------------------------\n" 
                            			+ courseTerm + instructor + credit + " | " + section + " | "
                            			+ tdTags.get(2).text() + " " // Language of instruction (EN - English, ....)
                            			+ tdTags.get(3).text() + " " // Class type (LECT, LAB, TUTR, ...)
                            			+ tdTags.get(4).text() + " ";		 // Class type number (LECT 01, LAB 01, ...)
                    		} else if (span5 != null) { /* Section displays info for labs/tutorials */
                    			catNumber = tdTags.get(3).text();
                            	course = "      " + instructor + "      "
                            		   + tdTags.get(1).text() + " " // Class type (LECT, LAB, TUTR, ...)
                            		   + tdTags.get(2).text();		// Class type number (LECT 01, LAB 01, ...)
                    		}
                    		
                        	if(!catNumber.equals("")) {
                        		catNumber = " | CAT#: " + catNumber;
                        	}

                        	String courseInformation = "\n" + course + catNumber + additionalInfo;
                        	System.out.print(courseInformation);
                        	//db.insertCourseInfo(getCourseName(), courseCat);
                        	
                        	System.out.println();
                        	
                        	Element tbodyTag = tdTags.select("tbody").first();
                        	if (tbodyTag != null) {
                            	Elements day = tbodyTag.select("[width=15%]"); // day
                            	Elements time = tbodyTag.select("[width=25%]"); // time and duration
                            	Elements location = tbodyTag.select("[width=35%]"); // room location
                            	Elements timeSlots = tbodyTag.select("tr");
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
    	 * is online/thesis/supervised study/others */
    	if(time.equals("0:00") || time.equals("0")) {
    		return time;
    	}
    	
    	/* Convert 24-hour to 12-hour time format. */
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
}
