package dgm.database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import dgm.model.ReservedOrder;
import dgm.model.ReservedOrderLine;

public class ReservedOrderDB {

    private static String INSERT_RESERVED_ORDER_QUERY =
            "insert into ReservedOrder (customerPhoneNo, [date], expiryDate, paymentMethod, total) " +
            "values (?, ?, ?, ?, ?)";
    private static String INSERT_RESERVED_ORDER_LINE_QUERY =
            "insert into ReservedOrderLine (reservedOrderId, productNumber, quantity, unitPrice) " +
            "values (?, ?, ?, ?)";

    private DBConnection connection;
    private PreparedStatement insertReservedOrderPS;
    private PreparedStatement insertReservedOrderLinePS;

    public ReservedOrderDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            insertReservedOrderPS = connection.getConnection().prepareStatement(INSERT_RESERVED_ORDER_QUERY,
                    Statement.RETURN_GENERATED_KEYS);
            insertReservedOrderLinePS = connection.getConnection().prepareStatement(INSERT_RESERVED_ORDER_LINE_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for ReservedOrderDB");
        }
    }

    public void save(ReservedOrder order) throws DataAccessException {
        validateOrder(order);

        try {
            insertReservedOrderPS.setString(1, order.getCustomer().getPhoneNo());
            insertReservedOrderPS.setDate(2, Date.valueOf(order.getDate()));
            insertReservedOrderPS.setDate(3, Date.valueOf(order.getExpiryDate()));
            insertReservedOrderPS.setString(4, order.getPaymentMethod());
            insertReservedOrderPS.setBigDecimal(5, order.getTotal());

            int reservedOrderId = connection.executeInsertWithIdentity(insertReservedOrderPS);

            for (ReservedOrderLine orderLine : order.getOrderLines()) {
                insertReservedOrderLinePS.setInt(1, reservedOrderId);
                insertReservedOrderLinePS.setInt(2, orderLine.getProduct().getProductNumber());
                insertReservedOrderLinePS.setInt(3, orderLine.getQuantity());
                insertReservedOrderLinePS.setBigDecimal(4, orderLine.getUnitPrice());
                insertReservedOrderLinePS.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not save reserved order");
        }
    }

    private void validateOrder(ReservedOrder order) throws DataAccessException {
        if (order == null) {
            throw new DataAccessException("Reserved order is required");
        }

        if (order.getCustomer() == null) {
            throw new DataAccessException("Reserved order must have a customer");
        }

        if (order.getOrderLines() == null || order.getOrderLines().isEmpty()) {
            throw new DataAccessException("Reserved order must have order lines");
        }
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
