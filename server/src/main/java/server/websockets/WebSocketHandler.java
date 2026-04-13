package server.websockets;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import records.GameData;
import request.JoinGameRequest;
import server.service.Service;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NoticationMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections;
    private final Service service;
    private final Map<Integer, Set<Session>> gameClients;

    public WebSocketHandler(Service service) {
        this.service = service;
        connections = new ConnectionManager();
        this.gameClients = new ConcurrentHashMap<>();
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        //System.out.println("Websocket handling message in Handler");
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
            if (gameData.blackUsername() != null && username.equals(gameData.blackUsername())) {
                joinType = "black player";
            } else if (gameData.whiteUsername() != null && username.equals(gameData.whiteUsername())) {
                joinType = "white player";
            } else {
                joinType = "observer";
            }
            gameClients.computeIfAbsent(userGameCommand.getGameID(), k -> ConcurrentHashMap.newKeySet()).add(session);

            var notification = new NoticationMessage(username + " has connected to game as " + joinType);
            connections.broadcastExclusiveInclude(session, gameClients.get(userGameCommand.getGameID()), notification);
        } catch (Exception e) {
            connections.broadcastTo(session, new ErrorMessage("Failed Connect:" + e.getMessage()));
        }
    }

    private void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws IOException {
        try {
            // Server verifies the validity of the move.
            String username = service.getUserName(makeMoveCommand.getAuthToken());
            GameData gameData = service.getGame(makeMoveCommand.getGameID());
            ChessGame game = gameData.game();
            ChessMove move = makeMoveCommand.getMove();

            if (game.getTeamTurn() != game.getBoard().getPiece(move.getStartPosition()).getTeamColor()) {
                throw new InvalidMoveException();
            }
            if (game.getTeamTurn() == ChessGame.TeamColor.WHITE) {
                if (!Objects.equals(username, gameData.whiteUsername())) {
                    throw new InvalidMoveException();
                }
            } else {
                if (!Objects.equals(username, gameData.blackUsername())) {
                    throw new InvalidMoveException();
                }
            }
            if (!game.isActive()) {
                throw new InvalidMoveException();
            }

            // Game is updated to represent the move. Game is updated in the database.
            game.makeMove(move);
            service.updateGame(gameData);

            // Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
            var loadGame = new LoadGameMessage(gameData.game());
            connections.broadcastExclusiveInclude(null, gameClients.get(makeMoveCommand.getGameID()), loadGame);

            // Server sends a Notification message to all other clients in that game informing them what move was made.
            NoticationMessage notificationMove = new NoticationMessage("move " + move + " was made");
            connections.broadcastExclusiveInclude(session, gameClients.get(makeMoveCommand.getGameID()), notificationMove);

            // If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
            NoticationMessage notification = null;
            if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                notification = new NoticationMessage("White is in check");
            }
            if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                notification = new NoticationMessage("Black is in check");
            }
            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                notification = new NoticationMessage("White is in checkmate");
            }
            if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                notification = new NoticationMessage("Black is in checkmate");
            }
            if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                notification = new NoticationMessage("White is in stalemate");
            }
            if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                notification = new NoticationMessage("Black is in stalemate");
            }

            if (notification != null) {
                connections.broadcast(notification);
            }
        } catch (Exception e) {
            connections.broadcastTo(session, new ErrorMessage("Bad Move:" + e.getMessage()));
        }
    }

    private void leave(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            // If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
            String username = service.getUserName(userGameCommand.getAuthToken());
            GameData gameData = service.getGame(userGameCommand.getGameID());

            Set<Session> clients = gameClients.getOrDefault(userGameCommand.getGameID(), ConcurrentHashMap.newKeySet());
            clients.remove(session);
            String playerColor = (Objects.equals(gameData.whiteUsername(), username)) ? "WHITE" : "BLACK";
            service.leaveGame(userGameCommand.getAuthToken(), new JoinGameRequest(playerColor, userGameCommand.getGameID()));

            // Server sends a Notification message to all other clients in that game informing them that the root client left. This applies to both players and observers
            connections.broadcastExclusiveInclude(session,
                    gameClients.get(userGameCommand.getGameID()),
                    new NoticationMessage(username + " left " + gameData.gameName()));
            connections.remove(session);
        } catch (Exception e) {
            connections.broadcastTo(session, new ErrorMessage("You can't leave:" + e.getMessage()));
        }
    }

    private void resign(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            // Server marks the game as over (no more moves can be made). Game is updated in the database.
            String username = service.getUserName(userGameCommand.getAuthToken());
            GameData gameData = service.getGame(userGameCommand.getGameID());
            ChessGame game = gameData.game();
            if (!game.isActive()) {
                throw new InvalidMoveException();
            }
            if (!Objects.equals(username, gameData.whiteUsername()) && !Objects.equals(username, gameData.blackUsername())) {
                throw new InvalidMoveException();
            }

            game.setActive(false);
            service.updateGame(gameData);

            // Server sends a Notification message to all clients in that game informing them that the root client resigned. This applies to both players and observers.
            connections.broadcastExclusiveInclude(null,
                    gameClients.get(userGameCommand.getGameID()),
                    new NoticationMessage(username + " has resigned"));
        } catch (Exception e) {
            connections.broadcastTo(session, new ErrorMessage("Failed resign:" + e.getMessage()));
        }

    }
}
