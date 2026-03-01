package server;

import dataaccess.AuthData;
import dataaccess.DataAccessException;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.CreateGameResult;
import server.result.ListGamesResult;
import server.result.LoginResult;

public class Service {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public Service(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;

    }
    public Service(){
        this(new MemUserDAO(),new MemAuthDAO(),new MemGameDAO());
    }

    static LoginResult addUser(RegisterRequest registerRequest) {
        return new LoginResult(null,null);
    }
    static LoginResult loginUser(LoginRequest loginRequest) throws DataAccessException {
        AuthData authData = new AuthData("","");
        return new LoginResult("","");
    }
    static void logoutUser(String authToken) throws DataAccessException {

    }

    static CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        return new CreateGameResult(0);
    }

    static ListGamesResult listGames(String authToken) throws DataAccessException {
        return new ListGamesResult(null);
    }

    static void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {

    }

    static void clearData() throws DataAccessException {

    }
}
