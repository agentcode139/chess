package client;

import server.exception.BadRequestException;
import server.exception.ServiceException;
import server.request.LoginRequest;
import server.request.RegisterRequest;
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
    // TODO: add calls for all functions
    // register
    public String register(String... params) throws Exception {
        if (params.length >= 3) {
            uiState = uiStates.POSTLOGIN;
            LoginResult result = server.register(new RegisterRequest(params[0],params[1],params[2]));
            return String.format("You signed in as %s.", result.username());
        }
        throw new BadRequestException();
    }

    public String login(String... params) throws Exception {
        if (params.length >= 2) {
            uiState = uiStates.POSTLOGIN;
            LoginResult result = server.login(new LoginRequest(params[0],params[1]));
            return String.format("You signed in as %s.", result.username());
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
    public String list() throws Exception {
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
            case PRELOGIN ->
                    SET_TEXT_COLOR_BLUE +
                    "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_MAGENTA + "- to create an account\n" + SET_TEXT_COLOR_BLUE +
                    "login <USERNAME> <PASSWORD> " + SET_TEXT_COLOR_MAGENTA + "- to play chess\n" + SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n" + SET_TEXT_COLOR_BLUE;
            case POSTLOGIN ->
                    SET_TEXT_COLOR_BLUE +
                    "create <NAME> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE +
                    "list " + SET_TEXT_COLOR_MAGENTA + "- games\n" + SET_TEXT_COLOR_BLUE +
                    "join <ID> [WHITE|BLACK] " + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE +
                    "observe <ID> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE +
                    "logout " + SET_TEXT_COLOR_MAGENTA + "- when you are done\n" + SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n" + SET_TEXT_COLOR_BLUE;
            case GAMEPLAY ->
                    SET_TEXT_COLOR_BLUE +
                    "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n" + SET_TEXT_COLOR_BLUE +
                    "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n" + SET_TEXT_COLOR_BLUE;
        };
    }
}
