package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.ResponseException;
import handler.*;
import model.*;
import spark.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;


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
        try {
            res.status(200);
            return serializer.toJson(handler.clearDataBase());
        }
        catch (DataAccessException e) {
            return serializer.toJson(new ErrorData("Error: you shouldn't ever get here")); // no idea how that could every come up
        }

   }

   private Object registerUser(Request req, Response res)  throws DataAccessException {
       // first try to find the user, make sure thats null, create the user adn then create an auth token with that user, and return that authtoken.

       RegisterData data = new Gson().fromJson(req.body(), RegisterData.class);
       var username = data.username();
       var password = data.password();
       Object newAuthenticationObject;
       var email = data.email();
       if (username == null || password == null || email == null) {
           res.status(400);
           return serializer.toJson(new ErrorData("Error: Bad request"));
       }
       try {
           newAuthenticationObject = handler.registerUser(username, password, email);
           res.status(200);
           return serializer.toJson(newAuthenticationObject);
       } catch (DataAccessException e) {
           if(Objects.equals(e.getMessage(), "already taken")){
               res.status(403);
               return serializer.toJson(new ErrorData("Error: username already taken"));
           }
           else {
               res.status(403);
               return serializer.toJson(new ErrorData("Error" + e.getMessage()));
           }
       }




   }

   private Object createSession(Request req, Response res)  throws DataAccessException {
        LoginData data = new Gson().fromJson(req.body(), LoginData.class);
        var username = data.username();
        var password = data.password();
        SessionData thisSession = new SessionData(null, null);
        try {
            AuthData newAuthenticationObject = handler.createSession(username, password);
            thisSession = new SessionData(newAuthenticationObject.username(), newAuthenticationObject.authToken());
            res.status(200);
            return serializer.toJson(thisSession);
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(500);
                return serializer.toJson(new ErrorData("Error:" + e.getMessage()));
            }
            else {
                res.status(401);
                thisSession = new SessionData(null, null);
                return serializer.toJson(new ErrorData("Error: unauthorized"));
            }
        }
   }

   private Object logOutUser(Request req, Response res)  throws DataAccessException {
        try {
            handler.logOutUser(req.headers("authorization"));
            res.status(200);
            Map<String, Integer> myDict = new HashMap<>();
            return new Gson().toJson(myDict); // no clue if that works
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(401);
                var newErrorMessage = new ErrorData("Error:" + e.getMessage());
                return serializer.toJson(newErrorMessage);
            }

        }

   }

   private Object getGames(Request req, Response res)  throws DataAccessException {
        try {
            res.status(200);
            var gameTokens = handler.getGames(req.headers("authorization"));
            GamesList games = new GamesList(gameTokens);
            return new Gson().toJson(games);
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Unauthorized")) {
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(500);
                var newErrorMessage = new ErrorData("Error:" + e.getMessage());
                return serializer.toJson(newErrorMessage);
            }
        }

   }


    private Object createGame(Request req, Response res)  throws DataAccessException {
        // need to return the gamename and the gameID. lez go
        try {
            GameCreationData data = new Gson().fromJson(req.body(), GameCreationData.class);

            var authentication = req.headers("authorization");
            var gameName = data.gameName();

            res.status(200);
            GameData newGame = (handler.createGame(authentication, gameName));
            GameCreated returnObject = new GameCreated(newGame.gameID());
            return serializer.toJson(returnObject);
        }
        catch(DataAccessException e) {
            if (Objects.equals(e.getMessage(), "unauthorized")) {
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(401);
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
        }
    }

    private Object joinGame(Request req, Response res)  throws DataAccessException {

        var authToken = req.headers("authorization");
        JoinData data = new Gson().fromJson(req.body(), JoinData.class);
        var playerColor = data.playerColor();
        String gameID = String.valueOf(data.gameID());
        if (authToken == null || gameID == null || playerColor == null) {
            res.status(400);
            return serializer.toJson(new ErrorData("Error: bad request"));
        }
        try {
            handler.joinGame(authToken, playerColor, gameID);
            res.status(200);
            return serializer.toJson(null);
        }
        catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Unauthorized")) {
                res.status(401); // 401
                var newErrorMessage = new ErrorData("Error: unauthorized");
                return serializer.toJson(newErrorMessage);
            }
            if (Objects.equals(e.getMessage(), "that color is taken")) {
                res.status(403); // 403
                var newErrorMessage = new ErrorData("Error: already taken");
                return serializer.toJson(newErrorMessage);
            }
            else {
                res.status(400); // 500
                var newErrorMessage = new ErrorData("Error: Bad request");
                return serializer.toJson(newErrorMessage);
            }
        }




    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        //res.status(ex.StatusCode());
        System.out.println("Something went wrong :(");
    }




}
