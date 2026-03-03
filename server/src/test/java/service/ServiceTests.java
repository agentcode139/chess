package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.Service;
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
    }

    // Register
    @Test
    @Order(1)
    public void registerPositive(){
        RegisterRequest registerRequest = new RegisterRequest("User1", "123456","cool@hotmail.com");

        LoginResult registerResult = Assertions.assertDoesNotThrow(() -> service.addUser(registerRequest));
        Assertions.assertEquals("User1", registerResult.username());
        // DATABASE
        assert Assertions.assertDoesNotThrow(() -> userDAO.getUser("User1")) != null;
    }

}
