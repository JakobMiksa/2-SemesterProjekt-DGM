package dgm.database;

import dgm.model.SaleOrder;

public class SaleOrderDB {

    private DBConnection connection;

    public SaleOrderDB() {
        this.connection = DBConnection.getInstance();
    }

    public void save(SaleOrder order) {
        // TODO: Tilføj SQL til at gemme SaleOrder senere
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
