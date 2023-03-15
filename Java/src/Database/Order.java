package Database;

import Service.Customer;
import Service.Pilot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Operations relate to order in database
 * @Author Subhoodeep, Jiahui
 * @Doc Jiahui
 */

public class Order extends DBconnecter {
    private static final String ORDER_INSERT =
            "INSERT into 'order' (orderId,store,droneId,createdDate,customerAccount) values (?,?,?,DATETIME('now'),?)";
    private static final String ORDER_SELECT = "SELECT * from 'order' where store =? and status='PENDING'";
    private static final String ORDER_SELECT_CUSTOM = "SELECT * from 'order' where store =? and orderId=?";
    private static final String LINE_INSERT =
            "INSERT into line(orderId,store,itemName,quantity,price,createdDate) values (?,?,?,?,?,DATETIME('now'))";
    private static final String LINE_DELETE = "DELETE from line where store=? and orderId=?";
    private static final String ORDER_DELETE = "DELETE from 'order' where store=? and orderId=?";

    private static final String LINE_ARCHIVE = "DELETE from line where date(createdDate) <= DATE('now',?)";
    private static final String ORDER_ARCHIVE = "DELETE from 'order' where date(createdDate) <= DATE('now',?)";

    // start order with default pending status
    public synchronized static void insertOrder(String store, String orderId, String droneId, String custId) {
        try {
            PreparedStatement insOrder = con.prepareStatement("PRAGMA foreign_keys = ON");
            insOrder.executeUpdate();
            insOrder = con.prepareStatement(ORDER_INSERT);
            insOrder.setString(1, orderId);
            insOrder.setString(2, store);
            insOrder.setString(3, droneId);
            insOrder.setString(4, custId);
            insOrder.executeUpdate();
            insOrder.close();

            dbOperationStatus(null, "order_identifier", "", "change", "ok");
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                dbOperationStatus(e.getMessage(), "order_identifier", "store_identifier", "change", "");
            } else {
                // write order information to memory.
                // attempt to reduce the database access for the calculations and small updates of totalweights, totalprice for lines or drones
                Service.Store checkStore = Store.fetchDbStore(store);
                if (checkStore != null) {
                    Service.Drone checkDrone = Drone.fetchDbDrone(store, droneId);
                    if (checkDrone != null) {
                        Person.fetchDbCustomer(custId);
                    }
                }
            }
        }
    }

    /**
     * Select and show orders from database
     * @param store not null
     * @param orderId "" will be passed to display all order;
     *                if orderId provided, the order will write in memory for other operations to getting orders' information
     * @return
     */
    public synchronized static Service.Order fetchDbOrder(String store, String orderId) {
        Service.Order order = null;
        try {
            // run sqlite query
            PreparedStatement displayOrder;
            if (orderId.isBlank()) {
                displayOrder = con.prepareStatement(ORDER_SELECT);
                displayOrder.setString(1, store);
            } else {
                displayOrder = con.prepareStatement(ORDER_SELECT_CUSTOM);
                displayOrder.setString(1, store);
                displayOrder.setString(2, orderId);
            }

            ResultSet result = displayOrder.executeQuery();
            List<Service.Line> line = Line.fetchDbLine(store, "");
            //getting results: directly display message or write in memory
            int ctr = 0;
            while (result.next()) {
                if (orderId.isBlank()) {
                    System.out.println("orderID:" + result.getString(1));
                    if (line.size() > 0) {
                        for (Service.Line l : line) {
                            if (l.getOrderId().equals(result.getString(1))) {
                                Service.Item item = Item.fetchDbitem(store, l.getItemName());
                                System.out.println("item_name:" + l.getItemName() + ",total_quantity:" + l.getQuantity() + ",total_cost:" + (l.getQuantity() * l.getPrice()) +
                                        ",total_weight:" + (l.getQuantity() * item.getWeight()));
                            }
                        }
                    }
                } else
                    order = new Service.Order(result.getString(2), result.getString(1), result.getString(3), result.getString(5));
                ctr++;
            }

            // display ok or error message
            if (ctr == 0) {
                if (orderId.isBlank()) dbOperationStatus(null, "store_identifier", "", "display", "no_data");
                else dbOperationStatus(null, "order_identifier", "", "display", "no_data");
            } else {
                if (orderId.isBlank()) dbOperationStatus(null, "store_identifier", "", "display", "ok");
            }

            result.close();
            displayOrder.close();
            return order;
        } catch (Exception e) {
            System.out.println("Error displaying order : " + e.getMessage());
            return null;
        }
    }

    // add item to order
    public synchronized static void requestDbItem(String store, String orderId, String item, String quantity, String price) {
        // find order information from memory
        Service.Store dbstore = Store.fetchDbStore(store);
        int droneWeight;

        Service.Order order;
        if (dbstore != null) order = fetchDbOrder(store, orderId);
        else return;

        Service.Item it;
        if (order != null) it = Item.fetchDbitem(store, item);
        else return;

        Service.Customer customer;
        int availableCredit;
        if (it != null) {
            customer = Person.fetchDbCustomer(order.getCustomerAccount());
            if (customers.containsKey(customer.getAccount()))
                availableCredit = customers.getOrDefault(customer.getAccount(), customer).getCredit();
            else {
                customers.put(customer.getAccount(), customer);
                availableCredit = customer.getCredit();
            }
        } else return;

        Service.Drone drone;
        if (customer != null) {
            drone = Drone.fetchDbDrone(store, order.getDroneID());
            if (drones.containsKey(store + drone.getDroneID())) {
                droneWeight = drones.get(store + drone.getDroneID()).getRemainingCapacity();
            } else {
                drones.put(store + drone.getDroneID(), drone);
                droneWeight = drone.getRemainingCapacity();
            }
        } else return;

        // do calculation and check credit balance then run sqlite query
        if (drone != null) {
            int quantityTimesPrice = Integer.parseInt(quantity) * Integer.parseInt(price);
            if (quantityTimesPrice <= availableCredit) {
                if ((Integer.parseInt(quantity) * it.getWeight()) <= droneWeight) {
                    try {
                        customer.setCredit(availableCredit - quantityTimesPrice);
                        drone.setRemainingCapacity(droneWeight - (Integer.parseInt(quantity) * it.getWeight()));
                        PreparedStatement insLine = con.prepareStatement("PRAGMA foreign_keys = ON");
                        insLine.executeUpdate();
                        insLine = con.prepareStatement(LINE_INSERT);
                        insLine.setString(1, orderId);
                        insLine.setString(2, store);
                        insLine.setString(3, item);
                        insLine.setString(4, quantity);
                        insLine.setString(5, price);
                        insLine.executeUpdate();
                        insLine.close();

                        dbOperationStatus(null, "line_identifier", "", "change", "ok");
                        customers.put(customer.getAccount(), customer);
                        drones.put(store + drone.getDroneID(), drone);
                        Drone.updateDroneForRequestAndPurchase(store, drone.getDroneID(), drone.getRemainingFuel(), drone.getNumberOfDeliveries() + 1, drone.getRemainingCapacity());
                    } catch (Exception e) {
                        dbOperationStatus(e.getMessage(), "item_identifier", "", "change", "");
                    }
                } else {
                    System.out.println("ERROR:drone_cant_carry_new_item");
                }
            } else {
                System.out.println("ERROR:customer_cant_afford_new_item");
            }
        }
    }

    // purchase order and make it completed
    public synchronized static void purchaseOrder(String store, String orderId) {
        // find relate information from memory
        Service.Store storeObj = Store.fetchDbStore(store);
        Service.Order order;
        if (storeObj != null)
            order = Order.fetchDbOrder(store, orderId);
        else return;

        Service.Drone drone;
        if (order != null)
            drone = Drone.fetchDbDrone(store, order.getDroneID());
        else return;

        List<Service.Line> line;
        Service.Item item;
        Customer customer = Person.fetchDbCustomer(order.getCustomerAccount());
        Pilot pilot = null;
        if (drone.getAssignedPilot() != null)
            pilot = Person.fetchDbPilot(drone.getAssignedPilot());

        int tempRevenue = 0;
        int tempWeight = 0;
        if (drone.getAssignedPilot() == null)
            System.out.println("ERROR:drone_needs_pilot");
        else {
            if (drone.getRemainingFuel() == 0)
                System.out.println("ERROR:drone_needs_fuel");
            else {
                line = Line.fetchDbLine(store, orderId);
                for (Service.Line l : line) {
                    tempRevenue = (l.getPrice() * l.getQuantity());
                    item = Item.fetchDbitem(store, l.getItemName());
                    tempWeight = (l.getQuantity() * item.getWeight());
                }

                // update related tables, all following are Classes in Database package
                Store.updateStore(store, storeObj.getRevenue() + tempRevenue);
                Person.updateCustomer(customer.getAccount(), customer.getCredit() - tempRevenue);
                Drone.updateDroneForRequestAndPurchase(store, drone.getDroneID(), drone.getRemainingFuel() - 1,
                        drone.getNumberOfDeliveries() - 1, drone.getRemainingCapacity() + tempWeight);
                Person.updatePilot(pilot.getAccount(), pilot.getLevel() + 1);

                // update order status to complete and record the date.
                String ORDER_UPDATE = "update 'order' set status = ?, createdDate = ? where store=? and orderId=?";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String ts = sdf.format(timestamp);
                try {
                    PreparedStatement updateOrder = con.prepareStatement(ORDER_UPDATE);
                    updateOrder.setString(1, "COMPLETED");
                    updateOrder.setString(2, ts);
                    updateOrder.setString(3, store);
                    updateOrder.setString(4, orderId);
                    updateOrder.executeUpdate();
                    updateOrder.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    // cancel order and delete from database
    public synchronized static void deleteOrder(String store, String orderId) {
        Service.Store st = Store.fetchDbStore(store);
        Service.Order order;
        List<Service.Line> line;
        Service.Item item;
        int tempWeight = 0;

        if (st != null) order = Order.fetchDbOrder(store, orderId);
        else return;

        if (order != null) {
            line = Line.fetchDbLine(store, orderId);
            for (Service.Line l : line) {
                item = Item.fetchDbitem(store, l.getItemName());
                tempWeight = (l.getQuantity() * item.getWeight());
            }
        }

        Service.Drone drone;
        if (order != null) {
            drone = Drone.fetchDbDrone(store, order.getDroneID());
            try {
                PreparedStatement delLine = con.prepareStatement(LINE_DELETE);
                delLine.setString(1, store);
                delLine.setString(2, orderId);
                delLine.executeUpdate();
                delLine.close();

                PreparedStatement delOrder = con.prepareStatement(ORDER_DELETE);
                delOrder.setString(1, store);
                delOrder.setString(2, orderId);
                delOrder.executeUpdate();
                delOrder.close();

                Database.Drone.updateDroneForRequestAndPurchase(store, drone.getDroneID(), drone.getRemainingFuel(),
                        drone.getNumberOfDeliveries() - 1, drone.getRemainingCapacity() + tempWeight);
                dbOperationStatus(null, "del_order_identifier", "", "change", "ok");
            } catch (Exception e) {
                System.out.println("Error deleting Order : " + e.getMessage());
            }
        }
    }

    /**
     * Show all completed orders of given store ordered by orderID
     * @param store store identifier, which is the store name
     * @author Jiahui Cui
     */
    public synchronized static void orderHistory(String store) {
        String ORDER_HISTORY = "SELECT * from 'order' where store =? order by orderId;";
        String ORDER_LINE =
                "SELECT line.itemName, line.quantity, (line.quantity*line.price) as total_cost, (line.quantity*item.weight) as total_weight " +
                "from line inner join item on line.itemName = item.itemName and line.store = item.store " +
                "where line.store = ? and line.orderId = ? order by line.itemName;";

        try {
            // run sqlite query for orders
            PreparedStatement orderHistory;
            orderHistory = con.prepareStatement(ORDER_HISTORY);
            orderHistory.setString(1, store);
            ResultSet history = orderHistory.executeQuery();

            // display order information
            while(history.next()){
                System.out.println("orderID:" + history.getString("orderId") +
                        "-" + history.getString("status") + " " + history.getString("createdDate"));

                // run sqlite query for lines
                PreparedStatement orderLine;
                orderLine = con.prepareStatement(ORDER_LINE);
                orderLine.setString(1,store);
                orderLine.setString(2, history.getString("orderId"));
                ResultSet line = orderLine.executeQuery();

                // display lines for each order
                while(line.next()){
                    System.out.printf("item_name:%s,total_quantity:%s,total_cost:%s,total_weight:%s\n",
                            line.getString("itemName"), line.getString("quantity"),
                            line.getString("total_cost"), line.getString("total_weight"));
                }
            }
            System.out.println("OK:display_completed");
        } catch (SQLException e) {
            System.out.println("Error display Order History : " + e.getMessage());
        }
    }

    /**
     * Delete completed order by given days.
     * @param days positive integer
     * @author Jiahui Cui
     */
    public synchronized static void archiveOrder(String days) {
        try {
            // check days is valid
            int num = Integer.parseInt(days);
            if(num < 1) {
                System.out.println("Error archive Order : " + num);
                return;
            }
            // run sqlite query
            PreparedStatement archLine = con.prepareStatement(LINE_ARCHIVE);
            String formatDays = "-" + days + " day";
            archLine.setString(1, formatDays);
            archLine.executeUpdate();
            archLine.close();

            PreparedStatement archOrder = con.prepareStatement(ORDER_ARCHIVE);
            archOrder.setString(1, formatDays);
            archOrder.executeUpdate();
            archOrder.close();
            System.out.println("OK:change_completed");
        } catch (Exception e) {
            System.out.println("Error archive Order : " + e.getMessage());
        }
    }
}
