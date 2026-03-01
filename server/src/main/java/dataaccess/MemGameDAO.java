package dataaccess;

import chess.ChessGame;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.NoMatchException;

import java.util.Collection;
import java.util.TreeMap;

public class MemGameDAO implements GameDAO{

    TreeMap<Integer, GameData> gameDataMap;
    int gameIDSeed;

    public MemGameDAO() {
        try {
            clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int addGame(String game) throws DataAccessException {
        gameIDSeed++;
        int gameID = gameIDSeed;

        gameDataMap.put(gameID,new GameData(gameID,null,null,game,new ChessGame()));
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDataMap.get(gameID);
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return gameDataMap.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!gameDataMap.containsKey(game.gameID())){
            throw new NoMatchException(String.valueOf(game.gameID()));
        }
        gameDataMap.put(game.gameID(),game);
    }

    @Override
    public void clear() throws DataAccessException {
        gameDataMap = new TreeMap<>();
        gameIDSeed = 1000;
    }
}
