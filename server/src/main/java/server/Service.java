package server;

import dataaccess.*;
import dataaccess.exception.DataAccessException;
import server.exception.AlreadyTakenException;
import server.exception.BadRequestException;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.CreateGameResult;
import server.result.ListGamesResult;
import server.result.LoginResult;

import java.util.Objects;

import static dataaccess.AuthData.generateAuth;

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

    public LoginResult addUser(RegisterRequest registerRequest) throws AlreadyTakenException {
        UserData newUser = new UserData(registerRequest.username(),registerRequest.password(),registerRequest.email());

        try{
            userDAO.addUser(newUser);
        } catch (DataAccessException ignored){
            throw new AlreadyTakenException();
        }

        AuthData auth = generateAuth(registerRequest.username());
        try {
            authDAO.addAuth(auth);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new LoginResult(null,null);
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws BadRequestException {
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (!Objects.equals(user.password(), loginRequest.password())){
                throw new BadRequestException();
            }
            AuthData auth = generateAuth(user.username());
            return new LoginResult(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new BadRequestException();
        }
    }
    public void logoutUser(String authToken) {
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest){
        try {
            int gameID = gameDAO.addGame(createGameRequest.gameName());
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ListGamesResult listGames(String authToken){
        try {
            return new ListGamesResult(gameDAO.getAllGames());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest){
        try {
            GameData game = gameDAO.getGame(joinGameRequest.gameID());
            if (Objects.equals(joinGameRequest.playerColor(), "WHITE")){
                gameDAO.updateGame(new GameData(game.gameID(),joinGameRequest.authToken(),game.blackUsername(), game.gamename(),game.game()));
            } else {
                gameDAO.updateGame(new GameData(game.gameID(),game.whiteUsername(), joinGameRequest.authToken(), game.gamename(),game.game()));
            }

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearData(){
        try {
            this.userDAO.clear();
            this.authDAO.clear();
            this.gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
