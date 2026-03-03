package dataaccess.exception;

public class NoMatchException extends DataAccessException {
    public NoMatchException(String msg) {
        super("No match in database");
    }
}
