package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Operations relate to Line in database
 * @Author Subhoodeep
 * @Doc Jiahui
 */
public class Line extends DBconnecter {

    private static final String LINE_SELECT = "SELECT * from line where store = ? ";
    private static final String LINE_SELECT_CUSTOM = "SELECT * from line where store = ? and orderId=? ";

    // getting lines (item with total quantity and total price)
    public synchronized static List<Service.Line> fetchDbLine(String store, String orderId) {
        Service.Line line;
        try {
            PreparedStatement displayLines;
            List<Service.Line> lines = new ArrayList<>();
            if (orderId.isBlank()) {
                displayLines = con.prepareStatement(LINE_SELECT);
                displayLines.setString(1, store);
            } else {
                displayLines = con.prepareStatement(LINE_SELECT_CUSTOM);
                displayLines.setString(1, store);
                displayLines.setString(2, orderId);
            }

            ResultSet result = displayLines.executeQuery();
            while (result.next()) {
                line = new Service.Line(result.getString(1), result.getString(2), result.getString(3), Integer.parseInt(result.getString(4)), Integer.parseInt(result.getString(5)));
                lines.add(line);
            }

            result.close();
            displayLines.close();
            return lines;
        } catch (Exception e) {
            return null;
        }
    }
}
