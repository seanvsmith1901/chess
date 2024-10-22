package dataaccess;

import model.GameData;

import java.util.HashSet;

public class GameService {

    private static DataAccess DATA_ACCESS;

    public GameService(DataAccess dataAccess) { // sets the correct dataaccess module
        this.DATA_ACCESS = dataAccess;
    }

    public HashSet<GameData> getGames() throws DataAccessException { // returns a hashset of all of the games
        return DATA_ACCESS.getGames();
    }

    public void createGame(String gameName) throws DataAccessException { // creates a new game
        DATA_ACCESS.createGame(gameName);
    }

    public GameData getGame(String gameName) throws DataAccessException { // gets a game from its game name
        return DATA_ACCESS.getGame(gameName);
    }

    public GameData getGameFromID(String gameID) throws DataAccessException { // gets the game from ID
        return DATA_ACCESS.getGameFromID(gameID);
    }
}
