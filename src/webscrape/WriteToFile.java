package webscrape;

import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;

/*  TO-DO:
    - Store courses in a DB somewhere to access and view
 */
public class WriteToFile {

    private File fileLocation = new File("C:/Users/Jackeh/Desktop/test2.txt");;
    private PrintWriter printWriter;
    private String[] result;

    public WriteToFile() throws FileNotFoundException {
        printWriter = new PrintWriter(this.fileLocation);
    }

    public void setResult(String[] result) {
        this.result = result;
    }

    public void printOutFile() {
        for (int i = 0; i < this.result.length; i++) {
            printWriter.println(this.result[i]);
            System.out.println(result[i]);
        }
        printWriter.close();
    }

}
