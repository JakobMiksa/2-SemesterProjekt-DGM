package dgm.database;

import java.sql.ResultSet;

import dgm.model.Price;

public class PriceDB {

    private DBConnection connection;

    public PriceDB() {
        this.connection = DBConnection.getInstance();
    }

    public Price findCurrentPrice(int productNumber) {
        // TODO: Tilføj SQL senere
        return null;
    }

    private Price buildObject(ResultSet rs) {
        // TODO: Byg Price-objekt fra ResultSet senere
        return null;
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
