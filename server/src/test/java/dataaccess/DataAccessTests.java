package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.Service;
import server.request.RegisterRequest;
import server.result.LoginResult;

import java.util.Objects;

public class DataAccessTests {
    private static final UserDAO memUserDAO = new MemUserDAO();
    private static final AuthDAO memAuthDAO = new MemAuthDAO();
    private static final GameDAO memGameDAO = new MemGameDAO();
    private static final Service memService = new Service(memUserDAO,memAuthDAO,memGameDAO);

    private static final UserDAO mySQLUserDAO = new MySQLUserDAO();
    private static final AuthDAO mySQLAuthDAO = new MySQLAuthDAO();
    private static final GameDAO mySQLGameDAO = new MySQLGameDAO();
    private static final Service mySQLService = new Service(mySQLUserDAO,mySQLAuthDAO,mySQLGameDAO);

    @Test
    void addUserPositiveTest(){
        Assertions.assertDoesNotThrow(mySQLService::clearData);
        RegisterRequest registerRequest = new RegisterRequest("TestUser1", "123456", "atat@hotmail.com");
        LoginResult registerResultMem = Assertions.assertDoesNotThrow(() -> memService.addUser(registerRequest));
        LoginResult registerResultSQL = Assertions.assertDoesNotThrow(() -> mySQLService.addUser(registerRequest));

        assert Objects.equals(registerResultMem, registerResultSQL);
    }

}
