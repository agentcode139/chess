package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.NoMatchException;

import java.util.HashMap;

public class MemAuthDAO implements AuthDAO{

    HashMap<String,AuthData> authDataMap;

    public MemAuthDAO() {
        try {
            clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAuth(AuthData data) throws DataAccessException {
        if (!authDataMap.containsKey(data.authToken())){
            throw new NoMatchException(data.authToken());
        }
        authDataMap.put(data.authToken(), data);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataMap.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDataMap.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        authDataMap = new HashMap<>();
    }
}
