package dgm.database;

import dgm.model.ReservedOrder;

public interface ReservedOrderDAO {

    void save(ReservedOrder order) throws DataAccessException;
}
