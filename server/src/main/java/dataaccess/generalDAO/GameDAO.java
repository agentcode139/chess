package dataaccess.generalDAO;

import dataaccess.exception.DataAccessException;
import dataaccess.records.GameData;

import java.util.Collection;

public interface GameDAO extends GeneralDAO {
    int addGame(String game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> getAllGames() throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;
}
