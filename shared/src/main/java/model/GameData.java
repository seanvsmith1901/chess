package model;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;


public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public String toString() {
        return game.getBoard().toString();
    }

    public String printPretty() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        ChessBoard board = game.getBoard();

        for (int rows = 7; rows >= 0; rows--) {
            out += "|";
            for (int columns = 0; columns <= 7; columns++) {
                if (squares[rows][columns] != null) {
                    board += squares[rows][columns].toString();
                }
                else {
                    board += " ";
                }
                board += "|";
            }
            board += "\n";
        }
        return board;
        // first we have to print the white one with all the headers

        // then we have to reverse it and print the black ones

        // then we return the whole thing
        // also append teh whole thing so its easier to rip apart.
    }

}

