package Service;

import java.util.Map;
import java.util.TreeMap;

public class Drone {
    private final String store;
    private final String droneID;
    private final int weightCapacity;
    private int remainingCapacity;
    private final int numberOfDeliveries;
    private int remainingFuel;
    String assignedPilot = null;
    Map<String, Order> carryOnOrders = new TreeMap<>();

    public Drone(String store, String droneID, String weightCapacity, String remainingCapacity, String numberOfDeliveries, String remainingFuel) {
        this.store = store;
        this.droneID = droneID;
        this.weightCapacity = Integer.parseInt(weightCapacity);
        this.remainingCapacity = Integer.parseInt(weightCapacity);
        this.remainingCapacity = Integer.parseInt(remainingCapacity);
        this.numberOfDeliveries = Integer.parseInt(numberOfDeliveries);
        this.remainingFuel = Integer.parseInt(numberOfDeliveries);
        this.remainingFuel = Integer.parseInt(remainingFuel);
    }


    public void delivery(String orderID) {
        remainingCapacity += carryOnOrders.get(orderID).getTotalWeight();
        remainingFuel -= 1;
        carryOnOrders.remove(orderID);
        //assignedPilot.level+=1;
    }

    public int getNumberOfDeliveries() {
        return numberOfDeliveries;
    }

    public int getNumOfOrders() {
        return numberOfDeliveries;
    }

    public String getDroneID() {
        return droneID;
    }

    public int getWeightCapacity() {
        return weightCapacity;
    }

    public int getRemainingCapacity() {
        return remainingCapacity;
    }

    public void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public int getRemainingFuel() {
        return remainingFuel;
    }

    public String getAssignedPilot() {
        return assignedPilot;
    }

    public void setAssignedPilot(String assignedPilot) {
        this.assignedPilot = assignedPilot;
    }
}

