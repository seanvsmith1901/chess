package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;



import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_ROOK;

public class Bucket {

    private PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    GameData currentGame;

    public void setChessGame(GameData newChessGame, String username) {
        currentGame = newChessGame;
        displayBoard(username);
    }

    public void displayBoard(String username) {
        try {
            drawBoard(currentGame, username);
        }
        catch (ResponseException e) {
            e.printStackTrace();
        }
    }

    public String drawBoard(GameData game, String username) throws ResponseException {
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

}
