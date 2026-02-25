package server;

import io.javalin.*;
import io.javalin.http.Context;
import jdk.jshell.spi.ExecutionControl;
import kotlin.NotImplementedError;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        throw new NotImplementedError();
    }

    private void login(Context ctx) {
        throw new NotImplementedError();
    }

    private void logout(Context ctx) {
        throw new NotImplementedError();
    }

    private void createGame(Context ctx) {
        throw new NotImplementedError();
    }

    private void listGames(Context ctx) {
        throw new NotImplementedError();
    }

    private void joinGame(Context ctx) {
        throw new NotImplementedError();
    }

    private boolean isAuthorized(Context ctx) {
        throw new NotImplementedError();
    }

    private void clear(Context ctx) {
        throw new NotImplementedError();
    }

}
