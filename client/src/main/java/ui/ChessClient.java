package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import ServerFacade.*;
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
            out.print(ERASE_SCREEN);
            return drawBoard(thisGame);
        }

        throw new ResponseException(400, "You are not signed in");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1 && state == State.SIGNEDIN) {
            var gameID = Integer.parseInt(params[0]);
            var gameListID = Integer.parseInt(params[1]);
            var joinData = new JoinData(null, gameID);
            var thisGame = server.joinGame(joinData, authToken);
            System.out.println("Success! You are observing " + gamesList.get(gameListID).gameName() + " as an observer");
            return drawBoard(thisGame);
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

    private String drawBoard(GameData game) throws ResponseException {
        String[] topAndBottomLetters = {"   ", " H  ", " G  ", " F  ", "E ", "  D ", " C ", "  B ", "  A ", "   "};
        ChessBoard board = game.game().getBoard();

        for (var i = 0; i < 10; i++) { // this first rep is whiteTopfirst
            for (var j = 0; j < 10; j++) {
                if(i == 0 || i == 9) {
                    out.print(SET_BG_COLOR_RED);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(topAndBottomLetters[j]);
                }
                else if(j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_RED);
                    out.print(" " + String.valueOf(i) + " ");
                }
                else {
                    if(i % 2 == 0) {
                        if (j % 2 == 0) {
                            out.print(SET_BG_COLOR_WHITE);
                        }
                        else {
                            out.print(SET_BG_COLOR_BLUE);
                        }
                    }
                    else { // odd, start light
                        if (j % 2 == 0) {
                            out.print(SET_BG_COLOR_BLUE);
                        }
                        else {
                            out.print(SET_BG_COLOR_WHITE);
                        }
                    }
                    ChessPosition newPosition = new ChessPosition(i, j);
                    ChessPiece currentPiece = board.getPiece(newPosition);
                    if(currentPiece == null) {
                        out.print(EMPTY);
                    }
                    else {
                        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                                out.print(WHITE_BISHOP);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                                out.print(WHITE_KNIGHT);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                                out.print(WHITE_PAWN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                out.print(WHITE_KING);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                                out.print(WHITE_QUEEN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                                out.print(WHITE_ROOK);
                            }
                        }
                        else {
                            if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                                out.print(BLACK_BISHOP);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                                out.print(BLACK_KNIGHT);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                                out.print(BLACK_PAWN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                out.print(BLACK_KING);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                                out.print(BLACK_QUEEN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                                out.print(BLACK_ROOK);
                            }
                        }
                    }
                }
            }
            out.print("\n");
        }

        for (var i = 9; i >=0; i--) { // this is black top now
            for (var j = 9; j >= 0; j--) {
                if(i == 0 || i == 9) {
                    out.print(SET_BG_COLOR_RED);
                    out.print(SET_TEXT_COLOR_BLACK);
                    out.print(topAndBottomLetters[j]);
                }
                else if(j == 0 || j == 9) {
                    out.print(SET_BG_COLOR_RED);
                    out.print(" " + String.valueOf(i) + " ");
                }
                else {
                    if(i % 2 == 0) {
                        if (j % 2 == 0) {
                            out.print(SET_BG_COLOR_WHITE);
                        }
                        else {
                            out.print(SET_BG_COLOR_BLUE);
                        }
                    }
                    else { // odd, start light
                        if (j % 2 == 0) {
                            out.print(SET_BG_COLOR_BLUE);
                        }
                        else {
                            out.print(SET_BG_COLOR_WHITE);
                        }
                    }
                    ChessPosition newPosition = new ChessPosition(i, j);
                    ChessPiece currentPiece = board.getPiece(newPosition);
                    if(currentPiece == null) {
                        out.print(EMPTY);
                    }
                    else {
                        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                            if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                                out.print(WHITE_BISHOP);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                                out.print(WHITE_KNIGHT);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                                out.print(WHITE_PAWN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                out.print(WHITE_KING);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                                out.print(WHITE_QUEEN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                                out.print(WHITE_ROOK);
                            }
                        }
                        else {
                            if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                                out.print(BLACK_BISHOP);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                                out.print(BLACK_KNIGHT);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                                out.print(BLACK_PAWN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                                out.print(BLACK_KING);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                                out.print(BLACK_QUEEN);
                            }
                            else if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                                out.print(BLACK_ROOK);
                            }
                        }
                    }
                }
            }
            out.print("\n");
        }
        return out.toString();
    }
}