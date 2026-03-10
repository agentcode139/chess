package dataaccess;

import dataaccess.exception.DataAccessException;

public class MySQLUserDAO implements UserDAO{
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
