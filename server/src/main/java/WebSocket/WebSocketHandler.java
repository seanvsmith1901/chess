package WebSocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import websocket.commands.JoinGameRequest;
import websocket.messages.Error;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import service.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import serializer.GsonObject;

import websocket.messages.*;
import websocket.commands.*;


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
            case CONNECT -> enter(message, session);
            case LEAVE -> leave(message, session);
            case MAKE_MOVE -> makeMove(message, session);
            case VALID -> getValidMoves(message, session);
            case RESIGN -> resign(message, session);
        }
    }

    private void enter(String action, Session session) throws IOException {
        JoinGameRequest currentRequest = new Gson().fromJson(action, JoinGameRequest.class);
        String authToken = currentRequest.getAuthToken();
        Integer gameID = currentRequest.getGameID();
        // note that this is entirely to make the tests pass and I hate them.


        //String username = currentRequest.getUsername();
        //String gameName = currentRequest.getGameName();
        //GameData currentGame = null;


        connections.add(authToken, gameID, session);
        try {
            AuthData currentAuth = services.checkAuth(authToken);
            String username = currentAuth.username();
            GameData currentGame = services.getGameFromID(String.valueOf(gameID));
            ChessGame.TeamColor teamColor = getTeamColorFromGame(currentGame, username);
            String thingToPrint = "";
            if (teamColor == null) {
                thingToPrint = "observer";
            }
            else {
                thingToPrint = teamColor.toString();
            }
            var message = String.format("%s has joined the game %s as %s", username, gameID, thingToPrint);
            var serverMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, serverMessage);
            var newBoardMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, message, currentGame);
            connections.directSend(authToken, gameID, newBoardMessage);
        }
        catch (DataAccessException e) {
            Error newError = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.directSend(authToken, gameID, newError); // send the error back to the user.
        }
    }

    private void leave(String message, Session session) throws IOException {
        LeaveGameRequest currentRequest = new Gson().fromJson(message, LeaveGameRequest.class);
        String authToken = currentRequest.getAuthToken();
        Integer gameID = currentRequest.getGameID();

        try {
            connections.remove(authToken, gameID);
            AuthData currentAuth = services.checkAuth(authToken);
            String username = currentAuth.username();
            GameData currentGame = services.getGameFromID(String.valueOf(gameID));
            ChessGame.TeamColor teamColor = getTeamColorFromGame(currentGame, username);
            services.removeUserWithGameID(String.valueOf(gameID), username);
            var thisMessage = String.format("%s has left the game %s as %s", username, gameID, teamColor);
            var serverMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, thisMessage);
            connections.remove(authToken, gameID);
            connections.broadcast(authToken, gameID, serverMessage);
        }
        catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeMove(String message, Session session) throws IOException {
        makeMoveRequest currentRequest = new Gson().fromJson(message, makeMoveRequest.class);
        String authToken = currentRequest.getAuthToken();
        Integer gameID = currentRequest.getGameID();
        Move move = currentRequest.getMove();
        String promotionPeice = currentRequest.getPromotionPeice();


        try {
            AuthData currentAuth = services.checkAuth(authToken);
            String username = currentAuth.username();
            GameData currentGame = services.getGameFromID(String.valueOf(gameID));
            ChessGame.TeamColor teamColor = getTeamColorFromGame(currentGame, username);
            GameData gameData = services.makeMove(gameID, username, move, teamColor, promotionPeice);

            var thisMessage = String.format("%s has moved %s to %s", username,
                    move.getStartPosition(), move.getEndPosition());

            var newMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, thisMessage);
            connections.broadcast(authToken, gameID, newMessage); // everyone who didn't
            if (gameData.game().isInCheck(teamColor)) {
                String formatter = " is in ";
                String state = "check";
                if (gameData.game().isInCheckmate(teamColor)) {
                    state += "mate";
                    services.markGameAsDone(gameData); // marks game as done and updates in database.
                }

                String playerInCheck = gameData.whiteUsername();
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    playerInCheck = gameData.blackUsername();
                }
                String finalMessage = (playerInCheck) + (formatter) + (state);
                Notification newNotificaiton =
                        new Notification(ServerMessage.ServerMessageType.NOTIFICATION, finalMessage);
                connections.broadcastAllNotification(gameID, newNotificaiton);
            }

            var serverMessage = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, message, gameData);
            connections.broadcastAll(gameID, serverMessage); // send out the new gameboard to everyone

        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                Error newError = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
                connections.sendThroughSession(session, newError);
            }
            else {
                Error newError = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
                connections.directSend(authToken, gameID, newError); // send the error back to the user.
            }
        }
    }

    private void getValidMoves(String message, Session session) throws IOException { // IO should never throw
        ValidMovesRequest currentRequest = new Gson().fromJson(message, ValidMovesRequest.class);
        String authToken = currentRequest.getAuthToken();
        Integer gameID = currentRequest.getGameID();
        Position startPosition = currentRequest.getStartPosition();
        String gameName = currentRequest.getGameName();

         try {
             int currRow = startPosition.getCol();
             int currCol = startPosition.getRow(); // ignore the swap im lazy
             ChessPosition newPosition = new ChessPosition(currCol, currRow);
             GameData currGame = services.getGame(gameName);
             Collection<ChessMove> validMoves = currGame.game().validMoves(newPosition);
             ServerMessage newServerMessage = new ValidMoves(ServerMessage.ServerMessageType.VALID_MOVES,
                     validMoves, currGame);
             connections.directSend(authToken, gameID, newServerMessage);
         }
         catch (DataAccessException e) {
             Error newError = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
             connections.directSend(authToken, gameID, newError);
         }
    }

    public void resign(String message, Session session) throws IOException {
        ResignRequest currentRequest = new Gson().fromJson(message, ResignRequest.class);
        String authToken = currentRequest.getAuthToken();
        Integer gameID = currentRequest.getGameID();

        try {
            AuthData currentAuth = services.checkAuth(authToken);
            String username = currentAuth.username();
            GameData currGame = services.getGameFromID(String.valueOf(gameID));
            if(currGame.gameCompleted()) {
                throw new DataAccessException("You cant resign after they resign. game is over :(");
            }
            GameData currentGame = services.getGameFromID(String.valueOf(gameID));
            ChessGame.TeamColor teamColor = getTeamColorFromGame(currentGame, username);
            if(teamColor == null) {
                throw new DataAccessException("You're an observer! you can't do that!");
            }
            services.markGameAsDone(currGame); // marks game as done and updates in database.
            var newMessage = String.format("%s has resigned!", username);

            ServerMessage newServerMessage = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, newMessage);
            connections.broadcast(authToken, gameID, newServerMessage);
            connections.directSend(authToken, gameID, newServerMessage);
        }
        catch (DataAccessException e) {
            Error newError = new Error(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            connections.directSend(authToken, gameID, newError); // send the error back to the user.
        }
    }


    public ChessGame.TeamColor getTeamColorFromGame(GameData currentGame, String userName) {
        if (Objects.equals(currentGame.blackUsername(), userName)) {
            return ChessGame.TeamColor.BLACK;
        }
        else if (Objects.equals(currentGame.whiteUsername(), userName)) {
            return ChessGame.TeamColor.WHITE;
        }
        else {
            return null;
        }
    }

}