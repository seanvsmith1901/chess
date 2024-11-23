package websocket;

import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


import model.*;

import serializer.ChessPositionMapDeserializer;
import serializer.ChessPositionMapSerializer;
import ui.Bucket;
import websocket.commands.validMovesRequest;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.commands.*;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    private static Gson serializer = new Gson();
    public Bucket bucket;
    public String username;

    public WebSocketFacade(String url, NotificationHandler notificationHandler, Bucket bucket, String username) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            this.bucket = bucket;
            this.username = username;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            serializer = new GsonBuilder() // gets me my custom gson object.
                    .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new ChessPositionMapSerializer())
                    .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new ChessPositionMapDeserializer())
                    .create();


            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = serializer.fromJson(message, ServerMessage.class);
                    if(notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        Notification newNotification = serializer.fromJson(message, Notification.class);
                        notificationHandler.notify(newNotification);
                    }
                    else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGame currentGame = serializer.fromJson(message, LoadGame.class); // does this work? who knows!!?
                        bucket.setChessGame(currentGame.getGame(), username);
                    }
                    else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                        Error currentError = serializer.fromJson(message, Error.class);
                        notificationHandler.displayError(currentError);
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authToken, Integer gameID, String username, String teamColor) throws ResponseException {
        try {
            var action = new JoinGameRequest(UserGameCommand.CommandType.CONNECT, authToken, gameID, username, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, "not sure what went wrong but try again");
        }
    }

    public void leaveGame(String authToken, Integer gameID, String username, String gameName, String teamColor) throws ResponseException {
        try {
            var action = new leaveGameRequest(UserGameCommand.CommandType.LEAVE, authToken, gameID, username, gameName, teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }
        catch (IOException ex) {
            throw new ResponseException(500, "not sure what went wrong but try again");
        }
    }

    public void makeMove(String authToken, Integer gameID, String username, String teamColor, String oldPosition, String newPosition, String promotionPeice, String gameName) throws ResponseException {
        try {
            var action = new makeMoveRequest(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, username, teamColor, gameName, oldPosition, newPosition, promotionPeice);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        }
        catch (IOException ex) {
            throw new ResponseException(500, "not sure what went wrong but try again");
        }
    }

//    public Object highlightMoves(String authToken, String startingPosition) {
//        try {
//            var action = new validMovesRequest(startingPosition);
//        }
//    }

}