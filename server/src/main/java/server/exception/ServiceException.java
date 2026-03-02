package server.exception;

import com.google.gson.Gson;

public abstract class ServiceException extends Exception {
    int statusCode;
    record ErrorResponse(String message) {}

    protected ServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String responseAsJson(Gson gson) {
        return gson.toJson(new ErrorResponse("Error: " + getMessage()));
    }

    public String responseAsJson() {
        return responseAsJson(new Gson());
    }
}
