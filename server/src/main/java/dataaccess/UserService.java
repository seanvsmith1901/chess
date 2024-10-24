package dataaccess;

import model.GameData;
import model.UserData;

public class UserService {

    private static DataAccess dataAccess;

    public UserService(DataAccess dataAccess) { // sets the correct data access class
        this.dataAccess = dataAccess;
    }

    // creates a new user and inserts it
    public void createUser(String username, String password, String email) throws DataAccessException {
        var newUser = new UserData(username, password, email);
        dataAccess.createUser(newUser);
    }

    public UserData getUser(String username) throws DataAccessException { // finds the user
        return dataAccess.getUser(username);
    }

//    // replaces the null with a username
//    public void replaceUserInGame(GameData currGame, String username, String playerColor) throws DataAccessException {
//        dataAccess.addUser(currGame, username, playerColor);
//    }
//
//    public int getUserCount() throws DataAccessException { // just used for testing
//        return dataAccess.getUserCount();
//    }





}
