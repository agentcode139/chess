package server;

import dataaccess.AuthData;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;

public class Service {
    LoginResult addUser(RegisterRequest registerRequest){
        return new LoginResult("","");
    }
    LoginResult loginUser(LoginRequest loginRequest){
        AuthData authData = new AuthData("","");
        return new LoginResult(authData);
    }
    void logoutUser(String authToken){

    }
}
