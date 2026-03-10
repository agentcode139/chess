package dataaccess;

import dataaccess.exception.DataAccessException;

public class MySQLAuthDAO implements AuthDAO{
    @Override
    public void addAuth(AuthData data) {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}
