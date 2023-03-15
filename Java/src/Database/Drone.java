package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Operations relate to drone in database
 * assigned pilots, fuel, weight
 * @Author Subhoodeep
 * @Doc Jiahui
 */

public class Drone extends DBconnecter {

    private static final String DRONE_INSERT =
            "INSERT INTO drone(droneId,storeName,weightCapacity,remainingCapacity,numberOfDeliveries,remainingFuel,assignedPilot,createdDate) values" +
                    " (?,?,?,?,'0',?,?,DATETIME('now'))";
    private static final String DRONE_SELECT = "SELECT * from drone where storeName = ?";
    private static final String DRONE_SELECT_CUSTOM = "SELECT * from drone where storeName = ? and droneId = ?";
    private static final String DRONE_UPDATE_INITIAL = "update drone set assignedPilot=null where assignedPilot=?";
    private static final String DRONE_UPDATE = "update drone set assignedPilot=? where storeName=? and droneId=?";
    private static final String DRONE_PILOT_UPDATE = "UPDATE pilot set assigned=true where account=?";
    private static final String DRONE_PURCHASE_UPDATE = "UPDATE drone set remainingFuel=?,numberOfDeliveries=?,remainingCapacity=? where storeName=? and droneId=?";

    // create a new drone and save in the database table
    public synchronized static void insertDrone(String store, String droneID, String weightCapacity, String numberOfDeliveries) {
        try {
            PreparedStatement insDrone = con.prepareStatement("PRAGMA foreign_keys = ON");
            insDrone.executeUpdate();
            insDrone = con.prepareStatement(DRONE_INSERT);
            insDrone.setString(1, droneID);
            insDrone.setString(2, store);
            insDrone.setString(3, weightCapacity);
            insDrone.setString(4, weightCapacity);
            insDrone.setString(5, numberOfDeliveries);
            insDrone.executeUpdate();
            insDrone.close();

            dbOperationStatus(null, "drone_identifier", "", "change", "ok");
        } catch (Exception e) {
            dbOperationStatus(e.getMessage(), "drone_identifier", "store_identifier", "change", "");
        }
    }

    /**
     * Select and show drones from database
     * @param store not null
     * @param droneId "" will be passed to display all drones;
     *                if droneId provided, the drone will write in memory for other operations to getting drones' information
     * @return
     */
    public synchronized static Service.Drone fetchDbDrone(String store, String droneId) {
        Service.Drone drone = null;
        try {
            PreparedStatement displayDrones;
            if (droneId.isBlank()) {
                displayDrones = con.prepareStatement(DRONE_SELECT);
                displayDrones.setString(1, store);
            } else {
                displayDrones = con.prepareStatement(DRONE_SELECT_CUSTOM);
                displayDrones.setString(1, store);
                displayDrones.setString(2, droneId);
            }

            ResultSet result = displayDrones.executeQuery();
            int ctr = 0;
            while (result.next()) {
                if (droneId.isBlank()) {
                    if (result.getString(7) == null)
                        System.out.printf(
                                "droneID:%s,total_cap:%s,num_orders:%s,remaining_cap:%s,trips_left:%s\n",
                                result.getString(1), result.getString(3), result.getString(5),
                                result.getString(4), result.getString(6));
                    else {
                        Service.Pilot pilot = Person.fetchDbPilot(result.getString(7));
                        System.out.printf(
                                "droneID:%s,total_cap:%s,num_orders:%s,remaining_cap:%s,trips_left:%s,flown_by:%s\n",
                                result.getString(1), result.getString(3), result.getString(5),
                                result.getString(4), result.getString(6), pilot.getFirstName() + "_" + pilot.getLastName());
                    }
                } else {
                    drone = new Service.Drone(result.getString(2), result.getString(1), result.getString(3), result.getString(4), result.getString(5), result.getString(6));
                    drone.setAssignedPilot(result.getString(7));
                }
                ctr++;
            }
            result.close();
            displayDrones.close();

            if (ctr == 0) {
                if (droneId.isBlank()) dbOperationStatus(null, "store_identifier", "", "display", "no_data");
                else dbOperationStatus(null, "drone_identifier", "", "display", "no_data");
            } else {
                if (droneId.isBlank()) dbOperationStatus(null, "drone_identifier", "", "display", "ok");
            }

            return drone;
        } catch (Exception e) {
            return null;
        }
    }

    // assign pilot to a drone
    public synchronized static void updateDbDrone(String store, String droneId, String pilotId) {
        try {
            PreparedStatement selectStore = con.prepareStatement(DRONE_SELECT);
            selectStore.setString(1, store);
            ResultSet result = selectStore.executeQuery();

            int ctr = 0;
            boolean droneFound = false;
            while (result.next()) {
                if (result.getString(1).equalsIgnoreCase(droneId))
                    droneFound = true;
                ctr++;
            }

            if (ctr == 0) dbOperationStatus(null, "store_identifier", "", "display", "no_data");
            if (!droneFound && ctr != 0) dbOperationStatus(null, "drone_identifier", "", "display", "no_data");
            else if (droneFound && ctr != 0) {
                try {
                    PreparedStatement updateDrone = con.prepareStatement("PRAGMA foreign_keys = ON");
                    updateDrone.executeUpdate();
                    updateDrone = con.prepareStatement(DRONE_UPDATE_INITIAL);
                    updateDrone.setString(1, pilotId);
                    updateDrone.executeUpdate();
                    updateDrone = con.prepareStatement(DRONE_UPDATE);
                    updateDrone.setString(1, pilotId);
                    updateDrone.setString(2, store.toLowerCase());
                    updateDrone.setString(3, droneId);
                    updateDrone.executeUpdate();

                    PreparedStatement updateDronePilot = con.prepareStatement(DRONE_PILOT_UPDATE);
                    updateDronePilot.setString(1, pilotId);
                    updateDronePilot.executeUpdate();
                    updateDrone.close();
                    updateDronePilot.close();

                    dbOperationStatus(null, "drone_identifier", "", "change", "ok");
                } catch (Exception e) {
                    dbOperationStatus(e.getMessage(), "", "pilot_identifier", "change", "");
                }
            }

            result.close();
            selectStore.close();
        } catch (Exception e) {
            System.out.println("updateDbDrone method threw exception : " + e.getMessage());
        }
    }

    // update fuel, weights and successful delivery after relate operations
    public synchronized static void updateDroneForRequestAndPurchase(String store, String droneID, int remainingFuel, int numberOfDeliveries, int remainingCapacity) {
        try {
            PreparedStatement updDrone = con.prepareStatement(DRONE_PURCHASE_UPDATE);
            updDrone.setString(1, String.valueOf(remainingFuel));
            updDrone.setString(2, String.valueOf(numberOfDeliveries));
            updDrone.setString(3, String.valueOf(remainingCapacity));
            updDrone.setString(4, store);
            updDrone.setString(5, droneID);
            updDrone.executeUpdate();
            updDrone.close();
        } catch (Exception e) {
            System.out.println("Error updating Drone for purchase : " + e.getMessage());
        }
    }

}
