package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.*;

import java.util.Dictionary;

public class GameService {

    public DataAccess dataAccess;

    public Gson getGames(AuthData authToke) {}
    public Gson createGame (AuthData authToken, String gameName) {}
    public Gson joinGame (AuthData authToken, String playerColor, int gameID) {}


}
