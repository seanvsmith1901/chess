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
            // fix this at some point bc its broken but its not really important yet
//            if (!(Objects.equals(teamColor, "black") || (Objects.equals(teamColor, "white")))) {
//                throw new ResponseException(400, "that color aint real");
//            }
            var input = Integer.parseInt(params[0]);
            if(input > gamesList.size()) {
                throw new ResponseException(300, "That game does not exist! please try again");
            }
            else {
                var gameID = gamesList.get(input-1).gameID();
                var joinData = new JoinData(teamColor, gameID);
                var thisGame = server.joinGame(joinData, authToken);
                currentGame = thisGame;
                this.teamColor = teamColor;
                ws = new WebSocketFacade(serverUrl, notificationHandler, bucket, username);
                ws.joinGame(authToken, gameID, username, teamColor);
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
                ws.joinGame(authToken, gameID, username, null);
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

    public String drawBoard(GameData game) throws ResponseException {
        ChessBoard board = game.game().getBoard(); // gets our board
        String[] topAndBottomLetters = {"   ", " A  ", " B  ", " C  ", "D ", "  E ", " F ", "  G ", "  H ", "   "};

        // if we are white, black top, else white top always
        var whiteTop = false;
        if (Objects.equals(game.blackUsername(), username)) {
            whiteTop = true;
        }

        // prints the white on top
        if(whiteTop) {
            for (int i = 0; i < 10; i++) {
                printBoardRow(i, topAndBottomLetters, board, true); // white maybe
            }
        }
        //prints black on top
        else {
            for (int i = 9; i >= 0; i--) {
                printBoardRow(i, topAndBottomLetters, board, false); // black
            }
        }


        out.print(RESET_TEXT_COLOR); // resets text and backround color
        out.print(RESET_BG_COLOR);
        return "";
    }

    private void printBoardRow(int row, String[] topAndBottomLetters, ChessBoard board, boolean whiteTop) {

        int startCol = whiteTop ? 9 : 0;
        int endCol = whiteTop ? -1 : 10;
        int step = whiteTop? -1 : 1;

        for (int col = startCol; col != endCol; col+=step) {
            if (row == 0 || row == 9) {
                printTopAndBottomRow(col, topAndBottomLetters);
            }
            else if (col == 0 || col == 9) {
                printSideColumn(row);
            }
            else {
                printBoardCell(row, col, board);
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
    private void printBoardCell(int row, int col, ChessBoard board) {
        String bgColor = getCellBackgroundColor(row, col);
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
    private String getCellBackgroundColor(int row, int col) {
        return (row + col) % 2 == 0 ? SET_BG_COLOR_BLUE : SET_BG_COLOR_WHITE;

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
        String oldPosition = params[0];
        String newPosition = params[1];
        String promotionPiece = "none";
        if(params.length == 3){
            promotionPiece = params[2];
        }
        Integer gameID = currentGame.gameID();
        try {
            ws.makeMove(authToken, gameID, username, teamColor, oldPosition, newPosition, promotionPiece, currentGame.gameName());
        }
        catch (Exception e) {
            System.out.println("something went wrong. IDK what. might not be a valid move. I'll fix this up more later. ");
        }

        return "";
    }
    public String resign(String... params) throws ResponseException {
        return "";

    }
    public String highlightMoves(String... params) throws ResponseException {
        var startingPosition = params[0];
        ws.returnLegalMoves(authToken, startingPosition, currentGame.gameID(), currentGame.gameName());
        return "";

    }

}