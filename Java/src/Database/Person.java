package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Operations relate to person in database
 * There are only two type of user in our program, pilot and customer.
 * If there are more type user provided in the future, shall separate in to different class.
 * @Author Subhoodeep
 * @Doc Jiahui
 */
public class Person extends DBconnecter {

    private static final String PERSON_INSERT =
            "INSERT INTO person (account,firstName,lastName,phoneNumber,createdDate) values (?,?,?,?,DATETIME('now'))";

    private static final String PILOT_INSERT =
            "INSERT INTO pilot(account,licenseId,taxId,level,assigned,createdDate) values (?,?,?,?,?,DATETIME('now'));";
    private static final String PILOT_SELECT = "SELECT * from person,pilot where person.account=pilot.account";
    private static final String PILOT_SELECT_CUSTOM = "SELECT * from person,pilot where person.account=pilot.account and pilot.account=?";
    private static final String PILOT_UPDATE = "UPDATE pilot set level= ? where account=?";

    private static final String CUSTOMER_INSERT = "INSERT INTO customer(rating,credit,account,createdDate) values (?,?,?,DATETIME('now'))";
    private static final String CUSTOMER_SELECT = "SELECT * from person,customer where person.account=customer.account";
    private static final String CUSTOMER_SELECT_CUSTOM = "SELECT * from customer where account=?";
    private static final String CUSTOMER_UPDATE = "UPDATE customer set credit= ? where account=?";

    // save new pilot to database
    public synchronized static void insertPilot(String account, String first, String last, String phoneNumber, String taxId, String licenseId, String level) {
        try {
            con.setAutoCommit(false);
            PreparedStatement insPerson = con.prepareStatement(PERSON_INSERT);
            insPerson.setString(1, account);
            insPerson.setString(2, first);
            insPerson.setString(3, last);
            insPerson.setString(4, phoneNumber);
            insPerson.executeUpdate();

            PreparedStatement insPilot = con.prepareStatement("PRAGMA foreign_keys = ON");
            insPilot.executeUpdate();
            insPilot = con.prepareStatement(PILOT_INSERT);
            insPilot.setString(1, account);
            insPilot.setString(2, licenseId);
            insPilot.setString(3, taxId);
            insPilot.setString(4, level);
            insPilot.executeUpdate();
            con.commit();

            insPilot.close();
            insPerson.close();
            dbOperationStatus(null, "pilot_identifier", "pilot_license", "change", "ok");
            dronePilots.put(account, new Service.Pilot(account, first, last, phoneNumber, taxId, licenseId, level));
        } catch (Exception e) {
            dbOperationStatus(e.getMessage(), "pilot_identifier", "pilot_license", "change", "");
        }
    }

    // save new customer to database
    public synchronized static void insertCustomer(String account, String first, String last, String phoneNumber, String rating, String credit) {
        try {
            PreparedStatement insPerson = con.prepareStatement(PERSON_INSERT);
            insPerson.setString(1, account);
            insPerson.setString(2, first);
            insPerson.setString(3, last);
            insPerson.setString(4, phoneNumber);
            insPerson.executeUpdate();

            PreparedStatement insCustomer = con.prepareStatement("PRAGMA foreign_keys = ON");
            insCustomer.executeUpdate();
            insCustomer = con.prepareStatement(CUSTOMER_INSERT);
            insCustomer.setString(1, rating);
            insCustomer.setString(2, credit);
            insCustomer.setString(3, account);
            insCustomer.executeUpdate();

            insPerson.close();
            insCustomer.close();
            dbOperationStatus(null, "customer_identifier", "pilot_license", "change", "ok");
        } catch (Exception e) {
            dbOperationStatus(e.getMessage(), "customer_identifier", "pilot_license", "change", "");
        }
    }

    /**
     *
     * @param pilotId "" will be passed to display all pilots;
     *                if pilotId provided, the pilot will write in memory for other operations to getting pilots' information
     * @return pilot entity
     */

