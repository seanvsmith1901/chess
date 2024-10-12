package dataaccess;

import model.*;

import chess.ChessGame;
import com.google.gson.Gson;

import javax.management.monitor.GaugeMonitor;
import java.util.Collection;

public interface DataAccess {

    // uncomment these one at a time while you start adding them to your memeory data access and go from there.

    UserData getUser(String userName) throws DataAccessException;
//
    void createUser(UserData currentUser) throws DataAccessException;
//
//    void createAuth(AuthData currentAuth) throws DataAccessException;
//
//    Boolean checkUserRequest(UserData returnedUser, String username, String password) throws DataAccessException;
//
//    // need to add functionality to return atuh objects from the checkUser(userRequsest) function under handler
//
//    AuthData getAuth(String authToken) throws DataAccessException;
//
//    void deleteAuth(AuthData currentAuth) throws DataAccessException;
//
//    Gson getGames() throws DataAccessException;
//
//    GameData GetGame(int gameID) throws DataAccessException;
//
//    void createGame(int gameID) throws DataAccessException;
//
//    void updateGame(GameData currentGame, String playerColor, String username) throws DataAccessException;

    Object deleteEverything() throws DataAccessException;

    // add functionality to check if a user specificed color is already in a game or not

}
