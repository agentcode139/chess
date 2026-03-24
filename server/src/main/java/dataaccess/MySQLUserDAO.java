package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.UserAlreadyExistsException;
import records.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class MySQLUserDAO implements UserDAO {

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (getUser(user.username()) != null) {
            throw new UserAlreadyExistsException();
        }
        var statement = "INSERT INTO users (username, passwordHash, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
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
        var statement = "DELETE FROM users";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("passwordHash");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }
}
