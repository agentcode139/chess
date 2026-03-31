package client;

import chess.ChessGame;
import exception.BadRequestException;
import exception.GameIDStringException;
import exception.GeneralServiceException;
import exception.NotEnoughParamsException;
import records.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import ui.ChessBoardDisplay;

import java.util.*;

import static ui.EscapeSequences.*;

public class ClientCommunicator {
    private final ServerFacade server;
    // User Info
    private String authtoken;
    private ChessGame.TeamColor perspective;

    enum UIStates {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }

    private UIStates uiState = UIStates.PRELOGIN;

    private final Map<Integer, Integer> serverToClientIDs = new HashMap<>();
    private final Map<Integer, Integer> clientToServerIDs = new HashMap<>();
    private int addID = 1;

    public ClientCommunicator(String serverUrl) {
        server = new ServerFacade(serverUrl);
        authtoken = null;
    }

    public void run() {
        // REPL
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(RESET_BG_COLOR + SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (uiState) {
                case PRELOGIN -> switch (cmd) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    default -> help();
                };
                case POSTLOGIN -> switch (cmd) {
                    case "logout" -> logout();
                    case "create" -> create(params);
                    case "list" -> list();
                    case "join" -> join(params);
                    case "observe" -> observe(params);
                    case "quit" -> "quit";
                    default -> help();
                };
                case GAMEPLAY -> switch (cmd) {
                    // Phase 6
                    case "quit" -> "quit";
                    default -> help();
                };
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length >= 3) {
            LoginResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
            uiState = UIStates.POSTLOGIN;
            authtoken = result.authToken();
            return String.format("You signed in as %s.", result.username());
        }
        throw new NotEnoughParamsException();
    }

    public String login(String... params) throws Exception {
        if (params.length >= 2) {
            LoginResult result = server.login(new LoginRequest(params[0], params[1]));
            uiState = UIStates.POSTLOGIN;
            authtoken = result.authToken();
            return String.format("You signed in as %s.", result.username());
        }
        throw new NotEnoughParamsException();
    }

    public String logout() throws Exception {
        assert uiState == UIStates.POSTLOGIN;
        server.logout(authtoken);
        uiState = UIStates.PRELOGIN;
        authtoken = null;
        return "You logged out.";
    }

    public String create(String... params) throws Exception {
        assert uiState == UIStates.POSTLOGIN;
        if (params.length >= 1) {
            StringJoiner paramJoiner = new StringJoiner(" ");
            for (String param:params){
                paramJoiner.add(param);
            }
            var name = paramJoiner.toString();
            assert !name.contains("\"");
            CreateGameResult result = server.createGame(authtoken, new CreateGameRequest(name));
            serverToClientIDs.put(result.gameID(), addID);
            clientToServerIDs.put(addID, result.gameID());
            addID++;
            return String.format("The Game ID is %d.", addID - 1);
        }
        throw new NotEnoughParamsException();
    }

    public String list() throws Exception {
        assert uiState == UIStates.POSTLOGIN;
        ListGamesResult result = server.listGames(authtoken);
        var out = new StringBuilder();
        out.append(" ID | Game name: White Player, Black Player\n");
        for (GameData game : result.games()) {
            var printableID = serverToClientIDs.get(game.gameID());
            if (printableID == null){
                serverToClientIDs.put(game.gameID(), addID);
                clientToServerIDs.put(addID, game.gameID());
                printableID = addID;
                addID++;
            }
            out.append(" ")
                    .append(printableID)
                    .append(" | ").append(game.gameName())
                    .append(" White player: ").append(game.whiteUsername())
                    .append(" Black player: ").append(game.blackUsername())
                    .append("\n");
        }
        return out.toString();
    }

    public String join(String... params) throws Exception {
        assert uiState == UIStates.POSTLOGIN;
        if (params.length >= 2) {
            try {
                var id = clientToServerIDs.get(Integer.parseInt(params[0]));
                if (id == null) {
                    throw new BadRequestException();
                }
                server.joinGame(authtoken, new JoinGameRequest(params[1].toUpperCase(), id));
                GameData gameData = getGame(id);
                uiState = UIStates.GAMEPLAY;
                perspective = (Objects.equals(params[1].toUpperCase(), "WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                assert gameData != null;
                printGame(gameData.game(), perspective);
                return "Joined";
            } catch (Exception ignored) {
                throw new GameIDStringException();
            }
        }
        throw new NotEnoughParamsException();
    }

    public String observe(String... params) throws Exception {
        assert uiState == UIStates.POSTLOGIN;
        if (params.length >= 1) {
            try {
                var inNum = Integer.parseInt(params[0]);

                var id = clientToServerIDs.get(inNum);
                if (id == null) {
                    throw new BadRequestException();
                }
                GameData gameData = getGame(id);
                perspective = ChessGame.TeamColor.WHITE;
                assert gameData != null;
                printGame(gameData.game(), perspective);
                return "Watching";
            } catch (Exception ignore) {
                throw new GameIDStringException();
            }
        }
        throw new NotEnoughParamsException();
    }

    public String help() {
        return switch (uiState) {
            case PRELOGIN -> SET_TEXT_COLOR_BLUE +
                    "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_MAGENTA + "- to create an account.\n" + SET_TEXT_COLOR_BLUE +
                    "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_MAGENTA + "- to play chess.\n" + SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess.\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands." + SET_TEXT_COLOR_BLUE;
            case POSTLOGIN -> SET_TEXT_COLOR_BLUE +
                    "create <NAME> " + SET_TEXT_COLOR_MAGENTA + "- a game.\n" + SET_TEXT_COLOR_BLUE +
                    "list " + SET_TEXT_COLOR_MAGENTA + "- games.\n" + SET_TEXT_COLOR_BLUE +
                    "join <ID> [WHITE|BLACK] " + SET_TEXT_COLOR_MAGENTA + "- a game.\n" + SET_TEXT_COLOR_BLUE +
                    "observe <ID> " + SET_TEXT_COLOR_MAGENTA + "- a game.\n" + SET_TEXT_COLOR_BLUE +
                    "logout " + SET_TEXT_COLOR_MAGENTA + "- when you are done.\n" + SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess.\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands." + SET_TEXT_COLOR_BLUE;
            // Phase 6
            case GAMEPLAY -> SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess.\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands." + SET_TEXT_COLOR_BLUE;
        };
    }

    private GameData getGame(int id) throws GeneralServiceException {
        try {
            ListGamesResult listGamesResult = server.listGames(authtoken);
            for (GameData game : listGamesResult.games()) {
                if (game.gameID() == id) {
                    return game;
                }
            }
        } catch (Exception e) {
            throw new GeneralServiceException(e.getMessage());
        }
        return null;
    }

    private void printGame(ChessGame game, ChessGame.TeamColor view) {
        ChessBoardDisplay.drawChessBoard(game.getBoard(), view);
    }
}
