package dataaccess;

import dataaccess.exception.DataAccessException;
import records.UserData;

public interface UserDAO extends GeneralDAO {
    void addUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
