package exception;

public class NotEnoughParamsException extends ServiceException {
    public NotEnoughParamsException() {
        super(400, "Not enough items inputed");
    }
}
