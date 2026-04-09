package server.websockets;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import records.GameData;
import server.service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NoticationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections;
    private final Service service;
//    private final UserDAO userDAO;
//    private final AuthDAO authDAO;
//    private final GameDAO gameDAO;

    public WebSocketHandler(Service service) {
//        this.userDAO = service.getUserDAO();
//        this.authDAO = service.getAuthDAO();
//        this.gameDAO = service.getGameDAO();
        this.service = service;
        connections = new ConnectionManager();
    }

    public WebSocketHandler() {
        this(new Service());
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected in Handler");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed in Handler");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        System.out.println("Websocket handling message in Handler");
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(userGameCommand.getGameID());
                case LEAVE -> leave(userGameCommand.getAuthToken(), ctx.session);
                case RESIGN -> resign(userGameCommand.getGameID());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        try {
            // Server sends a LOAD_GAME message back to the root client.
            String username = service.getUserName(authToken);
            GameData gameData = service.getGame(gameID);

            connections.add(session);
            var loadGame = new LoadGameMessage(gameData.game());
            connections.broadcastTo(session, loadGame);

            // Server sends a Notification message to all other clients in that game informing them the root client connected to the game, either as a player (in which case their color must be specified) or as an observer.
            String joinType;
            if (username.equals(gameData.blackUsername())) {
                joinType = "black player";
            } else if (username.equals(gameData.whiteUsername())) {
                joinType = "white player";
            } else {
                joinType = "observer";
            }
            var notification = new NoticationMessage(username + " has connected to game as " + joinType + ".");
            connections.broadcastExecept(session, notification); //server notification
        } catch (Exception e) {
            connections.broadcast(new ErrorMessage("Failed to connect."));
        }
    }

    private void makeMove(int gameID) throws IOException {
        // Server verifies the validity of the move.

        // Game is updated to represent the move. Game is updated in the database.

        // Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.

        // Server sends a Notification message to all other clients in that game informing them what move was made.

        // If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.

    }

    private void leave(String authToken, Session session) throws IOException {
        // If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.

        // Server sends a Notification message to all other clients in that game informing them that the root client left. This applies to both players and observers.

    }
    private void resign(int gameID) throws IOException {
        // Server marks the game as over (no more moves can be made). Game is updated in the database.

        // Server sends a Notification message to all clients in that game informing them that the root client resigned. This applies to both players and observers.

    }
}
