package webSocket;

import chess.ChessGame;
import com.google.gson.Gson;

import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.messages.Error;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import service.*;

import java.io.IOException;
import java.util.Objects;

import serializer.GsonObject;

import websocket.messages.*;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    Services services; // allows us to modify the database from webSocketHandler.
    GsonObject serializer;

    public WebSocketHandler(Services services) {
        this.services = services;
        this.serializer = new GsonObject();
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> enter(action.getAuthToken(), action.getGameID(), action.getUsername(), action.getTeamColor(), session);
            case LEAVE -> leave(action.getAuthToken(), action.getGameID(), action.getUsername(), action.getTeamColor(), action.getGameName(), session);
            case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID(), action.getUsername(), action.getTeamColor(), session, action.getPeice(), action.getNewMove(), action.getPromotionPeice());
        }
    }

    private void enter(String authToken, Integer gameID, String username, String teamColor, Session session) throws IOException {
        connections.add(authToken, gameID, session);
        if(teamColor == null) {
            teamColor = "observer";
        }
        var message = String.format("%s has joined the game %s as %s", username, gameID, teamColor);
        var serverMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(authToken, gameID, serverMessage);
    }

    private void leave(String authToken, Integer gameID, String username, String teamColor, String gameName, Session session) throws IOException {
        connections.remove(authToken, gameID);
        if(teamColor == null) {
            teamColor = "observer";
        }
        var message = String.format("%s has left the game %s as %s", username, gameID, teamColor);
        var serverMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        try {
            services.removeUser(gameName, username);
            connections.broadcast(authToken, gameID, serverMessage);
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeMove(String authToken, Integer gameID, String username, String teamColor, Session session, String oldPosition, String newPosition, String promotionPeice) throws IOException {
        try {

            GameData gameData = services.makeMove(gameID, username, oldPosition, newPosition, teamColor, promotionPeice);
            ChessGame.TeamColor currColor = getTeamColor(teamColor);

            var message = String.format("%s has moved %s to %s", username, oldPosition, newPosition); // get the peice type maybe?

            var newMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, newMessage); // send out the message to everyone who DIDN"T make the move
            if (gameData.game().isInCheck(currColor)) {
                String formatter = " is in ";
                String state = "check";
                    if (gameData.game().isInCheckmate(currColor)) {
                        state += "mate";
                }

                String playerInCheck =  gameData.whiteUsername();
                if(currColor == ChessGame.TeamColor.BLACK) {
                    playerInCheck = gameData.blackUsername();
                }
                String finalMessage = (playerInCheck) + (formatter) + (state);
                Notification newNotificaiton = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, finalMessage);
                connections.broadcastAllNotification(gameID, newNotificaiton);
            }

            var serverMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, message, gameData);
            connections.broadcastAll(gameID, serverMessage); // send out the new gameboard to everyone




        }
        catch (DataAccessException e) {
            Error newError = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.directSend(authToken, gameID, newError); // send the error back to the user.
        }
    }


    private static ChessGame.TeamColor getTeamColor(String teamColor) throws DataAccessException {
        ChessGame.TeamColor currColor = null;

        // yes this is supposed to be swapped, its weird I know but it works. maybe.
        if(Objects.equals(teamColor, "WHITE") || Objects.equals(teamColor, "white")) {
            currColor = ChessGame.TeamColor.BLACK;
        }
        if(Objects.equals(teamColor, "BLACK") || Objects.equals(teamColor, "black")) {
            currColor = ChessGame.TeamColor.WHITE;
        }

        return currColor;
    }


}