package client;

import com.google.gson.Gson;
import exception.BadRequestException;
import records.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientCommunicator {
    private final ServerFacade server;
    // User Info
    private String authtoken;

    enum uiStates {
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }

    private uiStates uiState = uiStates.PRELOGIN;

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
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
                    //Implement in phase 6
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
            uiState = uiStates.POSTLOGIN;
            LoginResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
            authtoken = result.authToken();
            return String.format("You signed in as %s.", result.username());
        }
        throw new BadRequestException();
    }

    public String login(String... params) throws Exception {
        if (params.length >= 2) {
            uiState = uiStates.POSTLOGIN;
            LoginResult result = server.login(new LoginRequest(params[0], params[1]));
            authtoken = result.authToken();
            return String.format("You signed in as %s.", result.username());
        }
        throw new BadRequestException();
    }

    public String logout() throws Exception {
        assert uiState == uiStates.POSTLOGIN;
        server.logout(authtoken);
        uiState = uiStates.PRELOGIN;
        authtoken = null;
        return "You logged out.";
    }

    public String create(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;
        CreateGameResult result = server.createGame(authtoken, new CreateGameRequest(params[0]));
        return String.format("The Game ID is %d.", result.gameID());
    }

    public String list() throws Exception {
        assert uiState == uiStates.POSTLOGIN;
        ListGamesResult result = server.listGames(authtoken);
        var out = new StringBuilder();
        var gson = new Gson();
        for (GameData game : result.games()) {
            out.append(gson.toJson(game)).append('\n');
        }
        return out.toString();
    }

    public String join(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;
        server.joinGame(authtoken, new JoinGameRequest(params[0], Integer.parseInt(params[1])));
        uiState = uiStates.GAMEPLAY;
        //teamColor = (Objects.equals(params[0].toUpperCase(), "WHITE"))? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;
        return "Joined";
    }

    public String observe(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;
        //teamColor = ;
        server.joinGame(authtoken, new JoinGameRequest("WHITE", Integer.parseInt(params[0])));
        return "Watching";
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
            case GAMEPLAY -> SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess.\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands." + SET_TEXT_COLOR_BLUE;
        };
    }
}
