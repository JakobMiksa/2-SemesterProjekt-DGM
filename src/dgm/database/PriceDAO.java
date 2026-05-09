package dgm.database;

import dgm.model.Price;

public interface PriceDAO {

    Price findCurrentPrice(int productNumber) throws DataAccessException;
}
