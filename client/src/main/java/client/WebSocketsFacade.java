package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketsFacade extends Endpoint {

    private Session session;

    public WebSocketsFacade(String url) {
        url = url.replace("http", "ws");
        URI socketURI = null;
        try {
            socketURI = new URI(url + "/ws");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            session = container.connectToServer(this, socketURI);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                notificationHandler.notify(notification);
                //ServerMessage userGameCommand = new Gson().fromJson(ctx.message(), ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {load_game();
                    }
                    case ERROR -> {
                    }
                    case NOTIFICATION -> {
                    }
                }

            }
        });

    }

    private void load_game(){}

    private void errorHandle(){}

    private void notification(){}

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
