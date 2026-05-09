package dgm.database;

import java.util.List;

import dgm.model.StockItem;

public interface StockItemDAO {

    StockItem findAvailableStock(int productNumber) throws DataAccessException;

    List<StockItem> findAllAvailableStock() throws DataAccessException;

    void decreaseAvailableQty(int productNumber, int quantity) throws DataAccessException;
}
