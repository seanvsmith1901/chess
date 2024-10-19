package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handler.*;
import model.*;
import spark.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class Server {

    private FrakenHandler handler;
    private Gson serializer = new Gson();


    public Server() {
        this.handler = new FrakenHandler(new MemoryDataAccess());
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");



        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        //Im an idiot, we will be passing a full authToken object in these functions when we delete and whatnot.

        // ok so check for stuff in the head vs. body, and understand how to access those elements. our handler is our service and our services are glorified data accessers. sweet.

        Spark.delete("/db", this::clearDataBase); // clear application
        Spark.post("/user", this::registerUser); // Register a new user (returns auth token)
        Spark.post("/session", this::createSession); // logs in an existing user
        Spark.delete("/session", this::logOutUser); // logs out a user represented by an auth token
        Spark.get("/game", this::getGames); // gives a list of all the games
        Spark.post("/game", this::createGame); // creates a new game
        Spark.put("/game", this::joinGame); // verifies that game exists, and adds caller as the requested color.


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
        return serializer.toJson(handler.clearDataBase());

   }

   private Object registerUser(Request req, Response res)  throws DataAccessException {
       // first try to find the user, make sure thats null, create the user adn then create an auth token with that user, and return that authtoken.

       RegisterData data = new Gson().fromJson(req.body(), RegisterData.class);
       var username = data.username();
       var password = data.password();
       Object newAuthenticationObject;
       var email = data.email();
       newAuthenticationObject = handler.registerUser(username, password, email);
       res.status(200);
       return serializer.toJson(newAuthenticationObject);


   }

   private Object createSession(Request req, Response res)  throws DataAccessException {
        LoginData data = new Gson().fromJson(req.body(), LoginData.class);
        var username = data.username();
        var password = data.password();
        try {
            var newAuthenticationObject = handler.createSession(username, password);
            SessionData thisSession = new SessionData(newAuthenticationObject.username(), newAuthenticationObject.authToken());
            res.status(200);
            return serializer.toJson(thisSession);
        }
        catch (DataAccessException e) {
            ;
        }



   }

   private Object logOutUser(Request req, Response res)  throws DataAccessException {
        handler.logOutUser(req.headers("authorization"));
        res.status(200);
        Map<String, Integer> myDict = new HashMap<>();
        return new Gson().toJson(myDict); // no clue if that works
   }

   private Object getGames(Request req, Response res)  throws DataAccessException {
        var gameTokens = handler.getGames(req.headers("authorization"));
        GamesList games = new GamesList(gameTokens);
        return new Gson().toJson(games);
   }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        //res.status(ex.StatusCode());
        System.out.println("Something went wrong :(");
    }

    private Object createGame(Request req, Response res)  throws DataAccessException {
        // need to return the gamename and the gameID. lez go
        GameCreationData data = new Gson().fromJson(req.body(), GameCreationData.class);

        var authentication = req.headers("authorization");
        var gameName = data.gameName();

        res.status(200);
        GameData newGame = (handler.createGame(authentication, gameName));
        GameCreated returnObject = new GameCreated(newGame.gameID());
        return serializer.toJson(returnObject);

    }

    private Object joinGame(Request req, Response res)  throws DataAccessException {

        var authToken = req.headers("authorization");
        JoinData data = new Gson().fromJson(req.body(), JoinData.class);
        var playerColor = data.playerColor();
        String gameID = String.valueOf(data.gameID());
        handler.joinGame(authToken, playerColor, gameID);
        res.status(200);
        return serializer.toJson(null);
    }



}
