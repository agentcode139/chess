package client;

import dataaccess.*;
import org.junit.jupiter.api.*;
import records.UserData;
import request.RegisterRequest;
import result.LoginResult;
import server.Server;
import server.Service;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    private static Service service;

    @BeforeAll
    public static void init() {
        authDAO = new MemAuthDAO();
        gameDAO = new MemGameDAO();
        userDAO = new MemUserDAO();
        service = new Service(userDAO,authDAO,gameDAO);

        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void setUp() {
        try {
            service.clearData();
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
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositiveTest(){
        RegisterRequest request = new RegisterRequest("Bob","123","test@byu.edu");
        LoginResult result = assertDoesNotThrow(() -> serverFacade.register(request));

        UserData userData = assertDoesNotThrow(() -> userDAO.getUser("Bob"));
        assertEquals("Bob",userData.username());
//        assertEquals("123",userData.password());
//        assertEquals("test@byu.edu",userData.email());
        assertDoesNotThrow(() -> authDAO.getAuth(result.authToken()));
    }

    @Test
    public void clearTest(){
        assertDoesNotThrow(() -> serverFacade.clear());
    }

}
