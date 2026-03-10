package service;

import chess.ChessGame;
import dataaccess.exception.DataAccessException;
import dataaccess.generalDAO.AuthDAO;
import dataaccess.generalDAO.GameDAO;
import dataaccess.generalDAO.UserDAO;
import dataaccess.memoryDAO.MemAuthDAO;
import dataaccess.memoryDAO.MemGameDAO;
import dataaccess.memoryDAO.MemUserDAO;
import dataaccess.records.AuthData;
import dataaccess.records.GameData;
import dataaccess.records.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Service;
import server.exception.ServiceException;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.CreateGameResult;
import server.result.ListGamesResult;
import server.result.LoginResult;

import java.util.Objects;

public class ServiceTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static Service service;

    private static String authtoken;

    @BeforeAll
    public static void init(){
        userDAO = new MemUserDAO();
        authDAO = new MemAuthDAO();
        gameDAO = new MemGameDAO();

        service = new Service(userDAO,authDAO,gameDAO);
        try {
            LoginResult loginResult = service.addUser(new RegisterRequest("TestUser1","123456","atat@hotmail.com"));
            authtoken = loginResult.authToken();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    // Register
    @Test
    public void registerNegative(){
        RegisterRequest registerRequest = new RegisterRequest("User1", "123456","cool@hotmail.com");

        LoginResult registerResult = Assertions.assertDoesNotThrow(() -> service.addUser(registerRequest));
        Assertions.assertEquals("User1", registerResult.username());
        // DATABASE
        assert Assertions.assertDoesNotThrow(() -> userDAO.getUser("User1")) != null;
    }

    @Test
    public void registerPositive(){
        RegisterRequest registerRequest1 = new RegisterRequest("User1", "123456","cool@hotmail.com");

        LoginResult registerResult1 = Assertions.assertDoesNotThrow(() -> service.addUser(registerRequest1));
        Assertions.assertEquals("User1", registerResult1.username());

        RegisterRequest registerRequest2 = new RegisterRequest("User1", "password","boring@gmail.com");
        Assertions.assertThrows(ServiceException.class, () -> service.addUser(registerRequest2));
        // DATABASE
        UserData user = Assertions.assertDoesNotThrow(() -> userDAO.getUser("User1"));

        assert user != null;
        assert Objects.equals(user.password(), "123456");
    }

    @Test
    public void loginNegative(){
        LoginRequest loginRequest = new LoginRequest("TestUser1","123456");

        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.loginUser(loginRequest));
        assert loginResult != null;
    }

    @Test
    public void loginPositive(){
        LoginRequest loginRequest = new LoginRequest("TestUser1","123456");

        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.loginUser(loginRequest));
        AuthData authData = Assertions.assertDoesNotThrow(()->authDAO.getAuth(loginResult.authToken()));
        assert Objects.equals(authData.username(), "TestUser1");
    }

    @Test
    public void logoutPositive(){
        RegisterRequest registerRequest = new RegisterRequest("LogMan123", "Iminboi","test@hotmail.com");
        LoginResult registerResult = Assertions.assertDoesNotThrow(() -> service.addUser(registerRequest));

        Assertions.assertDoesNotThrow(()->service.logoutUser(registerResult.authToken()));
        Assertions.assertThrows(DataAccessException.class,()->authDAO.getAuth(registerResult.authToken()));
    }

    @Test
    public void createGamePositive(){
        CreateGameRequest createGameRequest = new CreateGameRequest("Da best Game");
        CreateGameResult createGameResult = Assertions.assertDoesNotThrow(()->service.createGame(authtoken,createGameRequest));

        assert Objects.equals(Assertions.assertDoesNotThrow(() -> gameDAO.getGame(createGameResult.gameID())),
                new GameData(createGameResult.gameID(), "Da best Game", null, null, new ChessGame()));
    }

    @Test
    public void joinGameNegative(){
        CreateGameRequest createGameRequest = new CreateGameRequest("Da best Game");
        CreateGameResult createGameResult = Assertions.assertDoesNotThrow(()->service.createGame(authtoken,createGameRequest));

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE",createGameResult.gameID());
        Assertions.assertDoesNotThrow(() -> service.joinGame(authtoken,joinGameRequest));

        assert Assertions.assertDoesNotThrow(() -> gameDAO.getGame(createGameResult.gameID())).whiteUsername() != null;
    }

    @Test
    public void listGamesPositive(){
        ListGamesResult listGamesResult = Assertions.assertDoesNotThrow(() -> service.listGames(authtoken));
        assert listGamesResult.games().isEmpty();
    }

    @Test
    public void listGamesNegative(){
        CreateGameRequest createGameRequest = new CreateGameRequest("Da best Game");
        Assertions.assertDoesNotThrow(()->service.createGame(authtoken,createGameRequest));

        ListGamesResult listGamesResult = Assertions.assertDoesNotThrow(() -> service.listGames(authtoken));
        assert !listGamesResult.games().isEmpty();
    }

    @Test
    public void clearNegativeAuth(){
        Assertions.assertDoesNotThrow(()->service.clearData());
        Assertions.assertThrows(DataAccessException.class,()->authDAO.getAuth(authtoken));
    }
    @Test
    public void clearNegativeUser(){
        Assertions.assertDoesNotThrow(()->service.clearData());
        Assertions.assertThrows(DataAccessException.class,()->userDAO.getUser("TestUser1"));
    }
}
