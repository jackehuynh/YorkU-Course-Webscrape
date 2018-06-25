package webscrape;

import java.sql.*;

public class Database {

    private static final String host = "jdbc:mysql://localhost/test";
    private static final String user = "root";
    private static final String pass = "";
    private static Connection conn;
    private static Statement stmt;
    private static String dbName = "`fallwinter-1819`";

    public Database() {
        try {
            conn = DriverManager.getConnection(host, user, pass);
            System.out.println("Connected!");
            String useDB = "USE " + dbName; // any SQL statements will be applied to this particular DB
            stmt = conn.createStatement();
            stmt.executeUpdate(useDB);
        } catch (SQLException e) {
            System.out.println("Connection failed.");
        }
    }

    public static void main(String[] args) throws SQLException {
        conn = null;
        stmt = null;
        dbName = "`fallwinter-1819`";
        try {
            conn = DriverManager.getConnection(host, user, pass);
            System.out.println("Connected!");
            stmt = conn.createStatement();
            createDB();
            String sql = "USE " + dbName; // any SQL statements will be applied to this particular DB
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
            stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS "
                    + dbName
                    + " CHARACTER SET utf8mb4 "
                    + "COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(sql);
            System.out.println("Database created");
        } catch (SQLException e) {
            System.out.println("Did not create database // it already exists.");
            e.printStackTrace();
        }
    }

    public static void createTable() throws SQLException {
        try {
            stmt = conn.createStatement();
            String subjectTable = "CREATE TABLE IF NOT EXISTS Subject ("
                    + "Subject_ID INT NOT NULL AUTO_INCREMENT, "
                    + "Name varchar(400), "
                    + "PRIMARY KEY (Subject_ID) "
                    + ") "
                    + "ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(subjectTable);

            String courseTable = "CREATE TABLE IF NOT EXISTS Courses ("
                    + "Course_ID INT NOT NULL AUTO_INCREMENT, "
                    + "Subject_ID INT, "
                    + "Name varchar(200), "
                    + "Title varchar(500), "
                    + "Description varchar (3000), "
                    + "PRIMARY KEY(Course_ID), "
                    + "FOREIGN KEY(Subject_ID) REFERENCES Subject(Subject_ID)"
                    + ") "
                    + "ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci";
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

    public void insertSubject(String sub, int id) throws SQLException {
        String subject = sub.replace("'", "''");
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
        ResultSet rs = null;
        String course = courses.replace("'", "''");
        String title = titles.replace("'", "''");
        String description = desc.replace("'", "''");
        try {
            stmt = conn.createStatement();
            String queryID = "SELECT Subject_ID FROM `subject` WHERE NAME like '" + code + "%'";
            //stmt = conn.prepareStatement(queryID, Statement.RETURN_GENERATED_KEYS);
            rs = stmt.executeQuery(queryID);
            rs.next();
            int id = rs.getInt("Subject_ID");

            String query = "Insert into courses(Name, Subject_ID, Title, Description) values ("
                    + " '" + course + "',"
                    + " '" + id + "', "
                    + " '" + title + "', "
                    + " '" + description + "' "
                    + ")";
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

    public void closeConn() throws SQLException {
        conn.close();
    }

    public String getdbName() {
        return this.dbName;
    }

    public Connection getConnection() throws SQLException {
        return conn;
    }
}
