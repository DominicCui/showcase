package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Operations relate to Item in database
 * @Author Subhoodeep
 * @Doc Jiahui
 */

public class Item extends DBconnecter {
    private static final String ITEM_INSERT =
            "INSERT INTO item(itemName,store,weight,createdDate) values (?,?,?,DATETIME('now'))";
    private static final String ITEM_SELECT = "SELECT * from item where store= ? order by itemName";
    private static final String ITEM_SELECT_CUSTOM = "SELECT * from item where store= ? and itemName =?";

    // add new sell item to database
    public synchronized static  void insertItem(String store, String item, String weight) {
        try {
            PreparedStatement insItem = con.prepareStatement("PRAGMA foreign_keys = ON");
            insItem.executeUpdate();
            insItem = con.prepareStatement(ITEM_INSERT);
            insItem.setString(1, item);
            insItem.setString(2, store);
            insItem.setString(3, weight);
            insItem.executeUpdate();
            insItem.close();

            dbOperationStatus(null, "item_identifier", "store_identifier", "change", "ok");
        } catch (Exception e) {
            dbOperationStatus(e.getMessage(), "item_identifier", "store_identifier", "change", "");
        }
    }

    /**
     * Display items of selected store
     * @param store not null
     * @param item "" will be passed to display all items;
     *                if item provided, the item will write in memory for other operations to getting item information
     * @return
     */
    public synchronized static Service.Item fetchDbitem(String store, String item) {
        Service.Item it = null;
        try {
            // sqlite query execute
            PreparedStatement displayItems;
            if (item.isBlank()) {
                displayItems = con.prepareStatement(ITEM_SELECT);
                displayItems.setString(1, store);
            } else {
                displayItems = con.prepareStatement(ITEM_SELECT_CUSTOM);
                displayItems.setString(1, store);
                displayItems.setString(2, item);
            }

            // getting results
            ResultSet result = displayItems.executeQuery();
            int ctr = 0;
            while (result.next()) {
                if (item.isBlank()) System.out.println(result.getString(1) + "," + result.getString(3));
                else it = new Service.Item(store, item, result.getString(3));
                ctr++;
            }

            // display message
            if (ctr == 0) {
                if (item.isBlank()) dbOperationStatus(null, "store_identifier", "", "display", "no_data");
                else dbOperationStatus(null, "item_identifier", "", "display", "no_data");
            } else {
                if (item.isBlank()) dbOperationStatus(null, "store_identifier", "", "display", "ok");
            }

            result.close();
            displayItems.close();
            return it;
        } catch (Exception e) {
            System.out.println("Error displaying items : " + e.getMessage());
            return null;
        }
    }

}

