package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ServerFacade.*;
import com.google.gson.Gson;
import model.*;
import exception.ResponseException;


public class ChessClient {
    private String visitorName = null;
    private ServerFacade server;
    private String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = "";
    private String username = "";
    private ArrayList<GameData> gamesList = new ArrayList<>();

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;

    }

    public State getState() {
        return state;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                // pre login logic
                case "register" -> register(params);
                case "login" -> login(params);

                // post login logic
                case "logout" -> logOut(params);
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);

                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3 && state == State.SIGNEDOUT) {
            visitorName = params[0];
            var newUser = new RegisterData(params[0], params[1], params[2]);
            var newAuthData = server.register(newUser); // where should I store that auth token client side? just as a global variable?
            authToken = newAuthData.authToken();
            state = State.SIGNEDIN;
            return String.format("You signed up as %s.", visitorName);
        }
        throw new ResponseException(400, "USAGE: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2 && state == State.SIGNEDOUT) {
            visitorName = params[0];
            var newUser = new LoginData(params[0], params[1]);
            var newAuthData = server.login(newUser, authToken);
            authToken = newAuthData.authToken();
            username = newAuthData.username();
            state = State.SIGNEDIN;
            return String.format("You logged in as %s", username);
        }
        throw new ResponseException(400, "USAGE: <USERNAME> <PASSWORD> ");
    }

    public String logOut(String... params) throws ResponseException {
        if (params.length == 0 && state == State.SIGNEDIN) {
            state = State.SIGNEDOUT;
            var newAuthData = server.logOut(authToken);
            return String.format("You signed out as %s.", newAuthData);
        }
        throw new ResponseException(400, "you aint signed in boy");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1 && state == State.SIGNEDIN) {
            var gameName = params[0];
            GameCreationData newGame = new GameCreationData(gameName);
            var thisGame = server.createGame(newGame, authToken);
            return String.format("You have joined game %s", thisGame);
        }
        throw new ResponseException(400, "mnake sure you have inlcuded a game name");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0 && state == State.SIGNEDIN) {
            var games =  server.getGames(authToken);
            gamesList.addAll(games.games()); // keeps track of the order in which they were listed
            return games.toString();
        }
        throw new ResponseException(400, "You are not signed in");
    }

    public String joinGame(String... params) throws ResponseException {
        if (state != State.SIGNEDIN) {
            return "You gotta login cheif";
        }
        if (params.length == 2) {
            var teamColor = params[1];
            var gameListID = Integer.parseInt(params[0]);
            var gameID = gamesList.get(gameListID).gameID();
            var joinData = new JoinData(teamColor, gameID);
            var thisGame = server.joinGame(joinData, authToken);

            System.out.println("Success! You have joined " + gamesList.get(gameListID).gameName() + " as color " + teamColor);
            return thisGame.gameName();
        }

        throw new ResponseException(400, "You are not signed in");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1 && state == State.SIGNEDIN) {
            var gameID = Integer.parseInt(params[0]);
            var gameListID = Integer.parseInt(params[1]);
            var joinData = new JoinData(null, gameID);
            server.joinGame(joinData, authToken);
            return ("Success! You are observing " + gamesList.get(gameListID).gameName() + " as an observer");
        }
        throw new ResponseException(400, "You are not signed in or your inputs are wrong. get wrecked.");
    }




//    public String rescuePet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            var name = params[0];
//            var type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
//            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
//    }
//
//    public String listPets() throws ResponseException {
//        assertSignedIn();
//        var pets = server.listPets();
//        var result = new StringBuilder();
//        var gson = new Gson();
//        for (var pet : pets) {
//            result.append(gson.toJson(pet)).append('\n');
//        }
//        return result.toString();
//    }
//
//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                var id = Integer.parseInt(params[0]);
//                var pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(400, "Expected: <pet id>");
//    }
//
//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (var pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String signOut() throws ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
//        state = ServerFacade.State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }
//
//    private Pet getPet(int id) throws ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account"
                    - login <USERNAME> <PASSWORD> - to play chess
                    - quit - playing chess
                    - help - with possible commands
                    """;
        }
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observer <ID> - a game
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}