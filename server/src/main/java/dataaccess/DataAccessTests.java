package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DataAccessTests { // note to self: these are actually dataaccess tests, to make sure that I can access the data alright. my structure is wonky.

    static final DataAccess dataAccess = new MemoryDataAccess();
    static final AuthService authService = new AuthService(dataAccess);
    static final GameService gameService = new GameService(dataAccess);
    static final UserService userService = new UserService(dataAccess);


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
        assertEquals((authService.deleteEverything()), null);

    }

    // ** Start with auth tests

    @Test
    void createAuthToken() throws DataAccessException { // makes sure we add an authtoken
        var expectedAuthLength = 1;
        authService.createAuthToken("West");
        assertEquals(authService.getAuthSize(), expectedAuthLength);
    }

    @Test
    void createAuthTokenNegative() throws DataAccessException { // if the username is null we brick
        assertThrows(DataAccessException.class, () -> authService.createAuthToken(null));
    }

    @Test
    void getExistingAuthToken() throws DataAccessException { // finds the auth token
        authService.createAuthToken("West");
        var expectedUserName = "West";
        assertEquals((authService.getAuthObjectFromUserName(expectedUserName)).username(), expectedUserName);
    }

    @Test
    void grabNonexistentAuthToken() { // tries to grab a bad auth token
        assertThrows(DataAccessException.class, () -> {authService.getAuthObject("West");});
    }

    @Test
    void grabNonExistentUserName() { // trues to grab a user that doesn't exist
        assertThrows(DataAccessException.class, () -> {authService.getAuthObjectFromUserName("West");});
    }

    @Test
    void deleteExistingAuthToken() throws DataAccessException { // deletes a real authtoken
        var thisShouldBeRight = 0;
        authService.createAuthToken("West");
        AuthData currentAuth = authService.getAuthObjectFromUserName("West"); // IDK if that will work lol
        authService.deleteAuthObject(currentAuth);
        assertEquals(0, authService.getAuthSize());

    }

    @Test
    void deleteNonExistentAuthToken() throws DataAccessException {
        AuthData fakeAuthentication = new AuthData("111111", "West");
        assertThrows(DataAccessException.class, () -> {authService.deleteAuthObject(fakeAuthentication);});
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

    // ** End of Auth Tests ** ** Start of games tests **


    @Test
    void getGamesPositive() throws DataAccessException {
        var newGame = new GameData(1, null, null, "bestGame", new ChessGame());
        gameService.createGame("bestGame");
        assertEquals((gameService.getGames()).size(), 1);
    }

    @Test
    void getGameNamePositive() throws DataAccessException {
        authService.deleteEverything();
        var size = gameService.getGames().size();
        var newGame = new GameData(1, null, null, "goodGame", new ChessGame()); // gonna be honest no clue as to why thats happening
        gameService.createGame("goodGame"); // had to overwrite teh freaking chessgames equal operator for this one lol
        assertEquals(gameService.getGame("goodGame"), newGame);
    }

    @Test
    void getGameNameNegative() throws DataAccessException {
        authService.deleteEverything();
        assertThrows(DataAccessException.class, () -> {gameService.getGame("deez");});
    }

    @Test
    void getGameFromIDPositive() throws DataAccessException {
        var newGame = new GameData(1, null, null, "bestGame", new ChessGame());
        gameService.createGame("bestGame");
        assertEquals(gameService.getGameFromID("1"), newGame);
    }

    @Test
    void getGameFromIDNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {gameService.getGameFromID("bestGame");});
    }

    @Test
    void createGamePositive() throws DataAccessException {
        var newGame = new GameData(1, null, null, "bestGame", new ChessGame());
        gameService.createGame("bestGame");
        assertEquals((gameService.getGames()).size(), 1);
    }

    @Test
    void createGameNegative() throws DataAccessException {
        var newGame = new GameData(1, null, null, "goodGame", new ChessGame());
        gameService.createGame("goodGame");
        var anotherGame =  new GameData(2, null, null, "goodGame", new ChessGame());
        assertThrows(DataAccessException.class, () -> {gameService.createGame("goodGame");});
    }

    // ** END OF GAME TESTS ** ** Start of user Tests **

    @Test
    void createUserPositive() throws DataAccessException {
        userService.createUser("West", "password", "west@gmail.com");
        assertEquals(userService.getUserCount(), 1);
    }

    @Test
    void createUserNegative() throws DataAccessException {
        userService.createUser("West", "password", "west@gmail.com");
        assertThrows(DataAccessException.class, () -> {userService.createUser("West", "password", "bad@gmail.com");}); // should just to see if they have the same username
    }

    @Test
    void getUserPositive() throws DataAccessException {
        var newUser = new UserData("West", "password", "west@gmail.com");
        userService.createUser("West", "password", "west@gmail.com");
        assertEquals(userService.getUser("West"), newUser);
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {userService.getUser("West");});
    }

    @Test
    void replaceUserInGamePositive() throws DataAccessException {
        var expectedGame = new GameData(1, "West", null, "BestGame", new ChessGame());
        gameService.createGame("BestGame");
        var newGame = gameService.getGame("BestGame");
        userService.createUser("West", "password", "west@gmail.com");
        var currentUser = userService.getUser("West");
        userService.replaceUserInGame(newGame, currentUser.name(), "WHITE");
        assertEquals(gameService.getGame("BestGame"), expectedGame);
    }

    @Test
    void replaceUserInGameNegativeColorTaken() throws DataAccessException {
        gameService.createGame("BestGame");
        var newGame = gameService.getGame("BestGame");
        userService.createUser("West", "password", "west@gmail.com");
        var currentUser = userService.getUser("West");
        userService.replaceUserInGame(newGame, currentUser.name(), "WHITE");
        assertThrows(DataAccessException.class, () -> {userService.replaceUserInGame(gameService.getGame("BestGame"), currentUser.name(), "WHITE");});
    }

    @Test
    void getUserCountPositive() throws DataAccessException {
        userService.createUser("West", "password", "west@gmail.com");
        assertEquals(userService.getUserCount(), 1);
    }

    // ** end of user tests **



}