package server.exception;

public class NoUserException extends ServiceException {
    public NoUserException() {
        super(401, "User doesn't exist");
    }
}

