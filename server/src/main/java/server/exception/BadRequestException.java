package server.exception;

public class BadRequestException extends ServiceException {
    public BadRequestException(){
        super(400,"Bad Request");
    }
}
