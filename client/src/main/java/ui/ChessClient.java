package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import serverfacade.*;
import chess.*;

import model.*;
import exception.ResponseException;

import static ui.EscapeSequences.*;



public class ChessClient {
    private String visitorName = null;
    private ServerFacade server;
    private String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = "";
    private String username = "";
    private ArrayList<GameData> gamesList = new ArrayList<>();
    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final int BOARD_SIZE_IN_SQAURES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;




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
        assertSignedOut();
        if (params.length == 3) {
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
            var input = Integer.parseInt(params[0]);
            if(input > gamesList.size()) {
                throw new ResponseException(300, "That game does not exist! please try again");
            }
            else {
                var gameID = gamesList.get(input-1).gameID();
                var joinData = new JoinData(teamColor, gameID);
                var thisGame = server.joinGame(joinData, authToken);

                System.out.println("Success! You have joined " + gamesList.get(input-1).gameName() + " as color " + teamColor);
                out.print(ERASE_SCREEN);
                return drawBoard(thisGame);
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

                System.out.println("Success! You are observing " + gamesList.get(gameID-1).gameName() + " as an observer");
                return drawBoard(thisGame);
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

    private String drawBoard(GameData game) throws ResponseException {
        ChessBoard board = game.game().getBoard(); // gets our board
        String[] topAndBottomLetters = {"   ", " H  ", " G  ", " F  ", "E ", "  D ", " C ", "  B ", "  A ", "   "};

        // this goes through row by row

        for (int i = 0; i < 10; i++) {
            printBoardRow(i, topAndBottomLetters, board, true); // white
        }

        for (int i = 0; i < 10; i++) {
            printBoardRow(i, topAndBottomLetters, board, false); // black
        }

        out.print(RESET_TEXT_COLOR); // resets text and backround color
        out.print(RESET_BG_COLOR);
        return "";
    }

    private void printBoardRow(int row, String[] topAndBottomLetters, ChessBoard board, boolean whiteTop) {
        // if its white top, its forward, if not we have to reverse the letters.
        String[] adjustedLetters = whiteTop ? topAndBottomLetters : reverseArray(topAndBottomLetters);

        for (int col = 0; col < 10; col++) {
            if (row == 0 || row == 9) { // catches top and bottom edgecases
                printTopAndBottomRow(col, adjustedLetters);
            } else if (col == 0 || col == 9) { // catches left and right edgecases
                printSideColumn(row);
            } else { // this is where most of the bogos are binted
                printBoardCell(row, col, board, whiteTop);
            }
        }
        out.print(RESET_BG_COLOR);
        out.print("\n"); // gotta make sure the spacing works out.
    }

    // special case for top row and bottom row
    private void printTopAndBottomRow(int col, String[] topAndBottomLetters) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(topAndBottomLetters[col]); // prints our things at the inxex with the specificed column
    }

    // prints the rows the way they should be.
    private void printSideColumn(int row) {
        out.print(SET_BG_COLOR_RED);
        out.print(" " + String.valueOf(row) + " ");
    }

    // alternates backround colors, gets the peice if there is and controls backround color.
    private void printBoardCell(int row, int col, ChessBoard board, boolean whiteTop) {
        String bgColor = getCellBackgroundColor(row, col, whiteTop);
        out.print(bgColor);

        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);

        if (piece == null) {
            out.print(EMPTY);
        } else {
            out.print(getPieceRepresentation(piece));
        }
    }

    // condesned version of the logic for getting backround color
    private String getCellBackgroundColor(int row, int col, boolean whiteTop) {
        return (row + col) % 2 == 1 ? SET_BG_COLOR_BLUE : SET_BG_COLOR_WHITE;

    }
    // grabs the string representation of the chesspiece
    private String getPieceRepresentation(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
            };
        } else {
            return switch (piece.getPieceType()) {
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
            };
        }
    }

    private String[] reverseArray(String[] array) {
        String[] reversed = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }

}