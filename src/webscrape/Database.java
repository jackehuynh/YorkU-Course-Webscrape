package webscrape;

import java.sql.*;

public class Database {

    private static final String host = "jdbc:mysql://localhost/test";
    private static final String user = "root";
    private static final String pass = "";
    private static Connection conn;
    private static String dbName;

    public Database() {
        try {
            conn = DriverManager.getConnection(host, user, pass);
            System.out.println("Connected!");
            String useDB = "USE " + dbName; // any SQL statements will be applied to this particular DB
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(useDB);
        } catch (SQLException e) {
            System.out.println("Connection failed.");
        }
    }
    
    public static void setdbName(String name) {
    	dbName = name;
    }

    public static void main(String[] args) throws SQLException {
        conn = null;
        Statement stmt = null;
        setdbName("`fallwinter-1819`");
        //dbName = "`fallwinter-1819`";
        try {
            conn = DriverManager.getConnection(host, user, pass);
            System.out.println("Connected!");
            
            stmt = conn.createStatement();
            createDB();
            
            String sql = "USE " + getdbName(); // any SQL statements will be applied to this particular DB
            stmt.executeUpdate(sql);
            
            createTable();
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            conn.close();
        }
    }

    public static void createDB() throws SQLException {
        try {
            String sql = "CREATE DATABASE IF NOT EXISTS "
                    + getdbName()
                    + " CHARACTER SET utf8mb4 "
                    + "COLLATE utf8mb4_unicode_ci";
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            
            System.out.println("Database created");
        } catch (SQLException e) {
            System.out.println("Did not create database // it already exists.");
            e.printStackTrace();
        }
    }

    public static void createTable() throws SQLException {
    	Statement stmt = conn.createStatement();
        try {
            String subjectTable = "CREATE TABLE IF NOT EXISTS Subject ("
                    + "Subject_ID INT NOT NULL AUTO_INCREMENT, "
                    + "Name varchar(400), "
                    + "PRIMARY KEY (Subject_ID) "
                    + ") "
                    + "ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci";
            String courseTable = "CREATE TABLE IF NOT EXISTS Courses ("
                    + "Course_ID INT NOT NULL AUTO_INCREMENT, "
                    + "Subject_ID INT, "
                    + "Name varchar(200), "
                    + "Title varchar(500), "
                    + "Description varchar(3000), "
                    + "Info varchar(4000)"
                    + "PRIMARY KEY(Course_ID), "
                    + "FOREIGN KEY(Subject_ID) REFERENCES Subject(Subject_ID)"
                    + ") "
                    + "ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci";
            
            stmt.executeUpdate(subjectTable);
            stmt.executeUpdate(courseTable);
            
            System.out.println("Subject table created.");
        } catch (SQLException e) {
            System.out.println("Did not create table. // it already exists.");
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    public String escapeCharacter(String input) {
    	return input.replace("'", "''");
    }

    public void insertSubject(String subj, int id) throws SQLException {
        String subject = escapeCharacter(subj);
    	Statement stmt = null;
    	
        try {
            stmt = conn.createStatement();
            String query = "INSERT INTO Subject (Subject_ID, Name) "
                    + "VALUES (" + id + ", '" + subject + "') "
                    + "ON DUPLICATE KEY UPDATE "
                    + "NAME='" + subject + "'";
            stmt.executeUpdate(query);
            System.out.println("Inserted " + subject);
        } catch (SQLException e) {
            System.out.println("Failed to insert subject. " + subject);
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void insertCourse(String courses, String titles, String code, String desc) throws SQLException {
        String course = escapeCharacter(courses);
        String title = escapeCharacter(titles);
        String description = escapeCharacter(desc);
    	
    	Statement stmt = null;
    	ResultSet rs = null;
    	 
        try {
            String queryID = "SELECT Subject_ID FROM `subject` WHERE NAME like '" + code + "%'";

            //stmt = conn.prepareStatement(queryID, Statement.RETURN_GENERATED_KEYS);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryID);
            rs.next();
            
            int id = rs.getInt("Subject_ID");
            String query = "Insert into courses(Name, Subject_ID, Title, Description) values ("
                    + " '" + course + "',"
                    + " '" + id + "', "
                    + " '" + title + "', "
                    + " '" + description + "')";
            
            stmt.executeUpdate(query);
            
            System.out.println("Inserted: " + course);
        } catch (SQLException e) {
            System.out.println("Failed to insert. " + course);
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    public void insertCourseInfo(String title, String info) throws SQLException {
    	String courseTitle = escapeCharacter(title);
    	String courseInfo = escapeCharacter(info);
    	
    	Statement stmt = conn.createStatement();
    	
    	try {
    		String sql = "INSERT INTO courses(info) "
    				   + "SELECT Title FROM courses WHERE Title LIKE '" + courseTitle + "' "
    				   + "VALUES('" + courseInfo + "') "
    				   + "ON DUPLICATE KEY UPDATE info=CONCAT(info, '" + courseInfo + "')";
    		
    		stmt.executeQuery(sql);
    	} catch (SQLException e) {
    		System.out.println("Failed to insert/update course info: " + courseTitle);
    		e.printStackTrace();
    	} finally {
    		if (stmt != null) {
    			stmt.close();
    		}
    	}
    } 
    
    public void dropInfoColumn() throws SQLException {
    	/* Need to drop the info column otherwise scraper would keep concatenating to the info column */
    	String query = "ALTER TABLE courses "
				 	 + "DROP COLUMN info";
    	
		Statement stmt = conn.createStatement();
		
    	try {
    		stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("What would go wrong here..?");
			e.printStackTrace();
		} finally {
			stmt.executeQuery(query);
			if (stmt != null) {
				stmt.close();
			}
		}
    	
    }

    public void closeConn() throws SQLException {
        conn.close();
    }

    public static String getdbName() {
        return dbName;
    }

    public Connection getConn() throws SQLException {
        return conn;
    }
}
