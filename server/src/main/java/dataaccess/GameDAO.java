package dataaccess;

import java.util.Collection;

public interface GameDAO {
    int addGame() throws DataAccessException;
    GameData getGame() throws DataAccessException;
    Collection<GameData> getAllGames() throws DataAccessException;
    void updateGame() throws DataAccessException;
}
