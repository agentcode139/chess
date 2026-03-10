package dataaccess;

import dataaccess.exception.DataAccessException;

import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO{
    @Override
    public int addGame(String game) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> getAllGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
