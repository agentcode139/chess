package dataaccess.generalDAO;

import dataaccess.exception.DataAccessException;
import dataaccess.records.GameData;

import java.util.Collection;

public interface GameDAO extends GeneralDAO {
    int addGame(String game);

    GameData getGame(int gameID);

    Collection<GameData> getAllGames();

    void updateGame(GameData game) throws DataAccessException;
}
