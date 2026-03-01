package dataaccess;

import dataaccess.exception.DataAccessException;

public interface AuthDAO extends GeneralDAO{
    void addAuth(AuthData data) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
