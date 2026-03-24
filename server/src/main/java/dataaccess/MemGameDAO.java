package dataaccess;

import chess.ChessGame;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.NoMatchException;
import records.GameData;

import java.util.Collection;
import java.util.TreeMap;

public class MemGameDAO implements GameDAO {

    private TreeMap<Integer, GameData> gameDataMap;
    private int gameIDSeed;

    public MemGameDAO() {
        clear();
    }

    @Override
    public int addGame(String game) {
        gameIDSeed++;
        int gameID = gameIDSeed;

        gameDataMap.put(gameID, new GameData(gameID, game, null, null, new ChessGame()));
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return gameDataMap.get(gameID);
    }

    @Override
    public Collection<GameData> getAllGames() {
        return gameDataMap.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!gameDataMap.containsKey(game.gameID())) {
            throw new NoMatchException(String.valueOf(game.gameID()));
        }
        gameDataMap.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        gameDataMap = new TreeMap<>();
        gameIDSeed = 0;
    }
}
