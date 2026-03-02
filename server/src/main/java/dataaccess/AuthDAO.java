package dataaccess;

import dataaccess.exception.DataAccessException;

public interface AuthDAO extends GeneralDAO {
    void addAuth(AuthData data);

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);
}
