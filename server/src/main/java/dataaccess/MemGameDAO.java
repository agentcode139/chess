package dataaccess;

import java.util.Collection;
import java.util.List;

public class MemGameDAO implements GameDAO{
    @Override
    public int addGame() throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame() throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame() throws DataAccessException {

    }
}
