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
    static final DataAccess DataAccess = new MemoryDataAccess();
    static final Services Services = new Services(DataAccess);


    @BeforeAll
    static void clear() throws DataAccessException {
        Services.clearDataBase();
    }

    @BeforeEach
    void tearDown() throws DataAccessException {
        Services.clearDataBase();
    }

    @Test
    void checkClearDataBasePositive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        var currentUser = Services.getUser("West");
        var currentAuth = Services.getAuth(Services.getUser("West").name());
        Services.createGame(currentAuth.authToken(), "BestGame");
        Services.clearDataBase();
        assertThrows(DataAccessException.class, () -> Services.getGames(currentAuth.authToken()));
    }


    @Test
    void registerUserPositive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        var expectedUser = new UserData("West", "12345", "West@gmail.com");
        assertEquals(expectedUser, Services.getUser("West"));
    }

    @Test
    void registerUserNegative() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        assertThrows(DataAccessException.class, () -> Services.registerUser("West", "12124", "we@gmail.com")); // only checks for same username
    }

    @Test
    void createSession() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        Services.logOutUser(Services.getAuth("West").authToken()); // logs the user out
        Services.createSession("West", "12345");
        assertNotNull(Services.getAuth("West")); // creates an auth and certifies that we do return an auth for that user.
    }

    @Test
    void createSessionNegative() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        Services.logOutUser(Services.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> Services.createSession("West", "14232")); // wrong password
    }

    @Test
    void logOutUserPositive() throws DataAccessException { // pretty sure this is doing what I think its doing.
        Services.registerUser("West", "12345", "West@gmail.com");
        Services.logOutUser(Services.getAuth("West").authToken());
        assertThrows(DataAccessException.class, () -> Services.getAuth("West").authToken()); // if it works, there will no longer be an auth token for this user.
    }

    @Test
    void logOutUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> Services.logOutUser("111111")); // auth token doesn't exist, make sure it tells us that
    }

    @Test
    void getGamesPositive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        var authToken = Services.getAuth("West").authToken();
        Services.createGame(authToken, "BestGame");
        var currentGames = Services.getGames(authToken);
        assertNotNull(Services.getGames(authToken));
    }

    @Test
    void getGamesNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> Services.getGames("111111")); // tries to grab games with a nonexistent auth token.
    }

    @Test
    void createGamePositive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        var authToken = Services.getAuth("West").authToken();
        Services.createGame(authToken, "BestGame");
        var newGame = new GameData(1, null, null, "BestGame", new ChessGame());
        assertEquals(Services.getGame("BestGame"), newGame);
    }

    @Test
    void createGamesNegative() throws DataAccessException { // doesn't let you overwrite a game
        Services.registerUser("West", "12345", "West@gmail.com");
        var authToken = Services.getAuth("West").authToken();
        Services.createGame(authToken, "BestGame");
        assertThrows(DataAccessException.class, () -> Services.createGame(authToken, "BestGame")); // checks to see if that game already exists when we create it
    }

    @Test
    void joinGamePostitive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        var authToken = Services.getAuth("West").authToken();
        Services.createGame(authToken, "BestGame");
        int gameID = Services.getGame("BestGame").gameID();
        Services.joinGame(authToken, "WHITE", String.valueOf(gameID));
        var expectedGame = new GameData(1, "West", null, "BestGame", new ChessGame());
        assertEquals(expectedGame, Services.getGame("BestGame")); // checks to make sure that we really have joined the game as white.
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        Services.registerUser("East", "11111", "East@gmail.com");
        var authToken = Services.getAuth("West").authToken();
        var authToken2 = Services.getAuth("East").authToken();
        Services.createGame(authToken, "BestGame");
        int gameID = Services.getGame("BestGame").gameID();
        Services.joinGame(authToken, "WHITE", String.valueOf(gameID));
        assertThrows(DataAccessException.class, () -> Services.joinGame(authToken2, "WHITE", String.valueOf(gameID)));
    }

    //getUser
    @Test
    void getUserPositive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        UserData newUser = new UserData("West", "12345", "West@gmail.com");
        assertEquals(Services.getUser("West"), newUser);
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> Services.getUser("West"));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        Services.registerUser("West", "12345", "West@gmail.com");
        assertNotNull(Services.getAuth("West").authToken());
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> Services.getAuth("West").authToken());
    }

    @Test
    void getGamePositive() throws DataAccessException {
        GameData newGame = new GameData(1, null, null, "BestGame", new ChessGame());
        Services.createGame("11111", "BestGame");
        assertEquals(Services.getGame("BestGame"), newGame);
    }

    @Test
    void getGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> Services.getGame("11111"));
    }

}

