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
        ; // i have to somehow extract the game from the list using just its ID, which is a wierd problem lol.
    }
    // sdfgsdfgsdfgdfg 

    // so this is where I actually have to like, store everything which is going to be annoying.
    // I need to be able to implement the three different kinds of model data structures as like a hash map or something
    // and then we can have a function that just uhh throws back nothing to show that we have nothing in there.

}
