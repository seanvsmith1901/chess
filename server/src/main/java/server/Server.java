package server;


import dataaccess.DataAccessException;
import handler.FrakenHandler;
import spark.*;



public class Server {

    private final FrakenHandler handler = new FrakenHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");



        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();
        Spark.delete("/db", this::clearDataBase);// how the fetch do I call this function??);
        Spark.exception(DataAccessException.class, this::exceptionHandler);
        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private static void createRoutes() {
        ;
   }

   private static void clearDataBase(Request req, Response res)  throws DataAccessException {
       handler.clearDataBase();
   }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        //res.status(ex.StatusCode());
        print("Something went wrong :(");
    }

}
