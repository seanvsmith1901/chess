package serverfacade;

import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exception.*;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Objects;

import serializer.*;
public class ServerFacade {

    private final String serverUrl;
    private static Gson gson;

    public ServerFacade(String url) {
        serverUrl = url;

        gson = new GsonBuilder() // gets me my custom gson object.
                .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new ChessPositionMapSerializer())
                .registerTypeAdapter(new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(), new ChessPositionMapDeserializer())
                .create();

    }

    public AuthData register(RegisterData newUser) throws ResponseException {
        var path = "/user";
        try {
            return (this.makeRequest("POST", path, newUser, AuthData.class, null));
        }
        catch (ResponseException e) {
            throw new ResponseException(300, "looks like that user already exists. be more creative.");
        }
    }

    public AuthData login(LoginData newUser, String authToken) throws ResponseException {
        var path = "/session";
        try {
            return (this.makeRequest("POST", path, newUser, AuthData.class, authToken));
        }
        catch (ResponseException e) {
            //System.out.println("That user does not exist");
            throw new ResponseException(300, "That user does not exist");
        }
    }

    public Object logOut(String authToken) throws ResponseException {
        var path = "/session";
        try {
            return (this.makeRequest("DELETE", path, authToken, String.class, authToken));
        }
        catch (ResponseException e) {
            throw new ResponseException(300, "You must login first");
        }
    }

    public GameCreated createGame(GameCreationData newGame, String authToken) throws ResponseException {
        var path = "/game";
        try {
            return this.makeRequest("POST", path, newGame, GameCreated.class, authToken);
        }
        catch (ResponseException e) {
            throw new ResponseException(300, "That game already exists");
        }
    }

    public GamesList getGames(String authToken) throws ResponseException {
        var path = "/game";
        try {
            //return this.makeRequest("POST", path, newGame, GameCreated.class, authToken);
            return (this.makeRequest("GET", path, null, GamesList.class, authToken));
        }
        catch (ResponseException e) {
            throw new ResponseException(300, "you need to login first");
        }
    }

    public GameData joinGame(JoinData joinGame, String authToken) throws ResponseException {
        var path = "/game";
        try {
            return this.makeRequest("PUT", path, joinGame, GameData.class, authToken);
        }
        catch (ResponseException e) {
            if(Objects.equals(e.getMessage(), "failure: 403")) {
                throw new ResponseException(300, "That spot is already taken. Join as a different color or observe");
            }
            else {
                throw new ResponseException(300, "that game does not exist. try a different one");
            }
        }
    }

    public GameData observeGame(JoinData joinGame, String authToken) throws ResponseException {
        var path = "/observe";
        try {
            return this.makeRequest("POST", path, joinGame, GameData.class, authToken);
        }
        catch (ResponseException e) {
            throw new ResponseException(300, "that game does not exist or you are not logged in");
        }
    }

    public void resetDataBase() throws ResponseException { // if this ever throws an exception ill end up on the news
        var path = "/db";
        try {
            this.makeRequest("DELETE", path, null, null, null);
        }
        catch (ResponseException e) {
            throw new ResponseException(300, "if this goes off congrats hacker we are tracking your IP now");
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(authToken != null) { // add in the authToken as a header.
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = gson.toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = gson.fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}