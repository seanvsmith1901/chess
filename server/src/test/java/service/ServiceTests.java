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
    static final DataAccess dataAccess = new MemoryDataAccess();

    static final Services services = new Services(dataAccess);


    static private Server server;

    @BeforeAll
    static void clear() throws DataAccessException {
        services.clearDataBase();
    }

    @BeforeEach
    void tearDown() throws DataAccessException {
        services.clearDataBase();
    }

    @Test
    void checkClearDataBasePositive() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        var currentUser = services.getUser("West");
        var currentAuth = services.getAuth(services.getUser("West").name());
        services.createGame(currentAuth.authToken(), "BestGame");
        services.clearDataBase();
        assertThrows(DataAccessException.class, () -> services.getGames(currentAuth.authToken()));
    }


    @Test
    void registerUserPositive() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        var expectedUser = new UserData("West", "12345", "West@gmail.com");
        assertEquals(expectedUser, services.getUser("West"));
    }

    @Test
    void registerUserNegative() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        assertThrows(DataAccessException.class, () ->
                services.registerUser("West", "12124", "we@gmail.com"));
    }

    @Test
    void createSession() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        services.logOutUser(services.getAuth("West").authToken()); // logs the user out
        services.createSession("West", "12345");
        assertNotNull(services.getAuth("West")); // creates an auth and certifies that auth returns
    }

    @Test
    void createSessionNegative() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        services.logOutUser(services.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> services.createSession("West", "14232"));
    }

    @Test
    void logOutUserPositive() throws DataAccessException { // pretty sure this is doing what I think its doing.
        services.registerUser("West", "12345", "West@gmail.com");
        services.logOutUser(services.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> services.getAuth("West").authToken());
    }

    @Test
    void logOutUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> services.logOutUser("111111"));
    }

    @Test
    void getGamesPositive() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        var authToken = services.getAuth("West").authToken();
        services.createGame(authToken, "BestGame");
        var currentGames = services.getGames(authToken);
        assertNotNull(services.getGames(authToken));
    }

    @Test
    void getGamesNegative() throws DataAccessException {
        // tries to grab games with a nonexistent auth token.
        assertThrows(DataAccessException.class, () -> services.getGames("111111"));
    }

    @Test
    void createGamePositive() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        var authToken = services.getAuth("West").authToken();
        services.createGame(authToken, "BestGame");
        var newGame =
                new GameData(1, null, null, "BestGame", new ChessGame());
        assertEquals(services.getGame("BestGame"), newGame);
    }

    @Test
    void createGamesNegative() throws DataAccessException { // doesn't let you overwrite a game
        services.registerUser("West", "12345", "West@gmail.com");
        var authToken = services.getAuth("West").authToken();
        services.createGame(authToken, "BestGame");
        // checks to see if that game already exists when we create it
        assertThrows(DataAccessException.class, () -> services.createGame(authToken, "BestGame"));
    }

    @Test
    void joinGamePostitive() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        var authToken = services.getAuth("West").authToken();
        services.createGame(authToken, "BestGame");
        int gameID = services.getGame("BestGame").gameID();
        services.joinGame(authToken, "WHITE", String.valueOf(gameID));
        var expectedGame =
                new GameData(1, "West", null, "BestGame", new ChessGame());
        // checks to make sure that we really have joined the game as white.
        assertEquals(expectedGame, services.getGame("BestGame"));
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        services.registerUser("West", "12345", "West@gmail.com");
        services.registerUser("East", "11111", "East@gmail.com");
        var authToken = services.getAuth("West").authToken();
        var authToken2 = services.getAuth("East").authToken();
        services.createGame(authToken, "BestGame");
        int gameID = services.getGame("BestGame").gameID();
        services.joinGame(authToken, "WHITE", String.valueOf(gameID));
        assertThrows(DataAccessException.class, () ->
                services.joinGame(authToken2, "WHITE", String.valueOf(gameID)));
    }


}

