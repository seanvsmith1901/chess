package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.mindrot.jbcrypt.BCrypt;



public class Services {
    private AuthService authService;
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;

    public Services(DataAccess newDataAccess) { // remember to pass in the dataaccess objects and create new objects for the interface
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

    public AuthData createSession(String username, String password) throws DataAccessException {
        UserData currentUser = userService.getUser(username);

        var hashedPassword = currentUser.password();


        if (BCrypt.checkpw(password, hashedPassword)) { // checks the hashed passowrd
            return authService.createAuthToken(username);
        }
        else {
            throw new DataAccessException("unauthorized");
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

    public HashSet<GameData> getGames(String authToken) throws DataAccessException {
        AuthData currentAuth = authService.getAuthObject(authToken);
        if (currentAuth != null) {
            return gameService.getGames();
        }
        else {
            throw new DataAccessException("Wrong auth token");
        }
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        AuthData currentAuth = authService.getAuthObject(authToken);

        gameService.createGame(gameName);


        GameData actualGame = gameService.getGame(gameName);
        HashMap<String, Integer> newMap = new HashMap<>();
        newMap.put(actualGame.gameName(), actualGame.gameID()); //
        return actualGame;
    }

    public GameData joinGame(String authToken, String playerColor, String gameID) throws DataAccessException {

        AuthData currentAuth = authService.getAuthObject(authToken);
        var username = currentAuth.username();
        GameData currentGame = gameService.getGameFromID(gameID);
        userService.replaceUserInGame(currentGame, username, playerColor);
        return currentGame;
    }

    // helper functions that only exists for tests, and might be used at a higher level.

    public UserData getUser(String username) throws DataAccessException {
        return userService.getUser(username);
    }

    public AuthData getAuth(String username) throws DataAccessException {
        return authService.getAuthObjectFromUserName(username);
    }

    public GameData getGame(String gameName) throws DataAccessException {
        return gameService.getGame(gameName);
    }

    public void removeUser(String gameName, String username) throws DataAccessException {
        gameService.removeUser(gameName, username);
    }

    public void makeMove(String gameName, String username, String peice, String newMove) throws DataAccessException {
        gameService.updateGame(gameName, username, peice, newMove);
    }


}
