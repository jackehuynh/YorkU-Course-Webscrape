package webscrape;

import java.io.*;
import java.io.PrintWriter;
import java.io.File;

/*  TO-DO:
    - Store courses in a DB somewhere to access and view
    - rewrite this code entirely to properly print out from ScrapeBrowser.java
 */
public class WriteToFile {

    private int deptSize;
    private int courseAmount;
    private File fileLocation = new File("C:/Users/Jackeh/Desktop/test2.txt");
    private PrintWriter printWriter;
    private String[] deptArray;
    private String[] courseList;

    public WriteToFile() throws IOException {
        printWriter = new PrintWriter(new FileWriter(this.fileLocation));
    }

    public void setDeptSize(int deptSize) {
        this.deptSize = deptSize;
    }

    public void setCourseAmount(int courseAmount) {
        this.courseAmount = courseAmount;
    }

    public void setDeptArray(String[] array) {
        this.deptArray = array;
    }

    public void setCourseArray(String[] array) {
        this.courseList = array;
    }

    public void printOutFile() {
        
        for (int i = 0; i < this.deptSize; i++) {
            printWriter.println(this.deptArray[i]);
            for (int j = 0; j < this.courseAmount; j++) {
                printWriter.println(this.courseList[j]);
            }
            printWriter.println("--------------------------------------------------");
        }
        
        /*
        for (int i = 0; i < this.deptSize; i++) {
            printWriter.println(this.deptArray[i]);
            for (int j = 0; j < this.courseAmount; j++) {
                printWriter.println(this.courseList[j]);
            }
        }
        printWriter.close();
        System.out.println("Text written to file, Courses amount written: " + courseAmount);
        
        
        for (int i = 0; i < this.result.length; i++) {
            printWriter.println(this.result[i]);
            System.out.println(result[i]);
        }
        printWriter.close();
         */
    }
}
