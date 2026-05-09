package dgm.database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import dgm.model.Price;

public class PriceDB implements PriceDAO {

    private static String FIND_CURRENT_PRICE_QUERY =
            "select top 1 priceId, productNumber, amount, validFrom, validTo " +
            "from Price " +
            "where productNumber = ? and validFrom <= cast(getdate() as date) " +
            "and (validTo is null or validTo >= cast(getdate() as date)) " +
            "order by validFrom desc, priceId desc";

    private DBConnection connection;
    private PreparedStatement findCurrentPricePS;

    public PriceDB() throws DataAccessException {
        this.connection = DBConnection.getInstance();

        try {
            findCurrentPricePS = connection.getConnection().prepareStatement(FIND_CURRENT_PRICE_QUERY);
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not prepare statements for PriceDB");
        }
    }

    @Override
    public Price findCurrentPrice(int productNumber) throws DataAccessException {
        try {
            findCurrentPricePS.setInt(1, productNumber);

            try (ResultSet rs = findCurrentPricePS.executeQuery()) {
                if (rs.next()) {
                    return buildObject(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e, "Could not find current price");
        }
    }

    private Price buildObject(ResultSet rs) throws SQLException {
        Date validTo = rs.getDate("validTo");
        LocalDate validToDate = validTo == null ? null : validTo.toLocalDate();

        return new Price(
                rs.getBigDecimal("amount"),
                rs.getDate("validFrom").toLocalDate(),
                validToDate);
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
