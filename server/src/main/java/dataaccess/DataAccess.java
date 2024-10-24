package dataaccess;

import model.*;
import java.util.HashSet;

public interface DataAccess {

    // gotta reimplement these fetchers one by one, such just start from the top IG.

    Object deleteEverything() throws DataAccessException;

    UserData getUser(String userName) throws DataAccessException;

    void createUser(UserData currentUser) throws DataAccessException;
//
//    AuthData getAuthObject(String authToken) throws DataAccessException;
//
//    void deleteAuthToken(AuthData authToken) throws DataAccessException;
//
//    HashSet<GameData> getGames() throws DataAccessException;
//
//    void createGame(String gameName) throws DataAccessException;
//
//    GameData getGame(String gameName) throws DataAccessException;
//
//    GameData getGameFromID(String gameID) throws DataAccessException;
//
//    void addUser(GameData currentGame, String username, String playerColor) throws DataAccessException;
//
//    int getAuthSize() throws DataAccessException;
//
    void addAuth(AuthData currentAuth) throws DataAccessException;
//
//    AuthData getAuthObjectFromUsername(String username) throws DataAccessException;
//
//    int getUserCount() throws DataAccessException;
//
//

}
