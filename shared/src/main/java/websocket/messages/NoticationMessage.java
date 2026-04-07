package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class NoticationMessage extends ServerMessage{
    String message;

    public NoticationMessage(String message) {
        super(NOTIFICATION);
        this.message = message;
    }

    String getMessage(){
        return message;
    }
}
