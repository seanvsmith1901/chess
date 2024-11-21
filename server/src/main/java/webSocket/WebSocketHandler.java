package webSocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;

import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import service.*;

import java.io.IOException;
import java.util.Timer;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    Services services; // allows us to modify the database from webSocketHandler.

    public WebSocketHandler(Services services) {
        this.services = services;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> enter(action.getAuthToken(), action.getGameID(), action.getUsername(), action.getTeamColor(), session);
            case LEAVE -> leave(action.getAuthToken(), action.getGameID(), action.getUsername(), action.getTeamColor(), action.getGameName(), session);
            case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameName(), action.getUsername(), action.getTeamColor(), session, action.getPeice(), action.getNewMove());
        }
    }

    private void enter(String authToken, Integer gameID, String username, String teamColor, Session session) throws IOException {
        connections.add(authToken, gameID, session);
        if(teamColor == null) {
            teamColor = "observer";
        }
        var message = String.format("%s has joined the game %s as %s", username, gameID, teamColor);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, serverMessage);
    }

    private void leave(String authToken, Integer gameID, String username, String teamColor, String gameName, Session session) throws IOException {
        connections.remove(authToken, gameID);
        if(teamColor == null) {
            teamColor = "observer";
        }
        var message = String.format("%s has left the game %s as %s", username, gameID, teamColor);
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            services.removeUser(gameName, username);
            connections.broadcast(authToken, gameID, serverMessage);
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeMove(String authToken, String gameName, String username, String teamColor, Session session, String peice, String newMove) throws IOException {
        try {
            services.makeMove(gameName, username, peice, newMove, teamColor);
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

}