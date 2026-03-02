package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import server.exception.BadRequestException;
import server.exception.ServiceException;
import server.exception.UnauthorizedException;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.CreateGameResult;
import server.result.ListGamesResult;
import server.result.LoginResult;

import java.util.UUID;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();
    private final Service service;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        service = new Service();
        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear)
                .exception(ServiceException.class, this::handleException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    /* HANDLERS */

    private void register(@NotNull Context ctx) throws Exception {
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException();
        }
        LoginResult result = service.addUser(request);
        ctx.status(200).json(gson.toJson(result));
    }

    private void login(@NotNull Context ctx) throws Exception {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException();
        }
        LoginResult result = service.loginUser(request);
        ctx.status(200).json(gson.toJson(result));
    }

    private void logout(Context ctx) throws Exception {
        final String authToken = isAuthorized(ctx);
        service.logoutUser(authToken);
        ctx.status(200).json(gson.toJson(new Object()));
    }

    private void createGame(Context ctx) throws Exception {
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
        if (request.gameName() == null) {
            throw new BadRequestException();
        }
        CreateGameResult result = service.createGame(request);
        ctx.status(200).json(gson.toJson(result));
    }

    private void listGames(Context ctx) throws Exception {
        final String authToken = isAuthorized(ctx);
        ListGamesResult result = service.listGames(authToken);
        ctx.status(200).json(gson.toJson(result));
    }

    private void joinGame(Context ctx) throws Exception {
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
        if (request.playerColor() == null) {
            throw new BadRequestException();
        }
        service.joinGame(request);
        ctx.status(200).json(gson.toJson(new Object()));
    }

    private void clear(@NotNull Context ctx) throws Exception {
        //final String authToken = isAuthorized(ctx);
        service.clearData();
        ctx.status(200).json(gson.toJson(new Object()));
    }

    /* Helpers */

    private void handleException(@NotNull ServiceException e, @NotNull Context ctx) {
        ctx.status(e.getStatusCode()).json(e.responseAsJson());
    }

    @NotNull
    private String isAuthorized(@NotNull Context ctx) throws UnauthorizedException {
        final String authToken = ctx.header("Authorization");
        if (authToken == null) {
            throw new UnauthorizedException();
        }
        return authToken;
    }

}
