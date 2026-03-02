package dataaccess.exception;

public class UserAlreadyExistsException extends DataAccessException {
    public UserAlreadyExistsException() {
        super("User already Exists");
    }
}
