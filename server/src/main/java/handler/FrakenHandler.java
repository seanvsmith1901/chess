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
        return authService.deleteEverything(dataAccess);
    }

    public Object registerUser(String username, String password, String email) throws DataAccessException {
        if (userService.getUser(username) != null) {
            throw new DataAccessException("User already exists");
        }
        else {
            userService.createUser(username, password, email);
            return authService.createAuthToken(username);
        }

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
        var currentGame = gameService.getGame(gameName);
        if (currentGame != null) {
            throw new DataAccessException("Game already exists");
        }
        gameService.createGame(gameName);


        GameData actualGame = gameService.getGame(gameName);
        HashMap<String, Integer> newMap = new HashMap<>();
        newMap.put(actualGame.gameName(), actualGame.gameID()); //
        return newMap;
    }

    public Object joinGame(String gameName, String gameColor, int gameID) throws DataAccessException {
        AuthData currentAuth = authService.getAuthObject(gameName);
        var currentGame = gameService.getGameFromID(gameID);
    }



    // here we can write functions that will pass off things downstream to get other things done.

}
