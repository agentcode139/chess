package server;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.exception.DataAccessException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import exception.BadRequestException;
import exception.ServiceException;
import exception.UnauthorizedException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import server.service.Service;

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
                .exception(ServiceException.class, this::handleException)
                .ws("/ws",ws -> {
                    ws.onConnect(ctx -> {
                        ctx.enableAutomaticPings();
                        System.out.println("Websocket connected");
                    });
                    ws.onMessage(ctx -> ctx.send("WebSocket response:" + ctx.message()));
                    ws.onClose(ctx -> System.out.println("Websocket closed"));
                });
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.initDatabases();
        } catch (DataAccessException e) {
            return -1;
        }

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
        String authToken = isAuthorized(ctx);
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
        if (request.gameName() == null) {
            throw new BadRequestException();
        }
        CreateGameResult result = service.createGame(authToken, request);
        ctx.status(200).json(gson.toJson(result));
    }

    private void listGames(Context ctx) throws Exception {
        final String authToken = isAuthorized(ctx);
        ListGamesResult result = service.listGames(authToken);
        ctx.status(200).json(gson.toJson(result));
    }

    private void joinGame(Context ctx) throws Exception {
        final String authToken = isAuthorized(ctx);
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
        if (request.playerColor() == null) {
            throw new BadRequestException();
        }
        service.joinGame(authToken, request);
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
