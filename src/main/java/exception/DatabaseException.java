package exception;

import java.sql.SQLException;

public class DatabaseException extends LibraryException {
    public DatabaseException(String message, SQLException cause) {
        super(message, cause);
    }
}