    public synchronized static Service.Pilot fetchDbPilot(String pilotId) {
        Service.Pilot pilot = null;
        try {
            // run sqlite query
            PreparedStatement displayPilots;
            if (pilotId.isBlank())
                displayPilots = con.prepareStatement(PILOT_SELECT);
            else {
                displayPilots = con.prepareStatement(PILOT_SELECT_CUSTOM);
                displayPilots.setString(1, pilotId);
            }

            // getting results
            ResultSet result = displayPilots.executeQuery();
            int ctr = 0;
            while (result.next()) {
                if (pilotId.isBlank())
                    System.out.printf(
                            "name:%s_%s,phone:%s,taxID:%s,licenseID:%s,experience:%s%n",
                            result.getString(2), result.getString(3), result.getString(4),
                            result.getString(9), result.getString(8), result.getString(10));
                else
                    pilot = new Service.Pilot(result.getString(1), result.getString(2), result.getString(3),
                            result.getString(4), result.getString(9), result.getString(8), result.getString(10));
                ctr++;
            }

            // display message
            if (ctr == 0) {
                dbOperationStatus(null, "Pilot", "", "display", "no_data");
            } else {
                if (pilotId.isBlank())
                    dbOperationStatus(null, "pilot_identifier", "", "display", "ok");
            }

            result.close();
            displayPilots.close();
            return pilot;
        } catch (Exception e) {
            System.out.println("Failed displaying Pilots : " + e.getMessage());
            return null;
        }
    }

    /**
     * During order insert same function is being called but with a custId to check if custId exists.
     * @param custId "" will be passed for custId to display all customers
     * @return customer entity
     */
    public synchronized static Service.Customer fetchDbCustomer(String custId) {
        Service.Customer cust = null;
        try {
            // run sqlite query
            PreparedStatement displayCustomer;
            if (custId.isBlank())
                displayCustomer = con.prepareStatement(CUSTOMER_SELECT);
            else {
                displayCustomer = con.prepareStatement(CUSTOMER_SELECT_CUSTOM);
                displayCustomer.setString(1, custId);
            }

            //getting results
            ResultSet result = displayCustomer.executeQuery();
            int ctr = 0;
            while (result.next()) {
                if (custId.isBlank())
                    System.out.printf(
                            "name:%s_%s,phone:%s,rating:%s,credit:%s\n",
                            result.getString(2), result.getString(3), result.getString(4),
                            result.getString(7), result.getString(8));
                else
                    cust = new Service.Customer(result.getString(4), "", "", "", result.getString(2), result.getString(3));
                ctr++;
            }

            // display message
            if (ctr == 0) {
                dbOperationStatus(null, "customer_identifier", "", "display", "no_data");
            } else {
                if (custId.isBlank())
                    dbOperationStatus(null, "Customer", "", "display", "ok");
            }

            result.close();
            displayCustomer.close();
            return cust;
        } catch (Exception e) {
            System.out.println("Failed displaying Customers : " + e.getMessage());
            return null;

        }
    }

    // update the credit of customer
    public synchronized static void updateCustomer(String account, int credit) {
        try {
            PreparedStatement updCustomer = con.prepareStatement(CUSTOMER_UPDATE);
            updCustomer.setString(1, String.valueOf(credit));
            updCustomer.setString(2, account);
            updCustomer.executeUpdate();
            updCustomer.close();
        } catch (Exception e) {
            System.out.println("Error updating customer : " + e.getMessage());
        }
    }

    // update the experience level of pilot
    public synchronized static void updatePilot(String account, int level) {
        try {
            PreparedStatement updPilot = con.prepareStatement(PILOT_UPDATE);
            updPilot.setString(1, String.valueOf(level));
            updPilot.setString(2, account);
            updPilot.executeUpdate();
            updPilot.close();
            dbOperationStatus(null, "Customer", "", "change", "ok");
        } catch (Exception e) {
            System.out.println("Error updating pilot : " + e.getMessage());
        }
    }

}
