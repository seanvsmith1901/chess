package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.Dictionary;
import java.util.HashMap;

public class GameService {

    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public HashMap<String, GameData> getGames() throws DataAccessException {
        return dataAccess.getGames();
    }

    public void createGame(String gameName) throws DataAccessException {
        dataAccess.createGame(gameName);
    }

    public GameData getGame(String gameName) throws DataAccessException {
        return dataAccess.getGame(gameName);
    }

    public GameData getGameFromID(String gameID) throws DataAccessException {
        return dataAccess.getGameFromID(gameID);
    }


//    public Object getGames(AuthData authToken) {}
//    public Object createGame (AuthData authToken, String gameName) {}
//    public Object joinGame (AuthData authToken, String playerColor, int gameID) {}


}
