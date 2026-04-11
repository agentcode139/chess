package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.ChessBoardDisplay;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NoticationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketsFacade extends Endpoint {

    private final Gson gson = new Gson();
    private Session session;
    private ChessGame game;

    public WebSocketsFacade(String url) {
        this.game = null;
        url = url.replace("http", "ws");
        URI socketURI = null;
        try {
            socketURI = new URI(url + "/ws");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            session = container.connectToServer(this, socketURI);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                notificationHandler.notify(notification);
                //ServerMessage userGameCommand = new Gson().fromJson(ctx.message(), ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        load_game(new Gson().fromJson(message, LoadGameMessage.class));
                    }
                    case ERROR -> {
                        errorHandle(new Gson().fromJson(message, ErrorMessage.class));
                    }
                    case NOTIFICATION -> {
                        notification(new Gson().fromJson(message, NoticationMessage.class));
                    }
                }

            }
        });

    }

    private void load_game(LoadGameMessage loadGameMessage){
        game = loadGameMessage.getChessGame();
    }

    private void errorHandle(ErrorMessage errorMessage){
        System.out.print(errorMessage.getErrorMessage());
    }

    private void notification(NoticationMessage noticationMessage){
        System.out.print(noticationMessage.getMessage());
    }

    public void printGame(ChessGame.TeamColor view) {
        ChessBoardDisplay.drawChessBoard(game.getBoard(), view);
    }
    public void printGame(ChessGame.TeamColor view, ChessPosition chessPosition) {
        Collection<ChessMove> validMoves = game.validMoves(chessPosition);
        Map<Integer, Set<Integer>> validSpots = new ConcurrentHashMap<>();
        for (ChessMove move : validMoves) {
            validSpots.putIfAbsent(move.getEndPosition().getRow(), new HashSet<>());
            validSpots.get(move.getEndPosition().getRow()).add(move.getEndPosition().getColumn());
        }
        ChessBoardDisplay.drawChessBoard(game.getBoard(), view, validSpots);
    }

    public void connect(String authtoken, int gameID) throws IOException {
        String message = gson.toJson(
                new UserGameCommand(UserGameCommand.CommandType.CONNECT,authtoken,gameID)
        );
        session.getBasicRemote().sendText(message);

//        ChessGame.TeamColor perspective = (Objects.equals(team.toUpperCase(), "WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
//        printGame(perspective);
    }

    public void makeMove(String authtoken, int gameID, ChessMove move) throws IOException {
        String message = gson.toJson(
                new MakeMoveCommand(authtoken,gameID,move)
        );
        session.getBasicRemote().sendText(message);
    }

    public void resign(String authtoken, int gameID) throws IOException {
        String message = gson.toJson(
                new UserGameCommand(UserGameCommand.CommandType.RESIGN,authtoken,gameID)
        );
        session.getBasicRemote().sendText(message);
    }

    public void leave(String authtoken, int gameID) throws IOException {
        String message = gson.toJson(
                new UserGameCommand(UserGameCommand.CommandType.LEAVE,authtoken,gameID)
        );
        session.getBasicRemote().sendText(message);
    }



    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
