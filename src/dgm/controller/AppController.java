package dgm.controller;

import dgm.database.DBConnection;

public class AppController {

    private final DBConnection dbConnection;

    public AppController() {
        this.dbConnection = new DBConnection();
    }

    public String getSystemStatus() {
        return "Systemstatus: Swing-skeleton klar. " + dbConnection.getStatusMessage();
    }
}

