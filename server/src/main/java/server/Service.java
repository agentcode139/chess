package server;

import dataaccess.*;
import dataaccess.exception.DataAccessException;
import dataaccess.records.AuthData;
import dataaccess.records.GameData;
import dataaccess.records.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.exception.*;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.CreateGameResult;
import server.result.ListGamesResult;
import server.result.LoginResult;

import java.util.Objects;

import static dataaccess.records.AuthData.generateAuth;

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
        this(new MySQLUserDAO(), new MySQLAuthDAO(), new MySQLGameDAO());
    }

    private AuthData validateAuthToken(String authToken) throws ServiceException {
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public LoginResult addUser(RegisterRequest registerRequest) throws ServiceException {
        UserData newUser = new UserData(registerRequest.username(),
                BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt()),
                registerRequest.email());

        try {
            userDAO.addUser(newUser);

            AuthData auth = generateAuth(registerRequest.username());
            authDAO.addAuth(auth);

            return new LoginResult(auth.username(), auth.authToken());
        } catch (DataAccessException ignored) {
            throw new AlreadyTakenException();
        }
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws ServiceException {
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (user == null || !BCrypt.checkpw(loginRequest.password(), user.password())) {
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
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new GeneralServiceException(e.getMessage());
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException {
        validateAuthToken(authToken);
        try {
            int gameID = gameDAO.addGame(createGameRequest.gameName());
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new GeneralServiceException(e.getMessage());
        }
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        validateAuthToken(authToken);
        try {
            return new ListGamesResult(gameDAO.getAllGames());
        } catch (DataAccessException e) {
            throw new GeneralServiceException(e.getMessage());
        }
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException {
        AuthData auth = validateAuthToken(authToken);
        try {
            GameData game = gameDAO.getGame(joinGameRequest.gameID());
            if (game == null) {
                throw new BadRequestException();
            }
            if (Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException();
                }
                gameDAO.updateGame(new GameData(game.gameID(), game.gameName(), auth.username(), game.blackUsername(), game.game()));
            } else if (Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException();
                }
                gameDAO.updateGame(new GameData(game.gameID(), game.gameName(), game.whiteUsername(), auth.username(), game.game()));
            } else {
                throw new BadRequestException();
            }

        } catch (DataAccessException ignored) {
            throw new BadRequestException();
        }
    }

    public void clearData() throws ServiceException {
        try {
            this.userDAO.clear();
            this.authDAO.clear();
            this.gameDAO.clear();
        } catch (DataAccessException e) {
            throw new GeneralServiceException(e.getMessage());
        }
    }
}
