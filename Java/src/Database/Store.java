package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Operations relate to store in database
 * @Author Subhoodeep
 * @Doc Jiahui
 */
public class Store extends DBconnecter {

    private static final String STORE_INSERT =
            "INSERT INTO store(storeName,revenue,createdDate) values (?,?,DATETIME('now'))";
    private static final String STORE_SELECT_ALL = "SELECT * from store ";
    private static final String STORE_SELECT_CUSTOM = "SELECT * from store where storeName =? ";
    private static final String STORE_UPDATE = "UPDATE store set revenue=? where storeName =? ";

    // make a new store and save in database
    public synchronized static void insertStore(String storeName, int revenue) {
        try {
            PreparedStatement insStore = con.prepareStatement(STORE_INSERT);
            insStore.setString(1, storeName);
            insStore.setString(2, String.valueOf(revenue));
            insStore.executeUpdate();
            insStore.close();
            dbOperationStatus(null, "store_identifier", "", "change", "ok");
            stores.put(storeName,new Service.Store(storeName,String.valueOf(revenue)));
        } catch (Exception e) {
            dbOperationStatus(e.getMessage(), "store_identifier", "", "change", "");
        }
    }

    /**
     * /* During order insert same function is being called but with a store name to check if store exists.To Display all stores,
     * @param store "" will be passed for store
     *                if store provided, the store will write in memory for other operations to getting store's information
     */
    public synchronized static Service.Store fetchDbStore(String store) {
        Service.Store storeObj = null;
        try {
            PreparedStatement displayStores;
            if (store.isBlank())
                displayStores = con.prepareStatement(STORE_SELECT_ALL);
            else {
                displayStores = con.prepareStatement(STORE_SELECT_CUSTOM);
                displayStores.setString(1, store);
            }

            ResultSet result = displayStores.executeQuery();
            int ctr = 0;
            while (result.next()) {
                if (store.isBlank())
                    System.out.printf("name:%s,revenue:%s\n", result.getString(1), result.getString(2));
                else
                    storeObj = new Service.Store(store, result.getString(2));
                ctr++;
            }

            if (ctr == 0) {
                dbOperationStatus(null, "store_identifier", "", "display", "no_data");
            } else {
                if (store.isBlank())
                    dbOperationStatus(null, "store_identifier", "", "display", "ok");
            }

            result.close();
            displayStores.close();
            return storeObj;
        } catch (Exception e) {
            System.out.println("Error displaying store : " + e.getMessage());
            return null;
        }
    }

    // update store revenue
    public synchronized static void updateStore(String storeName, int revenue) {
        try {
            PreparedStatement updStore = con.prepareStatement(STORE_UPDATE);
            updStore.setString(1, String.valueOf(revenue));
            updStore.setString(2, storeName);
            updStore.executeUpdate();
            updStore.close();
        } catch (Exception e) {
            System.out.println("Error updating Store : " + e.getMessage());
        }
    }

}