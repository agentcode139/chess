package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.records.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;
import static dataaccess.DatabaseManager.executeUpdate;

public class DataAccessTests {
    MySQLAuthDAO authDAO = new MySQLAuthDAO();

    final static String USERNAME = "username";
    final static String USERNAME_INVALID = "username_invalid";
    final static String AUTH_TOKEN = "auth_token";
    final static String AUTH_TOKEN_INVALID = "auth_token_invalid";

    @BeforeEach
    void setUp() {
        try {
            authDAO.clear();
        } catch (DataAccessException e) {
            abort("Failed to clear table.");
        }

        try {
            var statement = "REPLACE INTO users (username, passwordHash, email) VALUES (?, '', '')";
            executeUpdate(statement, USERNAME);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addAuthPositive() {
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.addAuth(authData));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT count(*) FROM auths WHERE authtoken = ? AND username = ?")) {
            stmt.setString(1, AUTH_TOKEN);
            stmt.setString(2, USERNAME);

            ResultSet rs = stmt.executeQuery();

            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1));
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addAuthNegative(){
        AuthData goodAuthData = new AuthData(AUTH_TOKEN, USERNAME);
        AuthData badUsernameAuthData = new AuthData(AUTH_TOKEN, USERNAME_INVALID);
        assertDoesNotThrow(() -> authDAO.addAuth(goodAuthData));
        assertThrows(DataAccessException.class, () -> authDAO.addAuth(badUsernameAuthData));
    }
}
