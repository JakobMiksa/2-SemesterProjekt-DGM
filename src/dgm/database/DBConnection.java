package dgm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private Connection connection;
    private static DBConnection dbConnection;

    private static String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String DB_NAME = System.getenv().getOrDefault("HILDUR_DB_NAME", "DMA-CSD-S252_10700486");
    private static String SERVER_ADDRESS = System.getenv().getOrDefault("HILDUR_DB_HOST", "hildur.ucn.dk");
    private static int SERVER_PORT = Integer.parseInt(System.getenv().getOrDefault("HILDUR_DB_PORT", "1433"));
    private static String USER_NAME = System.getenv().getOrDefault("HILDUR_DB_USER", "DMA-CSD-S252_10700486");
    private static String PASSWORD = System.getenv().getOrDefault("HILDUR_DB_PASS", "Password1!");

    private DBConnection() {
        String connectionString = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=true",
                SERVER_ADDRESS, SERVER_PORT, DB_NAME, USER_NAME, PASSWORD);

        try {
            Class.forName(DRIVER_CLASS);
            connection = DriverManager.getConnection(connectionString);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load JDBC driver", e);
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to database", e);
        }
    }

    public static DBConnection getInstance() {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void startTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Could not start transaction", e);
        }
    }

    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Could not commit transaction", e);
        }
    }

    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Could not rollback transaction", e);
        }
    }

    public int executeInsertWithIdentity(PreparedStatement ps) throws SQLException {
        int rows = ps.executeUpdate();
        if (rows <= 0) {
            return rows;
        }
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return rows;
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    public void closeConnection() {
        disconnect();
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public String getStatusMessage() {
        return "Database connection ready.";
    }
}
