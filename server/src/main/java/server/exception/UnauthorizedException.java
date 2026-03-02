package server.exception;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException() {
        super(401, "Unathorized");
    }
}
