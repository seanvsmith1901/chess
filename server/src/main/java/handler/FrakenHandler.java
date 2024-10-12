package handler;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import service.AuthService;
import service.GameService;
import service.UserService;



public class FrakenHandler {
    private AuthService authService;
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;

    public FrakenHandler(DataAccess newDataAccess) {
        this.dataAccess = newDataAccess;
    }


    public Object clearDataBase() throws DataAccessException {
        return authService.deleteEverything(dataAccess);
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        userService.createUser(username, password, email);
    }




    // here we can write functions that will pass off things downstream to get other things done.

}
