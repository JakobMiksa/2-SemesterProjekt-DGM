package dgm.database;

import dgm.model.Product;

public interface ProductDAO {

    Product findByProductNumber(int productNumber) throws DataAccessException;
}
