package dgm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dgm.model.Product;
import dgm.model.ProductCategory;

public class ProductDB implements ProductDAO {

    private static String FIND_BY_PRODUCT_NUMBER_QUERY =
            "select productNumber, name, categoryName from Product where productNumber = ?";

    private DBConnection connection;
    private PreparedStatement findByProductNumberPS;

    public ProductDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            findByProductNumberPS = connection.getConnection().prepareStatement(FIND_BY_PRODUCT_NUMBER_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for ProductDB");
        }
    }

    @Override
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
