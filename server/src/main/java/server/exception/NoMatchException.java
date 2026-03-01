package server.exception;

public class NoMatchException extends ServiceException {
    public NoMatchException() {
        super(500,"No match in Database");
    }
}
