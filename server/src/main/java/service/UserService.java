package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.eclipse.jetty.server.Authentication;

import java.util.Dictionary;

public class UserService {

    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        var newUser = new UserData(username, password, email);
        dataAccess.createUser(newUser);
    }

    public UserData getUser(String username) throws DataAccessException {
        return dataAccess.getUser(username);
    }

    public void replaceUserInGame(GameData currentGame, String username, String playerColor) throws DataAccessException {
        dataAccess.addUser(currentGame, username, playerColor);
    }

    public int getUserCount() throws DataAccessException {
        return dataAccess.getUserCount();
    }





}
