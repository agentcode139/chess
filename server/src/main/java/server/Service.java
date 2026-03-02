package server;

import dataaccess.*;
import dataaccess.exception.DataAccessException;
import server.exception.*;
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


    public Service(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Service() {
        this(new MemUserDAO(), new MemAuthDAO(), new MemGameDAO());
    }

    private AuthData validateAuthToken(String authToken) throws ServiceException {
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public LoginResult addUser(RegisterRequest registerRequest) throws ServiceException {
        UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());

        try {
            userDAO.addUser(newUser);
        } catch (DataAccessException ignored) {
            throw new AlreadyTakenException();
        }

        AuthData auth = generateAuth(registerRequest.username());
        authDAO.addAuth(auth);

        return new LoginResult(auth.username(), auth.authToken());
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws ServiceException {
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (!Objects.equals(user.password(), loginRequest.password())) {
                throw new IncorrectPasswordException();
            }
            AuthData auth = generateAuth(user.username());
            authDAO.addAuth(auth);
            return new LoginResult(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new NoUserException();
        }
    }

    public void logoutUser(String authToken) throws ServiceException {
        validateAuthToken(authToken);
        authDAO.deleteAuth(authToken);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ServiceException {
        validateAuthToken(createGameRequest.authToken());
        int gameID = gameDAO.addGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        return new ListGamesResult(gameDAO.getAllGames());
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws ServiceException {
        AuthData auth = validateAuthToken(joinGameRequest.authToken());
        try {
            GameData game = gameDAO.getGame(joinGameRequest.gameID());
            if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
                gameDAO.updateGame(new GameData(game.gameID(), joinGameRequest.authToken(), game.blackUsername(), game.gamename(), game.game()));
            } else {
                gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), joinGameRequest.authToken(), game.gamename(), game.game()));
            }

        } catch (DataAccessException ignored) {
            throw new BadRequestException();
        }
    }

    public void clearData() throws ServiceException {
        //validateAuthToken(authToken);
        this.userDAO.clear();
        this.authDAO.clear();
        this.gameDAO.clear();
    }
}
