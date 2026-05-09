package dgm.database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import dgm.model.SaleOrder;
import dgm.model.SaleOrderLine;

public class SaleOrderDB implements SaleOrderDAO {

    private static String INSERT_SALE_ORDER_QUERY =
            "insert into SaleOrder ([date], paymentMethod, total) values (?, ?, ?)";
    private static String INSERT_SALE_ORDER_LINE_QUERY =
            "insert into SaleOrderLine (saleOrderId, productNumber, quantity, unitPrice) " +
            "values (?, ?, ?, ?)";

    private DBConnection connection;
    private PreparedStatement insertSaleOrderPS;
    private PreparedStatement insertSaleOrderLinePS;

    public SaleOrderDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            insertSaleOrderPS = connection.getConnection().prepareStatement(INSERT_SALE_ORDER_QUERY,
                    Statement.RETURN_GENERATED_KEYS);
            insertSaleOrderLinePS = connection.getConnection().prepareStatement(INSERT_SALE_ORDER_LINE_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for SaleOrderDB");
        }
    }

    @Override
    public void save(SaleOrder order) throws DataAccessException {
        validateOrder(order);

        try {
            insertSaleOrderPS.setDate(1, Date.valueOf(order.getDate()));
            insertSaleOrderPS.setString(2, order.getPaymentMethod());
            insertSaleOrderPS.setBigDecimal(3, order.getTotal());

            int saleOrderId = connection.executeInsertWithIdentity(insertSaleOrderPS);

            for (SaleOrderLine orderLine : order.getOrderLines()) {
                insertSaleOrderLinePS.setInt(1, saleOrderId);
                insertSaleOrderLinePS.setInt(2, orderLine.getProduct().getProductNumber());
                insertSaleOrderLinePS.setInt(3, orderLine.getQuantity());
                insertSaleOrderLinePS.setBigDecimal(4, orderLine.getUnitPrice());
                insertSaleOrderLinePS.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not save sale order");
        }
    }

    private void validateOrder(SaleOrder order) throws DataAccessException {
        if (order == null) {
            throw new DataAccessException("Sale order is required");
        }

        if (order.getOrderLines() == null || order.getOrderLines().isEmpty()) {
            throw new DataAccessException("Sale order must have order lines");
        }
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
