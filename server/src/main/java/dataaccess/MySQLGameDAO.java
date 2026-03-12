package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exception.DataAccessException;
import dataaccess.records.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static dataaccess.DatabaseManager.executeUpdate;

public class MySQLGameDAO implements GameDAO {
    private int gameIDSeed;
    public MySQLGameDAO() throws DataAccessException {
        clear();
    }

    @Override
    public int addGame(String game) throws DataAccessException {
        var statement = "INSERT INTO game(id, chessgame) VALUES (?, ?)";

        gameIDSeed++;
        int gameID = gameIDSeed;
        GameData newGame = new GameData(gameID, game, null, null, new ChessGame());
        String json = new Gson().toJson(newGame);

        executeUpdate(statement, gameID, json);

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, chessgame FROM game WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        Collection<GameData> result = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, chessgame FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE game SET chessgame=? WHERE id=?";
        String json = new Gson().toJson(game);

        executeUpdate(statement, json, game.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
        gameIDSeed = 0;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var json = rs.getString("game");
        return new Gson().fromJson(json, GameData.class);
    }
}
