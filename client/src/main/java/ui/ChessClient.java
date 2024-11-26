package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import model.*;

import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import serverfacade.*;
import chess.*;

import model.*;
import exception.ResponseException;

import static ui.EscapeSequences.*;

import websocket.commands.Move;
import websocket.commands.Position;


public class ChessClient {
    private String visitorName = null;
    private ServerFacade server;
    private String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = "";
    private String username = "";
    private ArrayList<GameData> gamesList = new ArrayList<>();
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private GameData currentGame = null;

    private String teamColor = null;
    private static final int BOARD_SIZE_IN_SQAURES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;

    private NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private boolean tried = false;
    private Bucket bucket;


    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.bucket = new Bucket();
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;


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

                case "redraw" -> redrawBoard(params);
                case "leave" -> leaveGame(params);
                case "move" -> makeMove(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightMoves(params);


                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 3) {
            visitorName = params[0];
            var newUser = new RegisterData(params[0], params[1], params[2]);
            var newAuthData = server.register(newUser); // where should I store that auth token client side? just as a global variable?
            authToken = newAuthData.authToken();
            username = newAuthData.username();
            state = State.SIGNEDIN;
            return String.format("You signed up as %s.", visitorName);
        }
        throw new ResponseException(400, "USAGE: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 2) {
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
        assertSignedIn();
        if (params.length == 0) {
            assertSignedIn();
            state = State.SIGNEDOUT;
            var newAuthData = server.logOut(authToken);
            return ("You signed out");
        }
        throw new ResponseException(400, "you aint signed in boy");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gameName = params[0];
            GameCreationData newGame = new GameCreationData(gameName);
            var thisGame = server.createGame(newGame, authToken);
            return String.format("You have created game %s", thisGame);
        }
        throw new ResponseException(400, "mnake sure you have inlcuded a game name");
    }

    public String listGames(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 0) {
            var games =  server.getGames(authToken);
            gamesList.clear(); // make sure its empty when we start
            gamesList.addAll(games.games()); // keeps track of the order in which they were listed
            return games.toString();
        }
        throw new ResponseException(400, "You are not signed in");
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            var teamColor = params[1];
            // filters color input.
            if (!(Objects.equals(teamColor, "BLACK") || Objects.equals(teamColor, "black") || Objects.equals(teamColor, "WHITE")
                    || Objects.equals(teamColor, "white"))) {
                throw new ResponseException(300, "that color bad :( ! please try again");
            }

            var input = Integer.parseInt(params[0]);
            if(input > gamesList.size()) {
                throw new ResponseException(300, "That game does not exist! please try again");
            }
            else {
                var gameID = gamesList.get(input-1).gameID();
                var joinData = new JoinData(teamColor, gameID);
                var thisGame = server.joinGame(joinData, authToken);
                currentGame = thisGame;
                ws = new WebSocketFacade(serverUrl, notificationHandler, bucket, username);
                ws.joinGame(authToken, gameID, username, teamColor, currentGame.gameName());


                System.out.println("Success! You have joined " + gamesList.get(input-1).gameName() + " as color " + teamColor);
                out.print(ERASE_SCREEN);
                state = State.INGAME; //do this AFTER we get confirmation from websocket
                bucket.setChessGame(thisGame, username);

                return "";
            }
        }

        throw new ResponseException(400, " check that you entered a team color");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            var gamesListID = Integer.parseInt(params[0]);
            if (gamesListID > gamesList.size()) {
                throw new ResponseException(300, "That game does not exist! please try again");
            }
            else {
                var gameID = gamesList.get(gamesListID-1).gameID();
                var joinData = new JoinData(null, gameID);
                var thisGame = server.observeGame(joinData, authToken);
                currentGame = thisGame;
                ws = new WebSocketFacade(serverUrl, notificationHandler, bucket, username);
                ws.joinGame(authToken, gameID, username, null, currentGame.gameName());
                System.out.println("Success! You are observing " + gamesList.get(gameID-1).gameName() + " as an observer");
                state = State.INGAME; //do this AFTER we get confirmation from websocket
                bucket.setChessGame(thisGame, username);
                return "";
            }

        }
        throw new ResponseException(400, "You are not signed in or your inputs are wrong. get wrecked.");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account"
                    - login <USERNAME> <PASSWORD> - to play chess
                    - quit - playing chess
                    - help - with possible commands
                    """;
        }
        else if (state == State.SIGNEDIN) {
            return """
                - create <NAME> - a game
                - logout - when you are done
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observer <ID> - a game
                - quit - playing chess
                - help - with possible commands
                """;
        }
        else {
            return """
                - redraw - redraws the chessboard
                - leave - removes you from chessgame
                - makeMove <startPosition>, <endPosition> <promotion piece>
                - resign - want to give this one up cheif?
                - highlight legal moves <PIECE <Q,K>
                - help - with possible commands
                """;

        }

    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException(400, "you gotta sign out my guy");
        }
    }

    public String redrawBoard(String... params) throws ResponseException {
        if (currentGame == null) {
            return "";
        }
        bucket.displayBoard(username);
        return "";
    }
    public String leaveGame(String... params) throws ResponseException {
        // remove conneciton to webscoket and reset currentgmae to null. lets go.
        ws.leaveGame(authToken, currentGame.gameID(), username, currentGame.gameName(), teamColor);
        return "You have left the game";
    }
    public String makeMove(String... params) throws ResponseException {
        Move moveToSend = null;

        try {
            String oldMove = params[0];
            int rowToSend = charToIntRow(oldMove.charAt(0));
            int oldCol = Integer.parseInt(String.valueOf(oldMove.charAt(1))); // yeah thats definitely legal


            String newMove = params[1];
            int newRowToSend = charToIntRow(newMove.charAt(0));
            int newCol = Integer.parseInt(String.valueOf(newMove.charAt(1))); // yeah thats definitely legal

            Position startPosition = new Position(oldCol, rowToSend);
            Position endPosition = new Position(newCol, newRowToSend);

            moveToSend = new Move(startPosition, endPosition);
        }
        catch (Exception e) {
            System.out.println("Check your move formatting, looks funky");
            return "";
        }


        String promotionPiece = "none";
        if(params.length == 5){
            promotionPiece = params[4];
        }
        Integer gameID = currentGame.gameID();
        try {
            ws.makeMove(authToken, gameID, username, teamColor, moveToSend, promotionPiece, currentGame.gameName());
        }
        catch (Exception e) {
            System.out.println("something went wrong. IDK what. might not be a valid move. I'll fix this up more later. ");
        }

        return "";
    }
    public String resign(String... params) throws ResponseException {
        if(!tried) {
            tried = true;
            return ("Are you sure you want to resign? type resign again to confirm.");
        }
        else {
            ws.resign(authToken, currentGame.gameID(), currentGame.gameName(), username);
            tried = false;
            return "you have resigned. fetcher.";
        }


    }
    public String highlightMoves(String... params) throws ResponseException {
        var startingPosition = params[0];
        int rowToSend = charToIntRow(startingPosition.charAt(0));
        int oldCol = Integer.parseInt(String.valueOf(startingPosition.charAt(1))); // yeah thats definitely legal
        Position newPosition = new Position(oldCol, rowToSend);
        ws.returnLegalMoves(authToken, newPosition, currentGame.gameID(), currentGame.gameName());
        return "";

    }

    private char charToIntRow(char currRow) {
        if(currRow == 'A' || currRow == 'a') {
            currRow = 1;
        }
        if(currRow == 'B' || currRow == 'b') {
            currRow = 2;
        }
        if(currRow == 'C' || currRow == 'c') {
            currRow = 3;
        }
        if(currRow == 'D' || currRow == 'd') {
            currRow = 4;
        }
        if(currRow == 'E' || currRow == 'e') {
            currRow = 5;
        }
        if(currRow == 'F' || currRow == 'f') {
            currRow = 6;
        }
        if(currRow == 'G' || currRow == 'g') {
            currRow = 7;
        }
        if(currRow == 'H' || currRow == 'h') {
            currRow = 8;
        }
        return currRow;
    }

}