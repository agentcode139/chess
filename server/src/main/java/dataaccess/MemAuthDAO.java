package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.NoMatchException;
import dataaccess.records.AuthData;

import java.util.HashMap;

public class MemAuthDAO implements AuthDAO {

    HashMap<String, AuthData> authDataMap;

    public MemAuthDAO() {
        clear();
    }

    @Override
    public void addAuth(AuthData data) {
        authDataMap.put(data.authToken(), data);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new NoMatchException("Doesn't exist");
        }
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authDataMap.remove(authToken);
    }

    @Override
    public void clear() {
        authDataMap = new HashMap<>();
    }
}
