package handler;

import dataaccess.DataAccessException;
import service.AuthService;
import service.GameService;
import service.UserService;



public class FrakenHandler {
    private AuthService authService;
    private GameService gameService;
    private UserService userService;


    public void clearDataBase() throws DataAccessException {
        authService.deleteEverything();
    }


    // here we can write functions that will pass off things downstream to get other things done.

}
