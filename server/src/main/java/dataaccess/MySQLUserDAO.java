package dataaccess;

import com.google.gson.Gson;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UserAlreadyExistsException;
import dataaccess.records.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class MySQLUserDAO implements UserDAO {

    private final Gson gson = new Gson();

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (getUser(user.username()) != null) {
            throw new UserAlreadyExistsException();
        }
        var statement = "INSERT INTO users (username, passwordHash, email) VALUES (?, ?, ?)";
        String json = gson.toJson(user);
        executeUpdate(statement, user.username(), json);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, passwordHash, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var id = rs.getString("username");
        var json = rs.getString("userdata");
        UserData userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }
}
