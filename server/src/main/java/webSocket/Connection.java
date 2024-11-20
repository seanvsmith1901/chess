package webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String visitorName;
    public Session session;

    public Connection(String visitorName, Session session) {
        this.visitorName = visitorName;
        this.session = session;
    }

    public void send(Object msgObj) throws IOException {
        session.getRemote().sendString(new Gson().toJson(msgObj));
    }
}