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

    @Test
    void getGamesNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> handler.getGames("111111")); // tries to grab games with a nonexistent auth token.
    }

    @Test
    void createGamePositive() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        var authToken = handler.getAuth("West").authToken();
        handler.createGame(authToken, "BestGame");
        var newGame = new GameData(1, null, null, "BestGame", new ChessGame());
        assertEquals(handler.getGame("BestGame"), newGame);
    }

    @Test
    void createGamesNegative() throws DataAccessException { // doesn't let you overwrite a game
        handler.registerUser("West", "12345", "West@gmail.com");
        var authToken = handler.getAuth("West").authToken();
        handler.createGame(authToken, "BestGame");
        assertThrows(DataAccessException.class, () -> handler.createGame(authToken, "BestGame")); // checks to see if that game already exists when we create it

    }

    @Test
    void joinGamePostitive() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        var authToken = handler.getAuth("West").authToken();
        handler.createGame(authToken, "BestGame");
        int gameID = handler.getGame("BestGame").gameID();
        handler.joinGame(authToken, "WHITE", String.valueOf(gameID));
        var expectedGame = new GameData(1, "West", null, "BestGame", new ChessGame());
        assertEquals(expectedGame, handler.getGame("BestGame")); // checks to make sure that we really have joined the game as white.
    }

    @Test
    void joinGameNegative() throws DataAccessException {
        handler.registerUser("West", "12345", "West@gmail.com");
        handler.registerUser("East", "11111", "East@gmail.com");
        var authToken = handler.getAuth("West").authToken();
        var authToken2 = handler.getAuth("East").authToken();
        handler.createGame(authToken, "BestGame");
        int gameID = handler.getGame("BestGame").gameID();
        handler.joinGame(authToken, "WHITE", String.valueOf(gameID));
        assertThrows(DataAccessException.class, () -> handler.joinGame(authToken2, "WHITE", String.valueOf(gameID)));
    }


}

