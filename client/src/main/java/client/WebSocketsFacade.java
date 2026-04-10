package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
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
//                System.out.println(message);
//                System.out.println("\nEnter another message you want to echo:");
                ServerMessage serverNotification = new Gson().fromJson(message, ServerMessage.class);
//                notificationHandler.notify(notification);

            }
        });

    }



    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
