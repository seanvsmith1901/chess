package dataaccess;

import model.GameData;
import model.UserData;

public class UserService {

    private static DataAccess DATA_ACCESS;

    public UserService(DataAccess dataAccess) { // sets the correct data access class
        this.DATA_ACCESS = dataAccess;
    }

    // creates a new user and inserts it
    public void createUser(String username, String password, String email) throws DataAccessException {
        var newUser = new UserData(username, password, email);
        DATA_ACCESS.createUser(newUser);
    }

    public UserData getUser(String username) throws DataAccessException { // finds the user
        return DATA_ACCESS.getUser(username);
    }

    // replaces the null with a username
    public void replaceUserInGame(GameData currGame, String username, String playerColor) throws DataAccessException {
        DATA_ACCESS.addUser(currGame, username, playerColor);
    }

    public int getUserCount() throws DataAccessException { // just used for testing
        return DATA_ACCESS.getUserCount();
    }





}
