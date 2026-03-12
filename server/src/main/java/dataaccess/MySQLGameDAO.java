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

    private final Gson gson = new Gson();

    @Override
    public int addGame(String game) throws DataAccessException {
        var statement = "INSERT INTO games (whitePlayer, blackPlayer, gameName, gameState) VALUES (?, ?, ?, ?)";

        ChessGame chessGame = new ChessGame();
        String json = gson.toJson(chessGame);

        int gameID = executeUpdate(statement, null, null, game, json);
        if (gameID == 0) {
            throw new DataAccessException("Nothing was updated");
        }
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whitePlayer, blackPlayer, gameName, gameState FROM games WHERE id=?";
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
            var statement = "SELECT id, whitePlayer, blackPlayer, gameName, gameState FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(readGame(rs));
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE games SET whitePlayer=?, blackPlayer=?, gameState=? WHERE id=?";
        String json = new Gson().toJson(game.game());

        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), json, game.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM games";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var whitePlayer = rs.getString("whitePlayer");
        var blackPlayer = rs.getString("blackPlayer");
        var gameName = rs.getString("gameName");
        var json = rs.getString("gameState");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(id, gameName, whitePlayer, blackPlayer, game);
    }
}
