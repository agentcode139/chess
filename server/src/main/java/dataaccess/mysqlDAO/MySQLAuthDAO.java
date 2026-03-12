package dataaccess.mysqlDAO;

import dataaccess.DatabaseManager;
import dataaccess.generalDAO.AuthDAO;
import dataaccess.exception.DataAccessException;
import dataaccess.records.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.executeUpdate;

public class MySQLAuthDAO implements AuthDAO {

    @Override
    public void addAuth(AuthData data) throws DataAccessException {
        var statement = "INSERT INTO auth(authtoken, username) VALUES (?, ?)";
        executeUpdate(statement, data.authToken(), data.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authtoken, username FROM auth WHERE authtoken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authtoken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authtoken = rs.getString("authtoken");
        var username = rs.getString("username");
        return new AuthData(authtoken,username);
    }

}
