package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import kotlin.NotImplementedError;

import java.util.UUID;

public class UserService {
    public void register(Context ctx) throws DataAccessException {
        throw new NotImplementedError();
        // TODO Create user then login
    }

    public void login(Context ctx) throws DataAccessException {
        throw new NotImplementedError();
    }

    public void logout(Context ctx) throws DataAccessException {
        if (isAuthorized(ctx)){

        }
        throw new NotImplementedError();
    }

    public void createGame(Context ctx) throws DataAccessException {
        if (isAuthorized(ctx)){

        }
        throw new NotImplementedError();
    }

    public void listGames(Context ctx) throws DataAccessException {
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
        if (isAuthorized(ctx)){

        }
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
