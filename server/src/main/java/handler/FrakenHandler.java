package handler;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import service.AuthService;
import service.GameService;
import service.UserService;

import model.*;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Objects;



public class FrakenHandler {
    private AuthService authService;
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;

    public FrakenHandler(DataAccess newDataAccess) { // remember to pass in the dataaccess objects and create new objects for the interface
        this.dataAccess = newDataAccess;
        authService = new AuthService(dataAccess);
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }


    public Object clearDataBase() throws DataAccessException {
        return authService.deleteEverything();
    }

    public Object registerUser(String username, String password, String email) throws DataAccessException {

        userService.createUser(username, password, email);
        return authService.createAuthToken(username);
    }

    public Object createSession(String username, String password) throws DataAccessException {
        UserData currentUser = userService.getUser(username);

        if (Objects.equals(password, currentUser.password())) { // checks that the user checks out
            return authService.createAuthToken(username);
        }
        else {
            throw new DataAccessException("Wrong password");
        }
    }

    public void logOutUser(String authToken) throws DataAccessException {
        AuthData currentAuth = authService.getAuthObject(authToken);
        if (currentAuth != null) {
            authService.deleteAuthObject(currentAuth);
        }
        else {
            throw new DataAccessException("Wrong auth token");
        }
    }

    public Object getGames(String authToken) throws DataAccessException {
        AuthData currentAuth = authService.getAuthObject(authToken);
        if (currentAuth != null) {
            return gameService.getGames();
        }
        else {
            throw new DataAccessException("Wrong auth token");
        }
    }

    public Object createGame(String authToken, String gameName) throws DataAccessException {
        AuthData currentAuth = authService.getAuthObject(authToken);

        gameService.createGame(gameName);


        GameData actualGame = gameService.getGame(gameName);
        HashMap<String, Integer> newMap = new HashMap<>();
        newMap.put(actualGame.gameName(), actualGame.gameID()); //
        return newMap;
    }

    public Object joinGame(String authToken, String playerColor, String gameID) throws DataAccessException {

        AuthData currentAuth = authService.getAuthObject(authToken);
        var username = currentAuth.userName();
        var currentGame = gameService.getGameFromID(gameID);
        userService.replaceUserInGame(currentGame, username, playerColor);
        return gameService.getGame(currentGame.gameName());
    }

    public UserData getUser(String username) throws DataAccessException {
        return userService.getUser(username);
    }

    public AuthData getAuth(String username) throws DataAccessException {
        return authService.getAuthObjectFromUserName(username);
    }


}
