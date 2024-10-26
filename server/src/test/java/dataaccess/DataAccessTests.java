package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mindrot.jbcrypt.BCrypt;

public class DataAccessTests { // these have been renamed appropraitely.

    static final DataAccess DATA_ACCESS = new MySqlDataAccess();
    static final AuthService AUTH_SERVICE = new AuthService(DATA_ACCESS);
    static final GameService GAME_SERVICE = new GameService(DATA_ACCESS);
    static final UserService USER_SERVICE = new UserService(DATA_ACCESS);


    @BeforeAll
    static void clear() throws DataAccessException {
        AUTH_SERVICE.deleteEverything();
    }

    @BeforeEach
    void tearDown() throws DataAccessException {
        AUTH_SERVICE.deleteEverything();
    }

    @Test
    void deleteDataBase() throws DataAccessException { // positive test - makes sure it returns nothing
        assertEquals((AUTH_SERVICE.deleteEverything()), null);

    }

    // ** Start with auth tests

    @Test
    void createAuthToken() throws DataAccessException { // makes sure we add an authtoken
        var expectedAuthLength = 1;
        AUTH_SERVICE.createAuthToken("West");
        assertEquals(AUTH_SERVICE.getAuthSize(), expectedAuthLength);
    }

    @Test
    void createAuthTokenNegative() throws DataAccessException { // if the username is null we brick
        assertThrows(DataAccessException.class, () -> AUTH_SERVICE.createAuthToken(null));
    }
    //
    @Test
    void getExistingAuthToken() throws DataAccessException { // finds the auth token
        AUTH_SERVICE.createAuthToken("West");
        var expectedUserName = "West";
        assertEquals((AUTH_SERVICE.getAuthObjectFromUserName(expectedUserName)).username(), expectedUserName);
    }
    //
    @Test
    void grabNonexistentAuthToken() { // tries to grab a bad auth token
        assertThrows(DataAccessException.class, () -> {
            AUTH_SERVICE.getAuthObject("West");});
    }
    //
    @Test
    void grabNonExistentUserName() { // trues to grab a user that doesn't exist
        assertThrows(DataAccessException.class, () -> {
            AUTH_SERVICE.getAuthObjectFromUserName("West");});
    }
    //
    @Test
    void deleteExistingAuthToken() throws DataAccessException { // deletes a real authtoken
        var thisShouldBeRight = 0;
        AUTH_SERVICE.createAuthToken("West");
        AuthData currentAuth = AUTH_SERVICE.getAuthObjectFromUserName("West"); // IDK if that will work lol
        AUTH_SERVICE.deleteAuthObject(currentAuth);
        assertEquals(0, AUTH_SERVICE.getAuthSize());

    }
    //
    @Test
    void deleteNonExistentAuthToken() throws DataAccessException {
        AuthData fakeAuthentication = new AuthData("111111", "West");
        assertThrows(DataAccessException.class, () -> {
            AUTH_SERVICE.deleteAuthObject(fakeAuthentication);});
    }

    @Test
    void deleteExistingAuthTokenGivenUsername() throws DataAccessException {
        AUTH_SERVICE.createAuthToken("West");
        var authToken = AUTH_SERVICE.getAuthObjectFromUserName("West").authToken();
        AuthData currentAuth = AUTH_SERVICE.getAuthObject(authToken);
        AUTH_SERVICE.deleteAuthObject(currentAuth);
        assertEquals(0, AUTH_SERVICE.getAuthSize());
    }

    //
    @Test
    void checkDeleteAuthObject() throws DataAccessException {
        AUTH_SERVICE.createAuthToken("West");
        AUTH_SERVICE.createAuthToken("East");
        var currentAuth = AUTH_SERVICE.getAuthObjectFromUserName("West");
        AUTH_SERVICE.deleteAuthObject(currentAuth);
        assertEquals(1, AUTH_SERVICE.getAuthSize());
    }
    //
    @Test
    void checkDeleteAuthObjectNotThere() throws DataAccessException {
        var newAuth = new AuthData("asdsfd", "West");
        assertThrows(DataAccessException.class, () -> {
            AUTH_SERVICE.deleteAuthObject(newAuth);});
    }

