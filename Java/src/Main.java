import Database.DBconnecter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Entry point of whole program
 * Collect command from users, and open database connection
 * @Author Jiahui
 */
public class Main {

    public static void main(String[] args) {
        ThreadsRun();
    }

    public static void ThreadsRun(){
        // Cached Thread Pool will reuse free thread or create new thread automatically
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        //Open DB connection
        DBconnecter datasource = new DBconnecter();
        if (!datasource.openDbConnection()) {
            System.out.println("Error: Opening datasource");
        }

        //Program start message and start Scanner
        System.out.println("Welcome to the Grocery Express Delivery Service!");
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";

        while (true) {
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);

                //Using Callable Object to solve dependency
                List<Callable<Object>> calls = new ArrayList<>();
                calls.add(Executors.callable(new DeliveryService(tokens)));
                executor.invokeAll(calls, 50, TimeUnit.MILLISECONDS);

                //Stop point of program
                if (tokens[0].equals("stop"))
                    break;

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
            }
        }
        commandLineInput.close();
        executor.shutdown();
        datasource.closeDbConnection();
    }


}
