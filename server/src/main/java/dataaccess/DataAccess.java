package dataaccess;



import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Collection;

public interface DataAccess {
    User getUser(string userName);
    void createUser(User userData);

    void createAuth(auth authData);

    bool checkUserRequest(User returnedUser, string username, string password);

    // need to add functionality to return atuh objects from the checkUser(userRequsest) function under handler

    auth getAuth(string authToken);

    void deleteAuth(auth authToken);

    Gson getGames();

    game GetGame(string gameID);

    void createGame(string gameID);

    void updateGame(Game currentGame, string playerColor, string username);

    void deleteEverything();

    // add functionality to check if a user specificed color is already in a game or not





}
