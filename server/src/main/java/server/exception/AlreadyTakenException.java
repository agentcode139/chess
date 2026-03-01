package server.exception;

import dataaccess.exception.DataAccessException;

public class AlreadyTakenException extends ServiceException {
    public AlreadyTakenException(){
        super(403, "Value is already taken.");
    }
}
