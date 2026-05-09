package dgm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dgm.model.Location;
import dgm.model.Product;
import dgm.model.ProductCategory;
import dgm.model.StockItem;

public class StockItemDB implements StockItemDAO {

    private static String FIND_AVAILABLE_STOCK_QUERY =
            "select top 1 si.stockItemId, si.availableQty, si.expirationDate, " +
            "p.productNumber, p.name as productName, p.categoryName, l.name as locationName " +
            "from StockItem si " +
            "join Product p on p.productNumber = si.productNumber " +
            "join Location l on l.locationId = si.locationId " +
            "where si.productNumber = ? and si.availableQty > 0 " +
            "order by si.expirationDate, si.stockItemId";
    private static String FIND_ALL_AVAILABLE_STOCK_QUERY =
            "select si.stockItemId, si.availableQty, si.expirationDate, " +
            "p.productNumber, p.name as productName, p.categoryName, l.name as locationName " +
            "from StockItem si " +
            "join Product p on p.productNumber = si.productNumber " +
            "join Location l on l.locationId = si.locationId " +
            "where si.availableQty > 0 " +
            "order by p.productNumber, si.expirationDate, si.stockItemId";
    private static String DECREASE_AVAILABLE_QTY_QUERY =
            "with SelectedStock as (" +
            "select top 1 stockItemId from StockItem " +
            "where productNumber = ? and availableQty >= ? " +
            "order by expirationDate, stockItemId) " +
            "update StockItem set availableQty = availableQty - ? " +
            "where stockItemId in (select stockItemId from SelectedStock)";

    private DBConnection connection;
    private PreparedStatement findAvailableStockPS;
    private PreparedStatement findAllAvailableStockPS;
    private PreparedStatement decreaseAvailableQtyPS;

    public StockItemDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            findAvailableStockPS = connection.getConnection().prepareStatement(FIND_AVAILABLE_STOCK_QUERY);
            findAllAvailableStockPS = connection.getConnection().prepareStatement(FIND_ALL_AVAILABLE_STOCK_QUERY);
            decreaseAvailableQtyPS = connection.getConnection().prepareStatement(DECREASE_AVAILABLE_QTY_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for StockItemDB");
        }
    }

    @Override
    public StockItem findAvailableStock(int productNumber) throws DataAccessException {
        try {
            findAvailableStockPS.setInt(1, productNumber);

            try (ResultSet rs = findAvailableStockPS.executeQuery()) {
                if (rs.next()) {
                    return buildObject(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not find available stock");
        }
    }

    @Override
    public List<StockItem> findAllAvailableStock() throws DataAccessException {
        try (ResultSet rs = findAllAvailableStockPS.executeQuery()) {
            List<StockItem> stockItems = new ArrayList<>();

            while (rs.next()) {
                stockItems.add(buildObject(rs));
            }

            return stockItems;
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not find all available stock");
        }
    }

    @Override
    public void decreaseAvailableQty(int productNumber, int quantity) throws DataAccessException {
        try {
            decreaseAvailableQtyPS.setInt(1, productNumber);
            decreaseAvailableQtyPS.setInt(2, quantity);
            decreaseAvailableQtyPS.setInt(3, quantity);

            int rows = decreaseAvailableQtyPS.executeUpdate();

            if (rows == 0) {
                throw new DataAccessException("Stock too low for product " + productNumber);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not decrease available quantity");
        }
    }

    private StockItem buildObject(ResultSet rs) throws SQLException {
        Product product = new Product(
                rs.getInt("productNumber"),
                rs.getString("productName"),
                ProductCategory.valueOf(rs.getString("categoryName")));
        Location location = new Location(rs.getString("locationName"));

        return new StockItem(
                product,
                location,
                rs.getInt("availableQty"),
                rs.getDate("expirationDate").toLocalDate());
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
