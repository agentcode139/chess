package server.exception;

public class GeneralServiceException extends ServiceException {
    public GeneralServiceException(String message) {
        super(500, "Error: " + message);
    }
}
