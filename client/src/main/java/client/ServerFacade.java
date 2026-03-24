package client;

import com.google.gson.Gson;
import exception.GeneralServiceException;
import exception.ServiceException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public LoginResult register(RegisterRequest registerRequest) throws Exception {
        var path = "/user";
        var request = buildRequest("POST", path, registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var path = "/session";
        var request = buildRequest("POST", path, loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String token) throws Exception {
        var path = "/session";
        var request = buildRequest("DELETE", path, null, token);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(String token, CreateGameRequest createGameRequest) throws Exception {
        var path = "/game";
        var request = buildRequest("POST", path, createGameRequest, token);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(String token, JoinGameRequest joinGameRequest) throws Exception {
        var path = "/game";
        var request = buildRequest("PUT", path, joinGameRequest, token);
        var response = sendRequest(request);
        handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(String token) throws Exception {
        var path = "/game";
        var request = buildRequest("GET", path, null, token);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null,null);
        sendRequest(request);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String token) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (token != null) {
            request.setHeader("Authorization", token);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServiceException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new GeneralServiceException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ServiceException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw GeneralServiceException.fromJson(body);
            }

            throw new GeneralServiceException("other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status == 200;
    }
}
