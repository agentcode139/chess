package server.websockets;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(ServerMessage notification) throws IOException {
        String msg = gson.toJson(notification);
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                c.getRemote().sendString(msg);
            }
        }
    }
    public void broadcastExcept(Session excludeSession, ServerMessage notification) throws IOException {
        String msg = gson.toJson(notification);
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcastExclusiveInclude(Session excludeSession, Collection<Session> includeSessions, ServerMessage notification) throws IOException {
        String msg = gson.toJson(notification);
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (includeSessions.contains(c) && !c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcastTo(Session targetSession, ServerMessage notification) throws IOException {
        String msg = gson.toJson(notification);
        if (targetSession.isOpen()){
            targetSession.getRemote().sendString(msg);
        }
    }
}
