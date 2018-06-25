package webscrape;

import java.awt.AWTException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartScraper {

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        //final int cpuCount = Runtime.getRuntime().availableProcessors();
        final int cpuCount = 1;
        System.out.println("CPU count is: " + cpuCount);
        ExecutorService executor = Executors.newFixedThreadPool(cpuCount);

        try {
            for (int i = 0; i < 184; i++) {
                String threadCount = "thread " + i;
                executor.submit(new ScrapeCourse(threadCount, i, i + 1));
            }
            System.out.println("Tasks finished submitting.");
        } finally {
            executor.shutdown();
        }
    }
}
