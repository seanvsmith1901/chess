package webSocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();




    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> enter(action.getAuthToken(), action.getGameID(), action.getUsername(), action.getTeamColor(), session);
            //case EXIT -> exit(action.visitorName());
        }
    }

    private void enter(String authToken, Integer gameID, String username, String teamColor, Session session) throws IOException {
        connections.add(authToken, session);
        if(teamColor == null) {
            teamColor = "observer";
        }
        var message = String.format("%s has joined the game %s as %s", username, gameID, teamColor);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, serverMessage);


    }

}