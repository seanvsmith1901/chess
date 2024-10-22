package server;

// adding this comment so I can figure out what is missing.

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.*;
import model.*;
import spark.*;

import java.util.Objects;


public class Server {

    private final Services services;
    private final Gson serializer = new Gson();


    public Server() {
        this.services = new Services(new MemoryDataAccess());
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.init();

        // set up all of our curl paths
        Spark.delete("/db", this::clearDataBase); // clear application
        Spark.post("/user", this::registerUser); // Register a new user (returns auth token)
        Spark.post("/session", this::createSession); // logs in an existing user
        Spark.delete("/session", this::logOutUser); // logs out a user represented by an auth token
        Spark.get("/game", this::getGames); // gives a list of all the games
        Spark.post("/game", this::createGame); // creates a new game
        Spark.put("/game", this::joinGame); // verifies that game exists, and adds caller as the requested color.


        Spark.awaitInitialization();
        return Spark.port(); // required for TA tests
    }

    public void stop() { // shuts down the server so it doesn't cycle infinitely
        Spark.stop();
        Spark.awaitStop();
    }

   private Object clearDataBase(Request req, Response res) { // testing stuff, should require authentication though lol
        try {
            res.status(200); // everything has gone well
            return serializer.toJson(services.clearDataBase());
        }
        catch (DataAccessException e) { // not really sure how you would end up here
            return serializer.toJson(new ErrorData("Error: you shouldn't ever get here")); // should never come up
        }
   }

   private Object registerUser(Request req, Response res) { // registers a new, never before seen user.

       RegisterData data = new Gson().fromJson(req.body(), RegisterData.class); // parses the data into a record class
       var username = data.username();
       var password = data.password();
       var email = data.email();
       if (username == null || password == null || email == null) { // make sure we have the data we need before we go
           res.status(400);
           return serializer.toJson(new ErrorData("Error: Bad request"));
       }
       try {
           var newAuthenticationObject = services.registerUser(username, password, email);
           res.status(200);
           return serializer.toJson(newAuthenticationObject);
       } catch (DataAccessException e) { // if that username is already taken
           if(Objects.equals(e.getMessage(), "already taken")){
               res.status(403);
               return serializer.toJson(new ErrorData("Error: username already taken"));
           }
           else {
               res.status(500); // catches any other errors
               return serializer.toJson(new ErrorData("Error" + e.getMessage()));
           }
       }
   }

   private Object createSession(Request req, Response res) { // logs in an existing user and returns a new auth token
        LoginData data = new Gson().fromJson(req.body(), LoginData.class);
        var username = data.username();
        var password = data.password();
        try {
            AuthData newAuthenticationObject = services.createSession(username, password);
            var thisSession = new SessionData(newAuthenticationObject.username(), newAuthenticationObject.authToken());
            res.status(200);
            return serializer.toJson(thisSession);
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) { // the user doesn't exist, so they are unauthorized
                res.status(401);
                return serializer.toJson(new ErrorData("Error: unauthorized"));
            }
            if (Objects.equals(e.getMessage(), "differentMessage")) {
                res.status(403);
                var newErrorMessage = new ErrorData("Error: totally different message");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(500); // any other errors go here
                return serializer.toJson(new ErrorData("Error:" + e.getMessage()));
            }
        }
   }

   private Object logOutUser(Request req, Response res) {
        try {
            services.logOutUser(req.headers("authorization")); // make sure we can log them out
            res.status(200);
            return new Gson().toJson(null); // just returns a null object
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) { // trying to log out a nonexistent user
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            if (Objects.equals(e.getMessage(), "randomMessage")) {
                res.status(402);
                var newErrorMessage = new ErrorData("Error: random message");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(500); // any other errors go here
                var newErrorMessage = new ErrorData("Error:" + e.getMessage());
                return serializer.toJson(newErrorMessage);
            }
        }
   }

   private Object getGames(Request req, Response res) { // returns all of the in action games
        try {
            res.status(200);
            var gameTokens = services.getGames(req.headers("authorization")); // gets the game tokens
            GamesList games = new GamesList(gameTokens);
            return new Gson().toJson(games);
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) { // checks for authorization
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(500); // any other errors go here
                var newErrorMessage = new ErrorData("Error:" + e.getMessage());
                return serializer.toJson(newErrorMessage);
            }
        }
   }


    private Object createGame(Request req, Response res) { // creates a new chess game
        try {
            GameCreationData data = new Gson().fromJson(req.body(), GameCreationData.class); // parses data

            var authentication = req.headers("authorization");
            var gameName = data.gameName();

            if(gameName == null) {
                res.status(400);
                return serializer.toJson(new ErrorData("Error: Bad request"));
            }

            res.status(200); // sets a good status
            GameData newGame = services.createGame(authentication, gameName); // creates the game
            GameCreated returnObject = new GameCreated(newGame.gameID());
            return serializer.toJson(returnObject);
        }
        catch(DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) { // not the right authentication token
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(500); // any other errors
                var newErrorMessage = new ErrorData("Error: " + e.getMessage());
                return serializer.toJson(newErrorMessage);
            }
        }
    }

    private Object joinGame(Request req, Response res) {

        var authToken = req.headers("authorization");
        JoinData data = new Gson().fromJson(req.body(), JoinData.class);
        var playerColor = data.playerColor();
        String gameID = String.valueOf(data.gameID());
        ErrorData newErrorMessage;
        if (authToken == null || gameID == null || playerColor == null) { // makes sure everything is there
            res.status(400);
            return serializer.toJson(new ErrorData("Error: bad request"));
        }
        try {
            services.joinGame(authToken, playerColor, gameID);
            res.status(200);
            return serializer.toJson(null);
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) { // bad authentication token
                res.status(401); // 401
                newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage); // i tried breaking this up and it bricked everytime lol
            }
            if (Objects.equals(e.getMessage(), "that color is taken")) { // trying to join an already claimed color
                res.status(403); // 403
                newErrorMessage = new ErrorData("Error: already taken");
                return serializer.toJson(newErrorMessage);
            }
            if (Objects.equals(e.getMessage(), "Game does not exist")) {
                res.status(400);
                newErrorMessage = new ErrorData("Error: Bad request");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(500); // 500
                newErrorMessage = new ErrorData("Error:" + e.getMessage());
                return serializer.toJson(newErrorMessage);
            }
        }
    }
}
