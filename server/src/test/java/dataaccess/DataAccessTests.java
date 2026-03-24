package dataaccess;

import dataaccess.exception.DataAccessException;
import records.AuthData;
import records.GameData;
import records.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;

public class DataAccessTests {
    MySQLAuthDAO authDAO = new MySQLAuthDAO();
    MySQLGameDAO gameDAO = new MySQLGameDAO();
    MySQLUserDAO userDAO = new MySQLUserDAO();

    final static String USERNAME = "username";
    final static String USERNAME_INVALID = "username_invalid";
    final static String AUTH_TOKEN = "auth_token";
    final static String AUTH_TOKEN_INVALID = "auth_token_invalid";

    @BeforeEach
    public void setUp() {
        try {
            authDAO.clear();
            gameDAO.clear();
            userDAO.clear();
        } catch (DataAccessException e) {
            abort("Failed to clear table.");
        }
    }

    @Test
    public void addAuthPositive() {
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
    public void addAuthNegative(){
        AuthData goodAuthData = new AuthData(AUTH_TOKEN, USERNAME);
        AuthData badUsernameAuthData = new AuthData(AUTH_TOKEN, USERNAME_INVALID);
        assertDoesNotThrow(() -> authDAO.addAuth(goodAuthData));
        assertThrows(DataAccessException.class, () -> authDAO.addAuth(badUsernameAuthData));
    }

    @Test
    public void getAuthPositive(){
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.addAuth(authData));
        AuthData getData = assertDoesNotThrow(() -> authDAO.getAuth(AUTH_TOKEN));
        assert Objects.equals(getData, authData);
    }

    @Test
    public void getAuthNegative(){
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.addAuth(authData));
        AuthData getData = assertDoesNotThrow(() -> authDAO.getAuth(AUTH_TOKEN_INVALID));
        assert !Objects.equals(getData, authData);
    }

    @Test
    public void deleteAuthPositive(){
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.addAuth(authData));
        assertDoesNotThrow(() -> authDAO.deleteAuth(AUTH_TOKEN));
        AuthData getData = assertDoesNotThrow(() -> authDAO.getAuth(AUTH_TOKEN));
        assert Objects.equals(getData, null);
    }

    @Test
    public void deleteAuthNegative(){
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.addAuth(authData));
        assertDoesNotThrow(() -> authDAO.deleteAuth(AUTH_TOKEN_INVALID));
        AuthData getData = assertDoesNotThrow(() -> authDAO.getAuth(AUTH_TOKEN));
        assert !Objects.equals(getData, null);
    }

    @Test
    public void clearAuthPositive(){
        AuthData authData = new AuthData(AUTH_TOKEN, USERNAME);
        assertDoesNotThrow(() -> authDAO.addAuth(authData));
        assertDoesNotThrow(() -> authDAO.clear());
        AuthData getData = assertDoesNotThrow(() -> authDAO.getAuth(AUTH_TOKEN));
        assert Objects.equals(getData, null);
    }

    @Test
    public void clearGamePositive(){
        int id = assertDoesNotThrow(() -> gameDAO.addGame("Chess Boi"));
        assertDoesNotThrow(() -> gameDAO.clear());
        GameData getData = assertDoesNotThrow(() -> gameDAO.getGame(id));
        assert Objects.equals(getData, null);
    }

    @Test
    public void clearUserPositive(){
        UserData userData = new UserData(USERNAME, "", "");
        assertDoesNotThrow(() -> userDAO.addUser(userData));
        assertDoesNotThrow(() -> userDAO.clear());
        UserData getData = assertDoesNotThrow(() -> userDAO.getUser(USERNAME));
        assert Objects.equals(getData, null);
    }
}
