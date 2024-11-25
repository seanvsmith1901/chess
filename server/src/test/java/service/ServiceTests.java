package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {
    static final DataAccess DATA_ACCESS = new MemoryDataAccess();

    static final Services SERVICES = new Services(DATA_ACCESS);


    static private Server server;

    @BeforeAll
    static void clear() throws DataAccessException {
        SERVICES.clearDataBase();
    }

    @BeforeEach
    void tearDown() throws DataAccessException {
        SERVICES.clearDataBase();
    }

    @Test
    void checkClearDataBasePositive() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        var currentUser = SERVICES.getUser("West");
        var currentAuth = SERVICES.getAuth(SERVICES.getUser("West").name());
        SERVICES.createGame(currentAuth.authToken(), "BestGame");
        SERVICES.clearDataBase();
        assertThrows(DataAccessException.class, () -> SERVICES.getGames(currentAuth.authToken()));
    }


    @Test
    void registerUserPositive() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        var expectedUser = new UserData("West", "12345", "West@gmail.com");
        assertEquals(expectedUser.email(), SERVICES.getUser("West").email()); // just check they emails cause passwords hashed
    }

    @Test
    void registerUserNegative() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        assertThrows(DataAccessException.class, () ->
                SERVICES.registerUser("West", "12124", "we@gmail.com"));
    }

    @Test
    void createSession() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        SERVICES.logOutUser(SERVICES.getAuth("West").authToken()); // logs the user out
        SERVICES.createSession("West", "12345");
        assertNotNull(SERVICES.getAuth("West")); // creates an auth and certifies that auth returns
    }

    @Test
    void createSessionNegative() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        SERVICES.logOutUser(SERVICES.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> SERVICES.createSession("West", "14232"));
    }

    @Test
    void logOutUserPositive() throws DataAccessException { // pretty sure this is doing what I think its doing.
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        SERVICES.logOutUser(SERVICES.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> SERVICES.getAuth("West").authToken());
    }

    @Test
    void logOutUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> SERVICES.logOutUser("111111"));
    }

    @Test
    void getGamesPositive() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        var authToken = SERVICES.getAuth("West").authToken();
        SERVICES.createGame(authToken, "BestGame");
        var currentGames = SERVICES.getGames(authToken);
        assertNotNull(SERVICES.getGames(authToken));
    }

    @Test
    void getGamesNegative() throws DataAccessException {
        // tries to grab games with a nonexistent auth token.
        assertThrows(DataAccessException.class, () -> SERVICES.getGames("111111"));
    }

    @Test
    void createGamePositive() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        var authToken = SERVICES.getAuth("West").authToken();
        SERVICES.createGame(authToken, "BestGame");
        var newGame =
                new GameData(1, null, null, "BestGame", new ChessGame(), false);
        assertEquals(SERVICES.getGame("BestGame"), newGame);
    }

    @Test
    void createGamesNegative() throws DataAccessException { // doesn't let you overwrite a game
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        var authToken = SERVICES.getAuth("West").authToken();
        SERVICES.createGame(authToken, "BestGame");
        // checks to see if that game already exists when we create it
        assertThrows(DataAccessException.class, () -> SERVICES.createGame(authToken, "BestGame"));
    }

    @Test
    void joinGamePostitive() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        var authToken = SERVICES.getAuth("West").authToken();
        SERVICES.createGame(authToken, "BestGame");
        int gameID = SERVICES.getGame("BestGame").gameID();
        SERVICES.joinGame(authToken, "WHITE", String.valueOf(gameID));
        var expectedGame =
                new GameData(1, "West", null, "BestGame", new ChessGame(), false);
        // checks to make sure that we really have joined the game as white.
        assertEquals(expectedGame, SERVICES.getGame("BestGame"));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        SERVICES.registerUser("West", "12345", "West@gmail.com");
        SERVICES.registerUser("East", "11111", "East@gmail.com");
        var authToken = SERVICES.getAuth("West").authToken();
        var authToken2 = SERVICES.getAuth("East").authToken();
        SERVICES.createGame(authToken, "BestGame");
        int gameID = SERVICES.getGame("BestGame").gameID();
        SERVICES.joinGame(authToken, "WHITE", String.valueOf(gameID));
        assertThrows(DataAccessException.class, () ->
                SERVICES.joinGame(authToken2, "WHITE", String.valueOf(gameID)));
    }


}

