package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;


public class MemoryDataAccess implements DataAccess {

    HashMap<String, AuthData> authenticationTokens = new HashMap<String, AuthData>();
    HashMap<String, GameData> gameTokens = new HashMap<String, GameData>();
    HashMap<String, UserData> userTokens = new HashMap<String, UserData>();

    public Object deleteEverything() {
        authenticationTokens.clear();
        gameTokens.clear();
        userTokens.clear();
        return null;
    }

    public UserData getUser(String username) throws DataAccessException {
        if(userTokens.containsKey(username)) {
            return userTokens.get(username);
        }
        throw new DataAccessException("User not found");
    }

    public void createUser(UserData currentUser) throws DataAccessException {
        var userName = currentUser.name();
        if(userTokens.containsKey(userName)) {
            throw new DataAccessException("That username is already taken my guy, be more creative");
        }
        userTokens.put(userName, currentUser);
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException {
        if(authenticationTokens.containsKey(authToken)) {
            return authenticationTokens.get(authToken);
        }
        else {
            throw new DataAccessException("token does not exists");
        }
    }

    public void deleteAuthToken(AuthData authToken) throws DataAccessException {
        for(AuthData auth: authenticationTokens.values()) {
            if (auth.equals(authToken)) {
                authenticationTokens.remove(auth.authToken());
                return;
            }
        }
        throw new DataAccessException("token has never existed you tripping");

    }

    public HashMap<String, GameData> getGames() throws DataAccessException {
        return gameTokens; // should just return the whole fetching dictionary.
    }

    public void createGame(String gameName) throws DataAccessException {
        // i should check that there isn't already an exisitng game
        // stupid gameID is going to be the total number of games
        var gameID = gameTokens.size()+1;
        if (gameTokens.containsKey(gameName)) {
            throw new DataAccessException("gameName already exists");
        }
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameTokens.put(gameName, newGame);
    }

    public GameData getGame(String gameName) throws DataAccessException {
        if(gameTokens.containsKey(gameName)) {
            return gameTokens.get(gameName);
        }
        throw new DataAccessException("that game don't exist cheif try again");
    }

    public GameData getGameFromID(String gameID) throws DataAccessException {
        for(GameData game: gameTokens.values()) {
            if(String.valueOf(game.gameID()).equals(gameID)) {
                return game;
            }
        }
        throw new DataAccessException("Game does not exist");
    }

    public void addUser(GameData currentGame, String username, String playerColor) throws DataAccessException {
        if(playerColor.equals("white")){
            if (currentGame.whiteUsername() != null) {
                throw new DataAccessException("that color is taken");
            }
            gameTokens.remove(currentGame.gameName());
            GameData newGame = new GameData(currentGame.gameID(), username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
            gameTokens.put(currentGame.gameName(), newGame);
        }
        else {
            if (currentGame.blackUsername() != null) {
                throw new DataAccessException("that color is taken");
            }
            gameTokens.remove(currentGame.gameName());
            GameData newGame = new GameData(currentGame.gameID(), currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
            gameTokens.put(currentGame.gameName(), newGame); // keep the same game name
        }
    }
    public int getAuthSize() {
        return authenticationTokens.size();
    }

    public void addAuth(AuthData currentAuth) throws DataAccessException {
        for(AuthData auth: authenticationTokens.values()) {
            if (auth.userName().equals(currentAuth.userName())) {
                throw new DataAccessException("that username is taken");
            }
        }
        authenticationTokens.put(currentAuth.authToken(), currentAuth);
    }

    public AuthData getAuthObjectFromUsername(String username) throws DataAccessException {
        for(AuthData auth: authenticationTokens.values()) {
            if (auth.userName().equals(username)) {
                return auth;
            }
        }
        throw new DataAccessException("username does not exist");
    }

    public int getUserCount() throws DataAccessException {
        return userTokens.size();
    }



}
