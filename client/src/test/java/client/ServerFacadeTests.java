package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import model.*;
import serverfacade.ServerFacade;


//assertThrows(DataAccessException .class, () -> {
//        USER_SERVICE.createUser("West", "password", "bad@gmail.com");});
//
//assertEquals(USER_SERVICE.getUserCount(), 1);

public class ServerFacadeTests {

    private static Server server;
    private static String serverUrl = "http://localhost:";
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverUrl = "http://localhost:" + String.valueOf(port);
        facade = new ServerFacade(serverUrl); // I think that will do the trick
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDataBase() throws ResponseException { // so thats why we need that lol, and why it doesn't have an auth token.
        facade.resetDataBase();
    }


    @Test
    public void registerPositive() throws Exception {
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        Assertions.assertNotNull(authData);
    }

    @Test
    public void registerNegative() throws Exception {
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        Assertions.assertThrows(ResponseException.class, () -> facade.register(newUser));
    }

    @Test
    public void loginPositive() throws ResponseException {
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        facade.logOut(authData.authToken());
        LoginData currentUser = new LoginData("1", "1");
        facade.login(currentUser, authData.authToken());
        Assertions.assertDoesNotThrow(() -> facade.login(currentUser, authData.authToken()));
    }

    @Test
    public void loginNegative() throws ResponseException {
        LoginData currentUser = new LoginData("1", "1");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(currentUser, null));
    }

    @Test
    public void createGamePositive() throws ResponseException {
        GameCreationData newGame = new GameCreationData("bestGame");
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        facade.createGame(newGame, authData.authToken());
        var gamesList = facade.getGames(authData.authToken());
        Assertions.assertNotNull(gamesList);
    }

    @Test
    public void createGameNegative() throws ResponseException {
        GameCreationData newGame = new GameCreationData("bestGame");
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        facade.createGame(newGame, authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(newGame, authData.authToken()));

    }

    @Test
    public void getGamesPositive() throws ResponseException {
        GameCreationData newGame = new GameCreationData("bestGame");
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        facade.createGame(newGame, authData.authToken());
        GamesList currentGamesList = facade.getGames(authData.authToken());
        Assertions.assertEquals(1, currentGamesList.size());
    }

    @Test
    public void getGamesNegative() {
        Assertions.assertThrows(ResponseException.class, () -> facade.getGames(null));
    }

    @Test
    public void joinGamePositive() throws ResponseException {
        GameCreationData newGame = new GameCreationData("bestGame");
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        facade.createGame(newGame, authData.authToken());
        JoinData newJoin = new JoinData("WHITE", 1); // hardcoded but I'm lazy
        GameData returnedGame = facade.joinGame(newJoin, authData.authToken());
        Assertions.assertNotNull(returnedGame);
    }

    @Test
    public void joinGameNegative() throws ResponseException {
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        JoinData newJoin = new JoinData("WHITE", 1); // hardcoded but I'm lazy
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(newJoin, authData.authToken()));
    }

    @Test
    public void observeGamePositive() throws ResponseException {
        GameCreationData newGame = new GameCreationData("bestGame");
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        facade.createGame(newGame, authData.authToken());
        JoinData newJoin = new JoinData("WHITE", 1); // hardcoded but I'm lazy
        GameData returnedGame = facade.observeGame(newJoin, authData.authToken());
        Assertions.assertNull(returnedGame.whiteUsername());
    }

    @Test
    public void observeGameNegative() throws ResponseException {
        RegisterData newUser = new RegisterData("1", "1", "1");
        var authData = facade.register(newUser);
        JoinData newJoin = new JoinData("WHITE", 3); // hardcoded but I'm lazy
        Assertions.assertThrows(ResponseException.class, () -> facade.observeGame(newJoin, authData.authToken()));
    }
//    @Test
//    void register() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        Assertions.assertTrue(authData.authToken().length() > 10);
//    }

}
