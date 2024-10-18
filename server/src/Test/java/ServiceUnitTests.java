//import dataaccess.DataAccessException;
//import dataaccess.MemoryDataAccess;
//import model.*;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import server.Server;
//import handler.FrakenHandler;
//
//import model.UserData;
//
//import java.util.Dictionary;
//import java.util.Map;
//
//class ServiceUnitTests {
//
//
//    static private Server server;
//
//    @BeforeAll
//    static void setUp() {
//        var handler = new FrakenHandler(new MemoryDataAccess());
//        server = new Server(handler);
//        server.run(0);
//        // there is more stuff but IDK what it means.
//    }
//
//    @AfterAll
//    static void tearDown() {
//        server.stop();
//    }
//
////    @BeforeEach
////    void clear() {
////        assertDoesNotThrow(() --> server.clearDataBase());
////    }
//
//    @Test
//    void deleteDataBase() throws DataAccessException { // positive test - add something to the data base and make sure it burns correctly.
////        var expected = new Map<>();
//
//    }
//
////
////    public Dictionary getGames(AuthData authToke) {}
////    public Dictionary createGame (AuthData authToken, String gameName) {}
////    public Dictionary joinGame (AuthData authToken, String playerColor, int gameID) {}
////
////    public AuthData checkUser(UserData user) {}
////    void logOutUser(AuthData authToken) {}
////    public AuthData CheckUser(UserData user) {}
////    void LogInUser(UserData newUser) {}
//
//    // we need two test cases for each of the above.
//
//}
