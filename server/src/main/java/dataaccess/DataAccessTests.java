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


public class DataAccessTests { // note to self: these are actually dataaccess tests, to make sure that I can access the data alright. my structure is wonky.

    static final DataAccess dataAccess = new MemoryDataAccess();
    static final AuthService AuthService = new AuthService(dataAccess);
    static final GameService GameService = new GameService(dataAccess);
    static final UserService UserService = new UserService(dataAccess);


    @BeforeAll
    static void clear() throws DataAccessException {
        AuthService.deleteEverything();
    }

    @BeforeEach
    void tearDown() throws DataAccessException {
        AuthService.deleteEverything();
    }

    @Test
    void deleteDataBase() throws DataAccessException { // positive test - add something to the data base and make sure it burns correctly.
        assertEquals((AuthService.deleteEverything()), null);

    }

    // ** Start with auth tests

    @Test
    void createAuthToken() throws DataAccessException { // makes sure we add an authtoken
        var expectedAuthLength = 1;
        AuthService.createAuthToken("West");
        assertEquals(AuthService.getAuthSize(), expectedAuthLength);
    }

    @Test
    void createAuthTokenNegative() throws DataAccessException { // if the username is null we brick
        assertThrows(DataAccessException.class, () -> AuthService.createAuthToken(null));
    }

    @Test
    void getExistingAuthToken() throws DataAccessException { // finds the auth token
        AuthService.createAuthToken("West");
        var expectedUserName = "West";
        assertEquals((AuthService.getAuthObjectFromUserName(expectedUserName)).username(), expectedUserName);
    }

    @Test
    void grabNonexistentAuthToken() { // tries to grab a bad auth token
        assertThrows(DataAccessException.class, () -> {AuthService.getAuthObject("West");});
    }

    @Test
    void grabNonExistentUserName() { // trues to grab a user that doesn't exist
        assertThrows(DataAccessException.class, () -> {AuthService.getAuthObjectFromUserName("West");});
    }

    @Test
    void deleteExistingAuthToken() throws DataAccessException { // deletes a real authtoken
        var thisShouldBeRight = 0;
        AuthService.createAuthToken("West");
        AuthData currentAuth = AuthService.getAuthObjectFromUserName("West"); // IDK if that will work lol
        AuthService.deleteAuthObject(currentAuth);
        assertEquals(0, AuthService.getAuthSize());

    }

    @Test
    void deleteNonExistentAuthToken() throws DataAccessException {
        AuthData fakeAuthentication = new AuthData("111111", "West");
        assertThrows(DataAccessException.class, () -> {AuthService.deleteAuthObject(fakeAuthentication);});
    }

    @Test
    void deleteExistingAuthTokenGivenUsername() throws DataAccessException {
        AuthService.createAuthToken("West");
        var authToken = AuthService.getAuthObjectFromUserName("West").authToken();
        AuthData currentAuth = AuthService.getAuthObject(authToken);
        AuthService.deleteAuthObject(currentAuth);
        assertEquals(0, AuthService.getAuthSize());
    }


    @Test
    void checkDeleteAuthObject() throws DataAccessException {
        AuthService.createAuthToken("West");
        AuthService.createAuthToken("East");
        var currentAuth = AuthService.getAuthObjectFromUserName("West");
        AuthService.deleteAuthObject(currentAuth);
        assertEquals(1, AuthService.getAuthSize());
    }

    @Test
    void checkDeleteAuthObjectNotThere() throws DataAccessException {
        var newAuth = new AuthData("asdsfd", "West");
        assertThrows(DataAccessException.class, () -> {AuthService.deleteAuthObject(newAuth);});
    }

    @Test
    void checkAuthSize() throws DataAccessException {
        var newAuth = new AuthData("asdsfd", "West");
        AuthService.createAuthToken("West");
        assertEquals(1, AuthService.getAuthSize());
    }

    // ** End of Auth Tests ** ** Start of games tests **


