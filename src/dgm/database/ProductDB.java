package dgm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dgm.model.Product;
import dgm.model.ProductCategory;

public class ProductDB {

    private static String FIND_AVAILABLE_PRODUCTS_QUERY =
            "select p.productNumber, p.name, p.categoryName " +
            "from Product p " +
            "where exists (select 1 from StockItem si where si.productNumber = p.productNumber and si.availableQty > 0) " +
            "order by p.name";
    private static String FIND_BY_PRODUCT_NUMBER_QUERY =
            "select productNumber, name, categoryName from Product where productNumber = ?";

    private DBConnection connection;
    private PreparedStatement findAvailableProductsPS;
    private PreparedStatement findByProductNumberPS;

    public ProductDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            findAvailableProductsPS = connection.getConnection().prepareStatement(FIND_AVAILABLE_PRODUCTS_QUERY);
            findByProductNumberPS = connection.getConnection().prepareStatement(FIND_BY_PRODUCT_NUMBER_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for ProductDB");
        }
    }

    public List<Product> findAvailableProducts() throws DataAccessException {
        try (ResultSet rs = findAvailableProductsPS.executeQuery()) {
            List<Product> products = new ArrayList<>();

            while (rs.next()) {
                products.add(buildObject(rs));
            }

            return products;
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not find available products");
        }
    }

    public Product findByProductNumber(int productNumber) throws DataAccessException {
        try {
            findByProductNumberPS.setInt(1, productNumber);

            try (ResultSet rs = findByProductNumberPS.executeQuery()) {
                if (rs.next()) {
                    return buildObject(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not find product by number");
        }
    }

    private Product buildObject(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("productNumber"),
                rs.getString("name"),
                ProductCategory.valueOf(rs.getString("categoryName")));
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
