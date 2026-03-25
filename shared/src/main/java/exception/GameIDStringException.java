package exception;

public class GameIDStringException extends ServiceException {
    public GameIDStringException() {
        super(400, "The game ID is a string instead of number.");
    }
}
