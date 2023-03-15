/**
 * A service distribute for each command running as a thread
 * @Author Jiahui
 */
public class DeliveryService implements Runnable{
    private final String[] tokens;

    public DeliveryService(String[] tokens){
        this.tokens = tokens;
    }

    @Override
    public void run() {
        switch (tokens[0]) {
            case "make_store":
                //System.out.println("store: " + tokens[1] + ", revenue: " + tokens[2]);
                Database.Store.insertStore(tokens[1], Integer.parseInt(tokens[2]));
                break;
            case "display_stores":
                //System.out.println("no parameters needed");
                Database.Store.fetchDbStore("");
                break;
            case "sell_item":
                //System.out.println("store: " + tokens[1] + ", item: " + tokens[2] + ", weight: " + tokens[3]);
                Database.Item.insertItem(tokens[1], tokens[2], tokens[3]);
                break;
            case "display_items":
                 Database.Item.fetchDbitem(tokens[1], "");
                break;
            case "make_pilot":
                //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                //System.out.println(", phone: " + tokens[4] + ", tax: " + tokens[5] + ", license: " + tokens[6] + ", experience: " + tokens[7]);
                Database.Person.insertPilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
                break;
            case "display_pilots":
                //System.out.println("no parameters needed");
                Database.Person.fetchDbPilot("");
                break;
            case "make_drone":
                //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", capacity: " + tokens[3] + ", fuel: " + tokens[4]);
                Database.Drone.insertDrone(tokens[1], tokens[2], tokens[3], tokens[4]);
                break;
            case "display_drones":
                //System.out.println("store: " + tokens[1]);
                Database.Drone.fetchDbDrone(tokens[1], "");
                break;
            case "fly_drone":
                //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", pilot: " + tokens[3]);
                Database.Drone.updateDbDrone(tokens[1], tokens[2], tokens[3]);
                break;
            case "make_customer":
                //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                //System.out.println(", phone: " + tokens[4] + ", rating: " + tokens[5] + ", credit: " + tokens[6]);
                Database.Person.insertCustomer(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
                break;
            case "display_customers":
                //System.out.println("no parameters needed");
                Database.Person.fetchDbCustomer("");
                break;
            case "start_order":
                //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", drone: " + tokens[3] + ", customer: " + tokens[4]);
                Database.Order.insertOrder(tokens[1], tokens[2], tokens[3], tokens[4]);
                break;
            case "display_orders":
                //System.out.println("store: " + tokens[1]);
                Database.Order.fetchDbOrder(tokens[1], "");
                break;
            case "request_item":
                //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);
                Database.Order.requestDbItem(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
                break;
            case "purchase_order":
                //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                Database.Order.purchaseOrder(tokens[1], tokens[2]);
                break;
            case "cancel_order":
                //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                Database.Order.deleteOrder(tokens[1], tokens[2]);
                break;
            case "order_history":
                Database.Order.orderHistory(tokens[1]);
                break;
            case "archive_orders":
                Database.Order.archiveOrder(tokens[1]);
                break;
            case "stop":
                System.out.println("stop acknowledged");
                System.out.println("simulation terminated");
                break;
            default:
                System.out.println("command " + tokens[0] + " NOT acknowledged");
                break;
        }
    }
}
