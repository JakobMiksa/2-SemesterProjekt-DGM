package dgm.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dgm.model.Product;

public class ProductDB {

    private DBConnection connection;

    public ProductDB() {
        this.connection = DBConnection.getInstance();
    }

    public List<Product> findAvailableProducts() {
        // TODO: Tilføj SQL senere
        return new ArrayList<>();
    }

    public Product findByProductNumber(int productNumber) {
        // TODO: Tilføj SQL senere
        return null;
    }

    private Product buildObject(ResultSet rs) {
        // TODO: Byg Product-objekt fra ResultSet senere
        return null;
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
