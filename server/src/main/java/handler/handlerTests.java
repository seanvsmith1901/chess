package handler;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import server.Server;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class handlerTests {
    static final DataAccess dataAccess = new MemoryDataAccess();

    static final FrakenHandler handler = new FrakenHandler(dataAccess);


    static private Server server;

    @BeforeAll
    static void clear() throws DataAccessException {
        handler.clearDataBase();
    }

    @BeforeEach
    void tearDown() throws DataAccessException {
        handler.clearDataBase();
    }

    @Test
    void checkClearDataBasePositive() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        var currentUser = handler.getUser("West");
        var currentAuth = handler.getAuth(handler.getUser("West").name());
        handler.createGame(currentAuth.authToken(), "BestGame");
        handler.clearDataBase();
        assertThrows(DataAccessException.class, () -> handler.getGames(currentAuth.authToken()));
    }


    @Test
    void registerUserPositive() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        var expectedUser = new UserData("West", "12345", "West@gmail.com");
        assertEquals(expectedUser, handler.getUser("West"));
    }

    @Test
    void registerUserNegative() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        assertThrows(DataAccessException.class, () -> handler.registerUser("West", "12124", "we@gmail.com")); // only checks for same username
    }

    @Test
    void createSession() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        handler.logOutUser(handler.getAuth("West").authToken()); // logs the user out
        handler.createSession("West", "12345");
        assertNotNull(handler.getAuth("West")); // creates an auth and certifies that we do return an auth for that user.
    }

    @Test
    void createSessionNegative() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        handler.logOutUser(handler.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> handler.createSession("West", "14232")); // wrong password
    }

    @Test
    void logOutUserPositive() throws DataAccessException { // pretty sure this is doing what I think its doing.
        handler.registerUser("West", "12345", "West@gmail.com");
        handler.logOutUser(handler.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> handler.getAuth("West").authToken()); // if it works, there will no longer be an auth token for this user.
    }

    @Test
    void logOutUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> handler.logOutUser("111111")); // auth token doesn't exist, make sure it tells us that
    }

    @Test
    void getGamesPositive() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        var authToken = handler.getAuth("West").authToken();
        handler.createGame(authToken, "BestGame");
        var currentGames = handler.getGames(authToken);
        assertNotNull(handler.getGames(authToken));
    }


}

