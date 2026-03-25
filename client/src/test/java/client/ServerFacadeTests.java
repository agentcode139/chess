package client;

import exception.ServiceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void setUp() {
        try {
            serverFacade.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void clearTest() {
        assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    public void registerPositiveTest() {
        RegisterRequest request = new RegisterRequest("Bob", "123", "test@byu.edu");
        LoginResult result = assertDoesNotThrow(() -> serverFacade.register(request));

        assertNotNull(result);
        assertEquals("Bob", result.username());
        assertNotNull(result.authToken());
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void registerNegative() {
        RegisterRequest request1 = new RegisterRequest("Bob", "123", "test@byu.edu");
        RegisterRequest request2 = new RegisterRequest("Bob", "321", "test@byu.edu");

        assertDoesNotThrow(() -> serverFacade.register(request1));
        assertThrows(ServiceException.class, () -> serverFacade.register(request2));
    }

    @Test
    public void logoutPositive() {
        RegisterRequest request = new RegisterRequest("Bob", "123", "test@byu.edu");
        LoginResult result = assertDoesNotThrow(() -> serverFacade.register(request));

        assertDoesNotThrow(() -> serverFacade.logout(result.authToken()));
    }

    @Test
    public void logoutNegative(){

    }

    @Test
    public void loginPositive(){
        assertDoesNotThrow(() -> serverFacade.logout(makeTestUser().authToken()));

        LoginRequest request = new LoginRequest("Tester", "123");
        LoginResult result = assertDoesNotThrow(() -> serverFacade.login(request));

        assertNotNull(result);
        assertEquals("Tester", result.username());
        assertNotNull(result.authToken());
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    public void loginNegativeUsername(){
        assertDoesNotThrow(() -> serverFacade.logout(makeTestUser().authToken()));
        LoginRequest request = new LoginRequest("Failure", "123");
        assertThrows(ServiceException.class, () -> serverFacade.login(request));
    }

    @Test
    public void loginNegativePassword(){
        assertDoesNotThrow(() -> serverFacade.logout(makeTestUser().authToken()));
        LoginRequest request = new LoginRequest("Tester", "567");
        assertThrows(ServiceException.class, () -> serverFacade.login(request));
    }

    @Test
    public void createGamePositive(){
        LoginResult loginResult = makeTestUser();

        CreateGameRequest request = new CreateGameRequest("game");
        CreateGameResult result =  assertDoesNotThrow(() -> serverFacade.createGame(loginResult.authToken(), request));

        assertNotNull(result);
        assertInstanceOf(Integer.class, result.gameID());
    }

    @Test
    public void joinGamePositive(){
        LoginResult user = makeTestUser();
        CreateGameResult game = makeTestGame(user.authToken());

        JoinGameRequest request = new JoinGameRequest("WHITE", game.gameID());
        assertDoesNotThrow(() -> serverFacade.joinGame(user.authToken(),request));
    }

    @Test
    public void joinGameNegative(){
        LoginResult user = makeTestUser();

        JoinGameRequest request = new JoinGameRequest("WHITE", -1);
        assertThrows(ServiceException.class,() -> serverFacade.joinGame(user.authToken(),request));
    }

    @Test
    public void listGamesPositive(){
        LoginResult user = makeTestUser();
        makeTestGame(user.authToken());

        ListGamesResult result = assertDoesNotThrow(() -> serverFacade.listGames(user.authToken()));

        assertNotNull(result);
    }

    @Test
    public void listGamesNone(){
        LoginResult user = makeTestUser();
        ListGamesResult result = assertDoesNotThrow(() -> serverFacade.listGames(user.authToken()));

        assertNotNull(result);
        assert result.games().isEmpty();
    }


    private LoginResult makeTestUser(){
        RegisterRequest request = new RegisterRequest("Tester", "123", "test@byu.edu");
        return assertDoesNotThrow(() -> serverFacade.register(request));
    }

    private CreateGameResult makeTestGame(String authtoken){
        CreateGameRequest request = new CreateGameRequest("game");
        return assertDoesNotThrow(() -> serverFacade.createGame(authtoken, request));
    }

}
