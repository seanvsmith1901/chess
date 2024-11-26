package model;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;


public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName,
                       ChessGame game, Boolean gameCompleted) {


    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName,
                    ChessGame game, Boolean gameCompleted) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
        this.gameCompleted = gameCompleted;
        if(gameCompleted == null) {
            gameCompleted = false;
        }
    }

    @Override
    public String toString() {
        return game.getBoard().toString();
    }

}



