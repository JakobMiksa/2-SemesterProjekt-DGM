package dgm.database;

import dgm.model.ReservedOrder;

public class ReservedOrderDB {

    private DBConnection connection;

    public ReservedOrderDB() {
        this.connection = DBConnection.getInstance();
    }

    public void save(ReservedOrder order) {
        // TODO: Tilføj SQL til at gemme ReservedOrder senere
    }

    public DBConnection getConnection() {
        return connection;
    }

    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }
}