    @Test
    void getGamesPositive() throws DataAccessException {
        var newGame = new GameData(1, null, null, "bestGame", new ChessGame());
        GameService.createGame("bestGame");
        assertEquals((GameService.getGames()).size(), 1);
    }

    @Test
    void getGameNamePositive() throws DataAccessException {
        AuthService.deleteEverything();
        var size = GameService.getGames().size();
        var newGame = new GameData(1, null, null, "goodGame", new ChessGame()); // gonna be honest no clue as to why thats happening
        GameService.createGame("goodGame"); // had to overwrite teh freaking chessgames equal operator for this one lol
        assertEquals(GameService.getGame("goodGame"), newGame);
    }

    @Test
    void getGameNameNegative() throws DataAccessException {
        AuthService.deleteEverything();
        assertThrows(DataAccessException.class, () -> {
            GameService.getGame("deez");});
    }

    @Test
    void getGameFromIDPositive() throws DataAccessException {
        var newGame = new GameData(1, null, null, "bestGame", new ChessGame());
        GameService.createGame("bestGame");
        assertEquals(GameService.getGameFromID("1"), newGame);
    }

    @Test
    void getGameFromIDNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            GameService.getGameFromID("bestGame");});
    }

    @Test
    void createGamePositive() throws DataAccessException {
        var newGame = new GameData(1, null, null, "bestGame", new ChessGame());
        GameService.createGame("bestGame");
        assertEquals((GameService.getGames()).size(), 1);
    }

    @Test
    void createGameNegative() throws DataAccessException {
        var newGame = new GameData(1, null, null, "goodGame", new ChessGame());
        GameService.createGame("goodGame");
        var anotherGame =  new GameData(2, null, null, "goodGame", new ChessGame());
        assertThrows(DataAccessException.class, () -> {
            GameService.createGame("goodGame");});
    }

    // ** END OF GAME TESTS ** ** Start of user Tests **

    @Test
    void createUserPositive() throws DataAccessException {
        UserService.createUser("West", "password", "west@gmail.com");
        assertEquals(UserService.getUserCount(), 1);
    }

    @Test
    void createUserNegative() throws DataAccessException {
        UserService.createUser("West", "password", "west@gmail.com");
        assertThrows(DataAccessException.class, () -> {
            UserService.createUser("West", "password", "bad@gmail.com");}); // should just to see if they have the same username
    }

    @Test
    void getUserPositive() throws DataAccessException {
        var newUser = new UserData("West", "password", "west@gmail.com");
        UserService.createUser("West", "password", "west@gmail.com");
        assertEquals(UserService.getUser("West"), newUser);
    }

    @Test
    void getUserNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            UserService.getUser("West");});
    }

    @Test
    void replaceUserInGamePositive() throws DataAccessException {
        var expectedGame = new GameData(1, "West", null, "BestGame", new ChessGame());
        GameService.createGame("BestGame");
        var newGame = GameService.getGame("BestGame");
        UserService.createUser("West", "password", "west@gmail.com");
        var currentUser = UserService.getUser("West");
        UserService.replaceUserInGame(newGame, currentUser.name(), "WHITE");
        assertEquals(GameService.getGame("BestGame"), expectedGame);
    }

    @Test
    void replaceUserInGameNegativeColorTaken() throws DataAccessException {
        GameService.createGame("BestGame");
        var newGame = GameService.getGame("BestGame");
        UserService.createUser("West", "password", "west@gmail.com");
        var currentUser = UserService.getUser("West");
        UserService.replaceUserInGame(newGame, currentUser.name(), "WHITE");
        assertThrows(DataAccessException.class, () -> {
            UserService.replaceUserInGame(GameService.getGame("BestGame"), currentUser.name(), "WHITE");});
    }

    @Test
    void getUserCountPositive() throws DataAccessException {
        UserService.createUser("West", "password", "west@gmail.com");
        assertEquals(UserService.getUserCount(), 1);
    }

    // ** end of user tests **



}
