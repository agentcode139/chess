package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.NoMatchException;
import dataaccess.exception.UserAlreadyExistsException;

import java.util.HashMap;

public class MemUserDAO implements UserDAO {

    HashMap<String, UserData> userDataMap;

    public MemUserDAO() {
        clear();
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (userDataMap.containsKey(user.username())) {
            throw new UserAlreadyExistsException();
        }
        userDataMap.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!userDataMap.containsKey(username)) {
            throw new NoMatchException("Doesn't exist");
        }
        return userDataMap.get(username);
    }

    @Override
    public void clear() {
        userDataMap = new HashMap<>();
    }
}
