package server.service;

import dataaccess.*;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UserAlreadyExistsException;
import records.AuthData;
import records.GameData;
import records.UserData;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.ServiceException;
import exception.UnauthorizedException;
import org.mindrot.jbcrypt.BCrypt;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;

import java.util.Objects;

import static records.AuthData.generateAuth;

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
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null || authData.username() == null) {
                throw new UnauthorizedException();
            }
            return authData;
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public LoginResult addUser(RegisterRequest registerRequest) throws ServiceException {
        UserData newUser = new UserData(registerRequest.username(),
                BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt()),
                registerRequest.email());

        if (newUser.username() == null || newUser.password() == null){
            throw new UnauthorizedException();
        }

        try {
            userDAO.addUser(newUser);

            AuthData auth = generateAuth(registerRequest.username());
            authDAO.addAuth(auth);

            return new LoginResult(auth.username(), auth.authToken());
        } catch (UserAlreadyExistsException e){
            throw new AlreadyTakenException();
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws ServiceException {
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (user == null){
                throw new UnauthorizedException();
            } else if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
                throw new exception.IncorrectPasswordException();
            }

            AuthData auth = generateAuth(user.username());
            authDAO.addAuth(auth);

            return new LoginResult(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public void logoutUser(String authToken) throws ServiceException {
        validateAuthToken(authToken);
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException {
        validateAuthToken(authToken);
        try {
            int gameID = gameDAO.addGame(createGameRequest.gameName());
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        validateAuthToken(authToken);
        try {
            return new ListGamesResult(gameDAO.getAllGames());
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
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

        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public void leaveGame(String authToken, JoinGameRequest leaveGameRequest) throws ServiceException {
        validateAuthToken(authToken);
        try {
            GameData game = gameDAO.getGame(leaveGameRequest.gameID());
            if (game == null) {
                throw new BadRequestException();
            }
            if (Objects.equals(leaveGameRequest.playerColor(), "WHITE")) {
                gameDAO.updateGame(new GameData(game.gameID(), game.gameName(), null, game.blackUsername(), game.game()));
            } else if (Objects.equals(leaveGameRequest.playerColor(), "BLACK")) {
                gameDAO.updateGame(new GameData(game.gameID(), game.gameName(), game.whiteUsername(), null, game.game()));
            } else {
                throw new BadRequestException();
            }

        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    public void clearData() throws ServiceException {
        try {
            this.userDAO.clear();
            this.authDAO.clear();
            this.gameDAO.clear();
        } catch (DataAccessException e) {
            throw new exception.GeneralServiceException(e.getMessage());
        }
    }

    //Data Access
    public String getUserName(String authToken) throws DataAccessException {
        return this.authDAO.getAuth(authToken).username();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return this.gameDAO.getGame(gameID);
    }
    public void updateGame(GameData gameData) throws DataAccessException {
        gameDAO.updateGame(gameData);
    }
}
