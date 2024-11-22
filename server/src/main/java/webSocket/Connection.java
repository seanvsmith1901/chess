package webSocket;

import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.HashMap;

import serializer.ChessPositionMapDeserializer;
import serializer.ChessPositionMapSerializer;
import serializer.GsonObject;

public class Connection {
    public String visitorName;
    public Session session;
    private static Gson serializer = new Gson();

    public Connection(String visitorName, Session session) {
        this.visitorName = visitorName;
        this.session = session;
        serializer = new GsonBuilder() // gets me my custom gson object.
                .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new ChessPositionMapSerializer())
                .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new ChessPositionMapDeserializer())
                .create();
    }

    public void send(Object msgObj) throws IOException {
        session.getRemote().sendString(serializer.toJson(msgObj)); // should work for chess games now
    }
}