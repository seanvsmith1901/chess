package ServerFacade;

import com.google.gson.Gson;
import com.sun.net.httpserver.BasicAuthenticator;
import exception.*;
import model.*;

import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(RegisterData newUser) throws ResponseException {
        var path = "/user";
        try {
            return (this.makeRequest("POST", path, newUser, AuthData.class, null));
        }
        catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        throw new ResponseException(300, "What the fetch");
    }

    public AuthData login(LoginData newUser, String authToken) throws ResponseException {
        var path = "/session";
        try {
            return (this.makeRequest("POST", path, newUser, AuthData.class, authToken));
        }
        catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        throw new ResponseException(300, "What the fetch");
    }

    public Object logOut(String authToken) throws ResponseException {
        var path = "/session";
        try {
            return (this.makeRequest("DELETE", path, authToken, String.class, authToken));
        }
        catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        throw new ResponseException(300, "What the fetch");
    }

    public Object createGame(GameCreationData newGame, String authToken) throws ResponseException {
        var path = "/game";
        try {
            return this.makeRequest("POST", path, newGame, GameCreated.class, authToken);
        }
        catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        throw new ResponseException(300, "What the fetch");
    }

    public Object getGames(String authToken) throws ResponseException {
        var path = "/game";
        var newGame = "bestGame";
        try {
            //return this.makeRequest("POST", path, newGame, GameCreated.class, authToken);
            return (this.makeRequest("GET", path, authToken, GamesList.class, authToken));
        }
        catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        throw new ResponseException(300, "What the fetch");
    }


//    public Pet addPet(Pet pet) throws ResponseException {
//        var path = "/pet";
//        return this.makeRequest("POST", path, pet, Pet.class);
//    }
//
//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var path = "/pet";
//        this.makeRequest("DELETE", path, null, null);
//    }
//
//    public Pet[] listPets() throws ResponseException {
//        var path = "/pet";
//        record listPetResponse(Pet[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }

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
            String reqData = new Gson().toJson(request);
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
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}