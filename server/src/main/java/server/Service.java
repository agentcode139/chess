package server;

import dataaccess.AuthData;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.CreateGameResult;
import server.result.ListGamesResult;
import server.result.LoginResult;

public class Service {
    static LoginResult addUser(RegisterRequest registerRequest){
        return new LoginResult(null,null);
    }
    static LoginResult loginUser(LoginRequest loginRequest){
        AuthData authData = new AuthData("","");
        return new LoginResult("","");
    }
    static void logoutUser(String authToken){

    }

    static CreateGameResult createGame(CreateGameRequest createGameRequest){
        return new CreateGameResult(0);
    }

    static ListGamesResult listGames(String authToken){
        return new ListGamesResult(null);
    }

    static void joinGame(JoinGameRequest joinGameRequest){

    }

    static void clearData(){

    }
}
