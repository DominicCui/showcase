package Service;

import java.util.*;

public class Order {
    private String store;
    private final String orderID;
    private final String droneID;
    private final String customerAccount;
    List<Item> itemList = new ArrayList<>();

    public Order(String store, String orderID, String droneID, String customerAccount){
        this.store = store;
        this.orderID = orderID;
        this.droneID = droneID;
        this.customerAccount = customerAccount;
    }

    public void addItem(Item item){
        itemList.add(item);
    }

    public int getTotalCost(){
        int sum = 0;
        for(Item item : itemList)
            sum += item.getTotalCost();
        return sum;
    }

    public int getTotalWeight(){
        int sum = 0;
        for(Item item : itemList)
            sum += item.getWeight();
        return sum;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getDroneID() {
        return droneID;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }
}