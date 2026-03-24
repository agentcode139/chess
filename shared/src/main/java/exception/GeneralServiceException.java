package exception;

public class GeneralServiceException extends ServiceException {
    public GeneralServiceException(String message) {
        super(500, "Error: " + message);
    }

    public static ServiceException fromJson(String body) {
        return new GeneralServiceException(body);
    }
}
