package dataaccess.exception;

import server.exception.ServiceException;

public class NoMatchException extends DataAccessException {
    public NoMatchException(String msg) {
        super("No match in database");
    }
}
