package client;

import com.google.gson.Gson;
import dataaccess.records.UserData;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import server.exception.GeneralServiceException;
import server.exception.ServiceException;
import server.request.LoginRequest;
import server.request.RegisterRequest;
import server.result.LoginResult;

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

    // TODO: all functions
    public LoginResult register(RegisterRequest registerRequest) throws Exception {
        var path = "/user";
        var request = buildRequest("POST", path, registerRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    // login
    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var path = "/session";
        var request = buildRequest("POST", path, loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    // logout

    // createGame

    // joinGame

    // listGames

    //clear
    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
                // throw Exception.fromJson(body);
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
