package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handler.*;
import spark.*;



public class Server {

    private FrakenHandler handler;

    public Server(FrakenHandler handler) {
        this.handler = handler;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");



        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.delete("/db", this::clearDataBase);// how the fetch do I call this function??);
        Spark.post("/user:username:password:email", this::createUser);


        Spark.exception(DataAccessException.class, this::exceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

   private Object clearDataBase(Request req, Response res)  throws DataAccessException {
       return handler.clearDataBase();
   }

   private Object createUser(Request req, Response res)  throws DataAccessException {
       // first try to find the user, make sure thats null, create the user adn then create an auth token with that user, and return that authtoken. 
        handler.createUser(req.attribute("username"), req.attribute("password"), req.attribute("email"));
   }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        //res.status(ex.StatusCode());
        System.out.println("Something went wrong :(");
    }

}
