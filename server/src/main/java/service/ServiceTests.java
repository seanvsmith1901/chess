package service;

import chess.ChessGame;
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

    @BeforeEach
    void tearDown() throws DataAccessException {
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
        assertEquals((authService.getAuthObjectFromUserName(expectedUserName)).userName(), expectedUserName);
    }

    @Test
    void grabNonexistentAuthToken() {
        assertThrows(DataAccessException.class, () -> {authService.getAuthObject("West");});
    }

    @Test
    void grabNonExistentUserName() {
        assertThrows(DataAccessException.class, () -> {authService.getAuthObjectFromUserName("West");});
    }


    @Test
    void deleteExistingAuthToken() throws DataAccessException {
        var thisShouldBeRight = 0;
        authService.createAuthToken("West");
        AuthData currentAuth = authService.getAuthObjectFromUserName("West"); // IDK if that will work lol
        authService.deleteAuthObject(currentAuth);
        assertEquals(0, authService.getAuthSize());

    }

    @Test
    void deleteExistingAuthTokenGivenUsername() throws DataAccessException {
        authService.createAuthToken("West");
        var authToken = authService.getAuthObjectFromUserName("West").authToken();
        AuthData currentAuth = authService.getAuthObject(authToken);
        authService.deleteAuthObject(currentAuth);
        assertEquals(0, authService.getAuthSize());
    }

    @Test
    void checkDeleteAuthObject() throws DataAccessException {
        authService.createAuthToken("West");
        authService.createAuthToken("East");
        var currentAuth = authService.getAuthObjectFromUserName("West");
        authService.deleteAuthObject(currentAuth);
        assertEquals(1, authService.getAuthSize());
    }

    @Test
    void checkDeleteAuthObjectNotThere() throws DataAccessException {
        var newAuth = new AuthData("asdsfd", "West");
        assertThrows(DataAccessException.class, () -> {authService.deleteAuthObject(newAuth);});
    }

    @Test
    void checkAuthSize() throws DataAccessException {
        var newAuth = new AuthData("asdsfd", "West");
        authService.createAuthToken("West");
        assertEquals(1, authService.getAuthSize());
    }

//    @Test not sure how to write this test, as I don't know how returnAuthSuze could ever throw an exception.
//    void checkAuthSize() throws DataAccessException {
//        var newAuth = new AuthData("asdsfd", "West");
//        authService.createAuthToken("West");
//
//    }

    // ** end of the authServiceTests *
    //GameData(1, "white", "black", "bestGame", new ChessGame())   fake game if you need it

    @Test
    void getGamesPositive() throws DataAccessException {
        var newGame = new GameData(1, "white", "black", "bestGame", new ChessGame());
        gameService.createGame("bestGame");
        assertEquals(gameService.getGames(), newGame);
    }



//getGames()
//
//
//createGame
//
//
//getGame
//
//getGameFromID











    // we need two test cases for each of the above.

//}



}
