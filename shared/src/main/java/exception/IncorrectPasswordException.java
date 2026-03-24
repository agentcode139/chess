package exception;

public class IncorrectPasswordException extends ServiceException {
    public IncorrectPasswordException() {
        super(401, "Incorrect Password");
    }
}

