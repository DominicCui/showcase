package Service;

public class Item {
    private final String store;
    private final String itemName;
    private final int weight;
    int unitPrice;
    int quantity;

    public Item(String store, String item, String weight){
        this.store = store;
        this.itemName = item;
        this.weight = Integer.parseInt(weight);
    }

    public int getTotalWeight(){
        return weight*quantity;
    }

    public int getTotalCost(){
        return unitPrice*quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStore() {
        return store;
    }

    public String getItemName() {
        return itemName;
    }

    public int getWeight() {
        return weight;
    }
}
