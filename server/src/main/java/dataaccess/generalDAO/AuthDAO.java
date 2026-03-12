package dataaccess.generalDAO;

import dataaccess.exception.DataAccessException;
import dataaccess.records.AuthData;

public interface AuthDAO extends GeneralDAO {
    void addAuth(AuthData data) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
