package dgm.database;

import dgm.model.SaleOrder;

public interface SaleOrderDAO {

    void save(SaleOrder order) throws DataAccessException;
}
