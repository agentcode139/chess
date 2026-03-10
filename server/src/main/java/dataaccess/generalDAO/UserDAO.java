package dataaccess.generalDAO;

import dataaccess.exception.DataAccessException;
import dataaccess.records.UserData;

public interface UserDAO extends GeneralDAO {
    void addUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
