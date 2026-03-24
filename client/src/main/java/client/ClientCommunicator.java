package client;

import server.exception.BadRequestException;
import server.exception.ServiceException;
import server.request.LoginRequest;
import server.result.LoginResult;

import java.rmi.ServerException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientCommunicator {
    //private String serverUrl = "http://localhost:8080";
    private final ServerFacade server;

    enum uiStates{
        PRELOGIN,
        POSTLOGIN,
        GAMEPLAY
    }
    private uiStates uiState = uiStates.PRELOGIN;

    public ClientCommunicator(String serverUrl) throws ServerException {
        server = new ServerFacade(serverUrl);
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
                    case "list" -> list(params);
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
    // TODO: add calls for all functions
    // register
    public String register(String... params) throws Exception {
        return "";
    }

    public String login(String... params) throws Exception {
        if (params.length >= 2) {
            uiState = uiStates.POSTLOGIN;
            String username = params[0];
            LoginResult result = server.login(new LoginRequest(params[0],params[1]));
            return String.format("You signed in as %s.", username);
        }
        throw new BadRequestException();
    }

    // logout
    public String logout() throws Exception {
        assert uiState == uiStates.POSTLOGIN;
        server.logout();
        uiState = uiStates.PRELOGIN;
        return "You logged out";
    }

    // create
    public String create(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;

        return "";
    }

    // list
    public String list(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;

        return "";
    }

    // join
    public String join(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;

        return "";
    }

    // observe
    public String observe(String... params) throws Exception {
        assert uiState == uiStates.POSTLOGIN;

        return "";
    }

    public String help() {
        return switch (uiState) {
            case PRELOGIN -> """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
            case POSTLOGIN -> """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK]- a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
            case GAMEPLAY -> """
                    quit - playing chess
                    help - with possible commands
                    """;
        };
    }
}
