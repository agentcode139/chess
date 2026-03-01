package dataaccess;

import server.exception.AlreadyTakenException;
import dataaccess.exception.DataAccessException;

import java.util.HashMap;

public class MemUserDAO implements UserDAO{

    HashMap<String,UserData> userDataMap;

    public MemUserDAO() {
        try {
            clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (userDataMap.containsKey(user.username())){
            throw new DataAccessException(user.username());
        }
        userDataMap.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataMap.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        userDataMap = new HashMap<>();
    }
}
