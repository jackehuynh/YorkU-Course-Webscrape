package webscrape;

import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.File;

/*
    TO-DO:
    Store courses in a DB somewhere
 */
public class WriteToFile {

    public WriteToFile() {

    }

    public static void Write(String data) {
        ArrayList<String> store = new ArrayList<String>();
        String print = data;
        store.add(print);
        int sizeArray;

        try {
            File file = new File("C:/Users/Jackeh/Desktop/test2.txt");
            PrintWriter printWriter = new PrintWriter(file);
            for (int i = 0; i < 10; i++) {
                printWriter.println(print);
            }
            System.out.println(print);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int setSize(int size) {
        int sizeArray = size;
        return sizeArray;
    }

}