    @Test
    void checkAuthSize() throws DataAccessException {
        var newAuth = new AuthData("asdsfd", "West");
        AUTH_SERVICE.createAuthToken("West");
        assertEquals(1, AUTH_SERVICE.getAuthSize());
    }
    //
//    // ** End of Auth Tests ** ** Start of games tests **
//
//
    @Test
    void getGamesPositive() throws DataAccessException {
        GAME_SERVICE.createGame("bestGame");
        assertEquals((GAME_SERVICE.getGames()).size(), 1);
    }

    @Test
    void getGameNamePositive() throws DataAccessException {
        AUTH_SERVICE.deleteEverything();
        var newGame =
                new GameData(1, null, null, "goodGame", new ChessGame());
        GAME_SERVICE.createGame("goodGame"); // had to overwrite the chessgames equal operator for this one lol
        assertEquals(GAME_SERVICE.getGame("goodGame"), newGame);
    }

    @Test
    void getGameNameNegative() throws DataAccessException {
        AUTH_SERVICE.deleteEverything();
        assertThrows(DataAccessException.class, () -> {
            GAME_SERVICE.getGame("deez");});
    }

    @Test
    void getGameFromIDPositive() throws DataAccessException {
        var newGame =
                new GameData(1, null, null, "bestGame", new ChessGame());
        GAME_SERVICE.createGame("bestGame");
        assertEquals(GAME_SERVICE.getGameFromID("1"), newGame);
    }

    @Test
    void getGameFromIDNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            GAME_SERVICE.getGameFromID("bestGame");});
    }

    @Test
    void createGamePositive() throws DataAccessException {
        GAME_SERVICE.createGame("bestGame");
        assertEquals((GAME_SERVICE.getGames()).size(), 1);
    }

    @Test
    void createGameNegative() throws DataAccessException {
        var newGame =
                new GameData(1, null, null, "goodGame", new ChessGame());
        GAME_SERVICE.createGame("goodGame");
        var anotherGame =
                new GameData(2, null, null, "goodGame", new ChessGame());
        assertThrows(DataAccessException.class, () -> {
            GAME_SERVICE.createGame("goodGame");});
    }
    //
//    // ** END OF GAME TESTS ** ** Start of user Tests **
//
    @Test
    void createUserPositive() throws DataAccessException {
        USER_SERVICE.createUser("West", "password", "west@gmail.com");
        assertEquals(USER_SERVICE.getUserCount(), 1);
    }

    @Test
    void createUserNegative() throws DataAccessException {
        USER_SERVICE.createUser("West", "password", "west@gmail.com");
        assertThrows(DataAccessException.class, () -> {
            USER_SERVICE.createUser("West", "password", "bad@gmail.com");});
    }

    @Test
    void getUserPositive() throws DataAccessException {

        var newUser = new UserData("West", "password", "west@gmail.com"); // password gets so no go
        USER_SERVICE.createUser("West", "password", "west@gmail.com");
        assertEquals(USER_SERVICE.getUser("West").email(), newUser.email());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            USER_SERVICE.getUser("West");});
    }

    @Test
    void replaceUserInGamePositive() throws DataAccessException {
        var expectedGame =
                new GameData(1, "West", null, "BestGame", new ChessGame());
        GAME_SERVICE.createGame("BestGame");
        var newGame = GAME_SERVICE.getGame("BestGame");
        USER_SERVICE.createUser("West", "password", "west@gmail.com");
        var currentUser = USER_SERVICE.getUser("West");
        USER_SERVICE.replaceUserInGame(newGame, currentUser.name(), "WHITE");
        assertEquals(GAME_SERVICE.getGame("BestGame"), expectedGame);
    }

    @Test
    void replaceUserInGameNegativeColorTaken() throws DataAccessException {
        GAME_SERVICE.createGame("BestGame");
        var newGame = GAME_SERVICE.getGame("BestGame");
        USER_SERVICE.createUser("West", "password", "west@gmail.com");
        var currentUser = USER_SERVICE.getUser("West");
        USER_SERVICE.replaceUserInGame(newGame, currentUser.name(), "WHITE");
        var currName = currentUser.name();
        assertThrows(DataAccessException.class, () -> {
            USER_SERVICE.replaceUserInGame(GAME_SERVICE.getGame("BestGame"), currName, "WHITE");});
    }

    @Test
    void getUserCountPositive() throws DataAccessException {
        USER_SERVICE.createUser("West", "password", "west@gmail.com");
        assertEquals(USER_SERVICE.getUserCount(), 1);
    }

    // ** end of user tests **



}
