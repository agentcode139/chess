package dataaccess.mysqlDAO;

import dataaccess.generalDAO.UserDAO;
import dataaccess.exception.DataAccessException;
import dataaccess.records.UserData;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {

    }
}
