package webSocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGame;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Map<String, Connection>> connections = new ConcurrentHashMap<>();
    // so we have the gameID or whatever as the key and then the players in that as the

    public void add(String visitorName, Integer gameName, Session session) {
        var connection = new Connection(visitorName, session);
        if (connections.containsKey(gameName)) {
            Map<String, Connection> gameConnections = this.connections.get(gameName);
            gameConnections.put(visitorName, connection);
            connections.put(gameName, gameConnections);
        }
        else {
            Map<String, Connection> gameConnections = new ConcurrentHashMap<>();
            gameConnections.put(visitorName, connection);
            connections.put(gameName, gameConnections);
        }
    }

    public void remove(String visitorName, Integer gameName) {
        Map<String, Connection> gameConnections = this.connections.get(gameName);
        gameConnections.remove(visitorName);
        connections.put(gameName, gameConnections);
        ;
    }

    public void broadcast(String excludeVisitorName, Integer gameName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        Map<String, Connection> current_map = connections.get(gameName);
        for (var c : current_map.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(notification);
                }
            } else {
                removeList.add(c);
            }
        }


        // revisit this in a second.
//        for (var c : removeList) {
//            connections.remove(c.visitorName);
//        }
    }

    public void broadcastAll(Integer gameName, LoadGame newGame) throws IOException {
        Map<String, Connection> current_map = connections.get(gameName);
        for (var c : current_map.values()) {
            if (c.session.isOpen()) {
                c.send(newGame);
            }
        }
    }

    public void broadcastAllNotification (Integer gameName, ServerMessage notification) throws IOException {
        Map<String, Connection> current_map = connections.get(gameName);
        for (var c : current_map.values()) {
            if (c.session.isOpen()) {
                c.send(notification);
            }
        }
    }


    public void directSend(String excludeVisitorName, Integer gameName, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        Map<String, Connection> current_map = connections.get(gameName);
        for (var c : current_map.values()) {
            if (c.session.isOpen()) {
                if (c.visitorName.equals(excludeVisitorName)) {
                    c.send(notification);
                }
            } else {
                removeList.add(c);
            }
        }
    }

    public void sendThroughSession(Session session, ServerMessage newError) throws IOException {
        Connection thisConnection = new Connection("null", session);
        thisConnection.send(newError);
    }

}