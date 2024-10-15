package handler;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import service.AuthService;
import service.GameService;
import service.UserService;

import model.*;

import java.util.Objects;


public class FrakenHandler {
    private AuthService authService;
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;

    public FrakenHandler(DataAccess newDataAccess) { // remember to pass in the dataaccess objects and create new objects for the interface
        this.dataAccess = newDataAccess;
        authService = new AuthService(dataAccess);
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }


    public Object clearDataBase() throws DataAccessException {
        return authService.deleteEverything(dataAccess);
    }

    public Object registerUser(String username, String password, String email) throws DataAccessException {
        if (userService.getUser(username) != null) {
            throw new DataAccessException("User already exists");
        }
        else {
            userService.createUser(username, password, email);
            return authService.createAuthToken(username);
        }

    }

    public Object createSession(String username, String password) throws DataAccessException {
        UserData currentUser = userService.getUser(username);

        if (Objects.equals(password, currentUser.password())) { // checks that the user checks out
            return authService.createAuthToken(username);
        }
        else {
            throw new DataAccessException("Wrong password");
        }
    }




    // here we can write functions that will pass off things downstream to get other things done.

}
