package dataaccess;

import server.exception.ServiceException;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException(String value){
        super("The " + value + " is already taken.");
    }
}
