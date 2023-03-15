package Service;

public class Line {
    private String orderId;
    private String store;
    private String itemName;
    private int quantity;
    private int price;

    public Line(String orderId, String store, String itemName, int quantity, int price) {
        this.orderId = orderId;
        this.store = store;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStore() {
        return store;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }
}
