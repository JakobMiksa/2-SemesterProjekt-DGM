package dgm.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

	private Connection connection;
	private static DBConnection instance;
	
	private DBConnection() {
	}
	
	public static DBConnection getInstance() {
	    if (instance == null) {
	        instance = new DBConnection();
	    }
	    return instance;
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
		} catch(SQLException e) {
			throw new RuntimeException("kunne ikke starte transaktionen", e);
		}
	}
	
	public void commitTransaktion() {
		try {
			connection.commit();
			connection.setAutoCommit(true);
		} catch(SQLException e) {
			throw new RuntimeException("Kunne ikke udføre transaktionen", e);
		}
	}
	
	public void rollbackTransaktion() {
		try {
			connection.rollback();
			connection.setAutoCommit(true);
		}catch(SQLException e) {
			throw new RuntimeException("Kunne ikke rulle transaktionen tilbage", e);
		}
	}
	
	public void closeConnection() {
	    try {
	        if (connection != null) {
	            connection.close();
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("Kunne ikke lukke databaseforbindelsen.", e);
	    }
	}

    public String getStatusMessage() {
        return "Database er ikke sat op endnu.";
    }
}

