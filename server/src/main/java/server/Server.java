package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import kotlin.NotImplementedError;
import server.exception.UnauthorizedException;
import server.request.RegisterRequest;
import server.result.LoginResult;

import java.util.UUID;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register)
        .post("/session", this::login)
        .delete("/session", this::logout)
        .get("/game", this::listGames)
        .post("/game", this::createGame)
        .put("/game", this::joinGame)
        .delete("/db", this::clear);
        //.exception(RequestException.class, this::handleException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    /* HANDLERS */

    private void register(Context ctx) throws Exception {
        RegisterRequest request = deserializeRequestBody(ctx.body(), RegisterRequest.class);

        LoginResult result = Service.addUser(request);
        ctx.json(gson.toJson(result));
    }

    private void login(Context ctx) throws Exception {
        throw new NotImplementedError();
    }

    private void logout(Context ctx) throws Exception {
        isAuthorized(ctx);
        throw new NotImplementedError();
    }

    private void createGame(Context ctx) throws Exception {
        isAuthorized(ctx);
        throw new NotImplementedError();
    }

    private void listGames(Context ctx) throws Exception {
        isAuthorized(ctx);
        throw new NotImplementedError();
    }

    private void joinGame(Context ctx) throws Exception {
        isAuthorized(ctx);
        throw new NotImplementedError();
    }

    private void clear(Context ctx) throws Exception {
        ctx.status(200);
        ctx.result(new Gson().toJson("{}"));
    }

    /* Helpers */

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void isAuthorized(Context ctx) throws UnauthorizedException {
        final String authToken = ctx.header("Authorization");

        if (authToken == null){
            throw new UnauthorizedException();
        }
    }

}
