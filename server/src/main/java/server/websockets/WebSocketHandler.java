package server.websockets;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove(userGameCommand.getGameID());
                case LEAVE -> leave(userGameCommand.getAuthToken(), ctx.session);
                case RESIGN -> resign(userGameCommand.getGameID());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connect(String authToken, Session session) throws IOException {
        // Server sends a LOAD_GAME message back to the root client.
        connections.add(session);
        // Server sends a Notification message to all other clients in that game informing them the root client connected to the game, either as a player (in which case their color must be specified) or as an observer.
//        var notifcation = new NotificationMessage();
//        ConnectionManager::broadcast(null,notifcation);
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
