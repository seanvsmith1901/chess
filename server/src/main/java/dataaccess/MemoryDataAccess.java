package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.mindrot.jbcrypt.BCrypt;

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
        throw new DataAccessException("unauthorized");
    }

    public void createUser(UserData currentUser) throws DataAccessException {
        var userName = currentUser.name();
        if(userTokens.containsKey(userName)) {
            throw new DataAccessException("already taken");
        }
        String hashedPassword = BCrypt.hashpw(currentUser.password(), BCrypt.gensalt());

        UserData newUser = new UserData(userName, hashedPassword, currentUser.email());
        userTokens.put(userName, newUser);
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException {
        if(authenticationTokens.containsKey(authToken)) {
            return authenticationTokens.get(authToken);
        }
        else {
            throw new DataAccessException("unauthorized");
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

    public HashSet<GameData> getGames() throws DataAccessException {
        HashSet<GameData> gameData = new HashSet<GameData>();
        gameData.addAll(gameTokens.values());
        return gameData; // should just return the whole fetching dictionary.
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
        if(playerColor.equals("WHITE")){
            if (currentGame.whiteUsername() != null) {
                throw new DataAccessException("that color is taken");
            }
            gameTokens.remove(currentGame.gameName());
            var currGameID = currentGame.gameID();
            var blackUserName = currentGame.blackUsername();
            GameData newGame =
                    new GameData(currGameID, username, blackUserName, currentGame.gameName(), currentGame.game());
            gameTokens.put(currentGame.gameName(), newGame);
        }
        else {
            if (currentGame.blackUsername() != null) {
                throw new DataAccessException("that color is taken");
            }
            gameTokens.remove(currentGame.gameName());
            var currGameID = currentGame.gameID();
            var whiteUserName = currentGame.whiteUsername();
            GameData newGame =
                    new GameData(currGameID, whiteUserName, username, currentGame.gameName(), currentGame.game());
            gameTokens.put(currentGame.gameName(), newGame); // keep the same game name
        }
    }
    public int getAuthSize() {
        return authenticationTokens.size();
    }

    public void addAuth(AuthData currentAuth) throws DataAccessException {
        authenticationTokens.put(currentAuth.authToken(), currentAuth); // just add it?? bro thats SO stupid
    }

    public AuthData getAuthObjectFromUsername(String username) throws DataAccessException {
        for(AuthData auth: authenticationTokens.values()) {
            if (auth.username().equals(username)) {
                return auth;
            }
        }
        throw new DataAccessException("unauthorized");
    }

    public int getUserCount() throws DataAccessException {
        return userTokens.size();
    }



}
