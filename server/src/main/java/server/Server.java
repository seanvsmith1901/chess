package server;


import dataaccess.DataAccessException;
import handler.FrakenHandler;
import spark.*;



public class Server {

    private FrakenHandler handler;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");



        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();
        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private static void createRoutes() {
        Spark.delete("/db", clearDataBase());
   }

   private static void clearDataBase(Request req, Response res)  throws DataAccessException {
        var response = handler.
   }

}
