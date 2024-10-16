package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handler.*;
import spark.*;

import java.util.HashMap;
import java.util.Map;


public class Server {

    private FrakenHandler handler;
    private Gson Serializer = new Gson();


    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");



        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        //Im an idiot, we will be passing a full authToken object in these functions when we delete and whatnot.

        Spark.delete("/db", this::clearDataBase);// how the fetch do I call this function??);
        Spark.post("/user:username:password:email", this::registerUser);
        Spark.post("/session:username:password", this::createSession);
        Spark.delete("/session:authToken", this::logOutUser);
        Spark.get("/game:authToken", this::getGames);
        Spark.post("/game:authToken:gameName", this::createGame);
        Spark.put("/game:authToken:playerColor:gameID", this::joinGame);


        Spark.exception(DataAccessException.class, this::exceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

   private Object clearDataBase(Request req, Response res)  throws DataAccessException {
        res.status(200);
        return handler.clearDataBase();

   }

   private Object registerUser(Request req, Response res)  throws DataAccessException {
       // first try to find the user, make sure thats null, create the user adn then create an auth token with that user, and return that authtoken.
        var newAuthenticationObject = handler.registerUser(req.attribute("username"), req.attribute("password"), req.attribute("email"));
        res.status(200);
        return new Gson().toJson(newAuthenticationObject);
   }

   private Object createSession(Request req, Response res)  throws DataAccessException {
        var newAuthenticationObject = handler.createSession(req.attribute("username"), req.attribute("password"));
        res.status(200);
        return new Gson().toJson(newAuthenticationObject);
   }

   private Object logOutUser(Request req, Response res)  throws DataAccessException {
        handler.logOutUser(req.attribute("authToken"));
        res.status(200);
        Map<String, Integer> myDict = new HashMap<>();
        return new Gson().toJson(myDict);
   }

   private Object getGames(Request req, Response res)  throws DataAccessException {
        return handler.getGames(req.attribute("authToken"));
   }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        //res.status(ex.StatusCode());
        System.out.println("Something went wrong :(");
    }

    private Object createGame(Request req, Response res)  throws DataAccessException {
        // need to return the gamename and the gameID. lez go
        res.status(200);
        return handler.createGame(req.attribute("authToken"), req.attribute("gameName"));

    }

    private Object joinGame(Request req, Response res)  throws DataAccessException {
        res.status(200);
        return handler.joinGame(req.attribute("authToken"), req.attribute("playerColor"), req.attribute("gameID"));
    }



}
