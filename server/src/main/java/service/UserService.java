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


//    public AuthData checkUser(UserData user) {}
//    void logOutUser(AuthData authToken) {}
//    public AuthData CheckUser(UserData user) {}
//    void LogInUser(UserData newUser) {}



}
