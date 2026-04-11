package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class NoticationMessage extends ServerMessage{
    private String message;

    public NoticationMessage(String message) {
        super(NOTIFICATION);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
