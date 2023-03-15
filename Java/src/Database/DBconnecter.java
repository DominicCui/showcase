package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Database Creation, Opening and closing
 * @Author Subhoodeep, Jiahui
 */

public class DBconnecter {

    private static final String DB_NAME = "CS6310Group65.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    static Connection con;

    public static Map<String, Service.Pilot> dronePilots = new TreeMap<>();
    public static Map<String, Service.Customer> customers = new TreeMap<>();
    public static Map<String, Service.Store> stores = new TreeMap<>();
    public static Map<String, Service.Drone> drones = new TreeMap<>();

    public boolean openDbConnection() {
        try {
            con = DriverManager.getConnection(CONNECTION_STRING);
            return true;
        } catch (SQLException e) {
            System.out.println("Failed connecting to database: " + e.getMessage());
            return false;
        }
    }

    public void closeDbConnection() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Failed closing connection: " + e.getMessage());
        }
    }

    /**
     * Display feedback message after every database operation
     * @param strException the error message, could be null
     * @param identifier_self provide the identifier
     * @param identifier_fk if there is a foreign key needed
     * @param operation the command (database) operation
     * @param status distinguish ok or display
     */
    public synchronized static void dbOperationStatus(String strException, String identifier_self, String identifier_fk, String operation, String status) {
        if (status.equals("no_data")) {
            System.out.println("ERROR:" + identifier_self + "_does_not_exist");
        } else if (strException == null && status.equals("ok")) {
            System.out.println("OK:" + operation + "_completed");
        } else if (strException.contains("FOREIGN KEY constraint failed")) {
            System.out.println("ERROR:" + identifier_fk + "_does_not_exist");
        } else if (strException.contains("UNIQUE constraint failed: pilot.licenseId")) {
            System.out.println("ERROR:" + identifier_fk + "_already_exists");
        } else if (strException.contains("UNIQUE constraint failed: item.itemName, item.store")) {
            System.out.println("ERROR:item_identifier_already_exists");
        } else if (strException.contains("UNIQUE constraint failed: line.store, line.orderId, line.itemName)")){
            System.out.println("ERROR:item_already_ordered");
        } else {
            System.out.println("ERROR:" + identifier_self + "_already_exists");
        }
    }

}