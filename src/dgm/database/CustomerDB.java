package dgm.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dgm.model.Customer;

public class CustomerDB {

    private static String FIND_BY_PHONE_QUERY = "select phoneNo, name from Customer where phoneNo = ?";

    private DBConnection connection;
    private PreparedStatement findByPhonePS;

    public CustomerDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            findByPhonePS = connection.getConnection().prepareStatement(FIND_BY_PHONE_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for CustomerDB");
        }
    }

    public Customer findByPhone(String phoneNo) throws DataAccessException {
        try {
            findByPhonePS.setString(1, phoneNo);

            try (ResultSet rs = findByPhonePS.executeQuery()) {
                if (rs.next()) {
                    return buildObject(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not find customer by phone");
        }
    }

    private Customer buildObject(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("name"),
                rs.getString("phoneNo"));
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
