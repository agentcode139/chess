package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import kotlin.NotImplementedError;

import java.util.List;
import java.util.UUID;

public class UserService {
    public LoginResult register(Context ctx) throws DataAccessException {
        throw new NotImplementedError();
        // TODO Create user then login
    }

    public LoginResult login(Context ctx) throws DataAccessException {
        throw new NotImplementedError();
    }

    public void logout(Context ctx) throws DataAccessException {
        if (isAuthorized(ctx)){

        }
        throw new NotImplementedError();
    }

    public String createGame(Context ctx) throws DataAccessException {
        if (isAuthorized(ctx)){

        }
        throw new NotImplementedError();
    }

    public List<GameData> listGames(Context ctx) throws DataAccessException {
        if (isAuthorized(ctx)){

        }
        throw new NotImplementedError();
    }

    public void joinGame(Context ctx) throws DataAccessException {
        if (isAuthorized(ctx)){

        }
        throw new NotImplementedError();
    }

    public void clear(Context ctx) throws DataAccessException {

        throw new NotImplementedError();
    }

    /*Authentication*/
    //TODO Store Generated tokens

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isAuthorized(Context ctx) {
        throw new NotImplementedError();
    }
}
