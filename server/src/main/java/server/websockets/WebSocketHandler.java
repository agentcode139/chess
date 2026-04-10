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
import websocket.commands.MakeMoveCommand;
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
                case CONNECT -> connect(userGameCommand, ctx.session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(ctx.message(), MakeMoveCommand.class), ctx.session);
                case LEAVE -> leave(userGameCommand, ctx.session);
                case RESIGN -> resign(userGameCommand, ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            // Server sends a LOAD_GAME message back to the root client.
            String username = service.getUserName(userGameCommand.getAuthToken());
            GameData gameData = service.getGame(userGameCommand.getGameID());

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
            connections.broadcastExecept(session, notification);
        } catch (Exception e) {
            //connections.broadcast(new ErrorMessage("Failed to connect."));
            throw new IOException(e.getMessage());
        }
    }

    private void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws IOException {
        try {
            // Server verifies the validity of the move. TODO
            GameData gameData = service.getGame(makeMoveCommand.getGameID());
            ChessGame game = gameData.game();
            ChessMove move = makeMoveCommand.getMove();
            game.makeMove(move);
            // Game is updated to represent the move. Game is updated in the database.

            // Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
            var loadGame = new LoadGameMessage(gameData.game());
            connections.broadcast(loadGame);

            // Server sends a Notification message to all other clients in that game informing them what move was made.
            NoticationMessage notificationMove = new NoticationMessage("move " + move.toString() + " was made");
            connections.broadcastExecept(session, notificationMove);

            // If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
            NoticationMessage notification = null;
            if (game.isInCheck(ChessGame.TeamColor.WHITE)){
                notification = new NoticationMessage("White is in check");
            }
            if (game.isInCheck(ChessGame.TeamColor.BLACK)){
                notification = new NoticationMessage("Black is in check");
            }
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
                notification = new NoticationMessage("White is in checkmate");
            }
            if (game.isInCheckmate(ChessGame.TeamColor.BLACK)){
                notification = new NoticationMessage("Black is in checkmate");
            }
            if (game.isInStalemate(ChessGame.TeamColor.WHITE)){
                notification = new NoticationMessage("White is in stalemate");
            }
            if (game.isInStalemate(ChessGame.TeamColor.BLACK)){
                notification = new NoticationMessage("Black is in stalemate");
            }

            if (notification != null) {
                connections.broadcast(notification);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void leave(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            // If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.

            // Server sends a Notification message to all other clients in that game informing them that the root client left. This applies to both players and observers.
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    private void resign(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            // Server marks the game as over (no more moves can be made). Game is updated in the database.

            // Server sends a Notification message to all clients in that game informing them that the root client resigned. This applies to both players and observers.
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }
}
