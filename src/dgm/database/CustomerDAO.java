package dgm.database;

import dgm.model.Customer;

public interface CustomerDAO {

    Customer findByPhone(String phoneNo) throws DataAccessException;
}
