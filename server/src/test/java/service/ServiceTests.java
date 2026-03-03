package service;

import dataaccess.*;
import dataaccess.exception.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Service;
import server.exception.ServiceException;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;

public class ServiceTests {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static Service service;

    @BeforeAll
    public static void init(){
        userDAO = new MemUserDAO();
        authDAO = new MemAuthDAO();
        gameDAO = new MemGameDAO();

        service = new Service(userDAO,authDAO,gameDAO);
        try {
            service.addUser(new RegisterRequest("TestUser1","123456","atat@hotmail.com"));
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
    public void loginNegative(){
        LoginRequest loginRequest = new LoginRequest("TestUser1","123456");

        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> service.loginUser(loginRequest));
        assert loginResult != null;
    }

    @Test
    public void logoutPositive(){
        RegisterRequest registerRequest = new RegisterRequest("LogMan123", "Iminboi","test@hotmail.com");
        LoginResult registerResult = Assertions.assertDoesNotThrow(() -> service.addUser(registerRequest));

        Assertions.assertDoesNotThrow(()->service.logoutUser(registerResult.authToken()));
        Assertions.assertThrows(DataAccessException.class,()->authDAO.getAuth(registerResult.authToken()));
    }

}
