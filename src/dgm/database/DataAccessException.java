package dgm.database;

public class DataAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(Exception e, String explanation) {
        super(explanation, e);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
