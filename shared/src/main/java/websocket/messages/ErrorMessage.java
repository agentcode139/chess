package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class ErrorMessage extends ServerMessage{
    String errorMessage;

    public ErrorMessage(String message) {
        super(ERROR);
        this.errorMessage = message;
    }

    String getErrorMessage(){
        return errorMessage;
    }
}
