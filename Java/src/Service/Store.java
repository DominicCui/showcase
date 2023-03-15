package Service;

import java.util.*;

public class Store{
    private String storeName;
    private int revenue;
    Map<String,Drone> drones = new HashMap<>();
    Map<String, Item> items = new HashMap<>();

    public Store(String storeName, String revenue){
        this.storeName = storeName;
        this.revenue = Integer.parseInt(revenue);
    }

    public void sellItem(String itemName, Item item){
        items.put(itemName, item);
    }

    public void buyDrone(String droneid, Drone drone){
        drones.put(droneid, drone);
    }

    public int getRevenue() {
        return revenue;
    }
}