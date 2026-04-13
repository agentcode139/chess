package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(String message) {
        super(ERROR);
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
