package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import java.util.HashMap;


import model.UserData;

import java.util.Dictionary;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {

    static final AuthService authService = new AuthService(new MemoryDataAccess());
    static final GameService gameService = new GameService(new MemoryDataAccess());
    static final UserService userService = new UserService(new MemoryDataAccess());


    static private Server server;

    @BeforeAll
    static void clear() throws DataAccessException {
        authService.deleteEverything();
    }

    @Test
    void deleteDataBase() throws DataAccessException { // positive test - add something to the data base and make sure it burns correctly.
        assertEquals((authService.deleteEverything()), 0);

    }

    // auth service ones first for no other reason that

    @Test
    void createAuthToken() throws DataAccessException {
        var expectedAuthLength = 1;
        authService.createAuthToken("West");
        assertEquals(authService.getAuthSize(), expectedAuthLength);
    }

    @Test
    void tryToOverwriteUserName() throws DataAccessException {
        authService.createAuthToken("West");
        assertThrows(DataAccessException.class, () -> {authService.createAuthToken("West");});
    }

    @Test
    void getExistingAuthToken() throws DataAccessException {
        authService.createAuthToken("West");
        var expectedUserName = "West";
        assertEquals((authService.getAuthObject(expectedUserName)).userName(), expectedUserName);
    }

    @Test
    void grabNonexistentAuthToken() {
        assertThrows(DataAccessException.class, () -> {authService.getAuthObject("West");});
    }

    @Test
    void deleteExistingAuthToken() throws DataAccessException {
        var expectedLength = 0;
        authService.createAuthToken("West");
        AuthData currentAuth = authService.getAuthObject("West"); // IDK if that will work lol
        authService.deleteAuthObject(currentAuth);
        assertEquals(authService.getAuthSize(), expectedLength);

    }








    // we need two test cases for each of the above.

//}



}
