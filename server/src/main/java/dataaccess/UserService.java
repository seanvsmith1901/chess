package dataaccess;

import model.GameData;
import model.UserData;

public class UserService {

    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) { // sets the correct data access class
        this.dataAccess = dataAccess;
    }

    public void createUser(String username, String password, String email) throws DataAccessException { // creates a new user and inserts it
        var newUser = new UserData(username, password, email);
        dataAccess.createUser(newUser);
    }

    public UserData getUser(String username) throws DataAccessException { // finds the user
        return dataAccess.getUser(username);
    }

    public void replaceUserInGame(GameData currentGame, String username, String playerColor) throws DataAccessException { // replaces the null with a username
        dataAccess.addUser(currentGame, username, playerColor);
    }

    public int getUserCount() throws DataAccessException { // just used for testing
        return dataAccess.getUserCount();
    }





}
