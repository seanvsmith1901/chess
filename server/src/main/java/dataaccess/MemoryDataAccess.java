package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;


public class MemoryDataAccess implements DataAccess {

    Map<String, AuthData> authenticationTokens = new HashMap<String, AuthData>();
    Map<String, GameData> gameTokens = new HashMap<String, GameData>();
    Map<String, UserData> userTokens = new HashMap<String, UserData>();

    public Object deleteEverything() {
        authenticationTokens.clear();
        gameTokens.clear();
        userTokens.clear();
        return this;
    }

    public UserData getUser(String username) {
        return userTokens.get(username);
    }

    public void createUser(UserData currentUser) {
        var userName = currentUser.name();
        userTokens.put(userName, currentUser);
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException {
        if(authenticationTokens.containsKey(authToken)) {
            return authenticationTokens.get(authToken);
        }
        else {
            throw new DataAccessException("Game already exists");
        }
    }

    public void deleteAuthToken(AuthData authToken) throws DataAccessException {
        authenticationTokens.remove(authToken);
    }

    public Object getGames() throws DataAccessException {
        return gameTokens; // should just return the whole fetching dictionary.
    }

    public void createGame(String gameName) throws DataAccessException {
        // i should check that there isn't already an exisitng game
        // stupid gameID is going to be the total number of games
        var gameID = gameTokens.size()+1;
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameTokens.put(gameName, newGame);
    }

    public GameData getGame(String gameName) throws DataAccessException {
        return gameTokens.get(gameName);
    }

    public GameData getGameFromID(Integer gameID) throws DataAccessException {
        for(GameData game: gameTokens.values()) {
            if(game.gameID() == gameID) {
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
}
