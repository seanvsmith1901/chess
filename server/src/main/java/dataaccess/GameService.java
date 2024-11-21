package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;


public class GameService {

    private static DataAccess dataAccess;

    public GameService(DataAccess dataAccess) { // sets the correct dataaccess module
        this.dataAccess = dataAccess;
    }

    public HashSet<GameData> getGames() throws DataAccessException { // returns a hashset of all of the games
        return dataAccess.getGames();
    }
//
    public void createGame(String gameName) throws DataAccessException { // creates a new game
        dataAccess.createGame(gameName);
    }

    public GameData getGame(String gameName) throws DataAccessException { // gets a game from its game name
        return dataAccess.getGame(gameName);
    }

    public GameData getGameFromID(String gameID) throws DataAccessException { // gets the game from ID
        return dataAccess.getGameFromID(gameID);
    }

    public void removeUser(String gameName, String username) throws DataAccessException {
        dataAccess.removeUser(gameName, username);
    }

    public void updateGame(String gameName, String username, String peice, String newMove, String teamColor) throws DataAccessException {
        var currentGame = dataAccess.getGame(gameName);
        var modifiiedGame = currentGame.game();
        var currentBoard = modifiiedGame.getBoard();
        var currColor = ChessGame.TeamColor.BLACK;
        var currPieceType = ChessPiece.PieceType.BISHOP;

        if(Objects.equals(teamColor, "WHITE") || Objects.equals(teamColor, "white")) {
            currColor = ChessGame.TeamColor.WHITE;
        }


        if(Objects.equals(peice, "Q") || Objects.equals(peice, "q")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }
        if(Objects.equals(peice, "K") || Objects.equals(peice, "k")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }
        if(Objects.equals(peice, "P") || Objects.equals(peice, "p")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }
        if(Objects.equals(peice, "B") || Objects.equals(peice, "b")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }
        if(Objects.equals(peice, "R") || Objects.equals(peice, "r")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }
        if(Objects.equals(peice, "N") || Objects.equals(peice, "n")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }

        var currentPieces = currentBoard.getTeamPieces(currColor);
        chess.ChessPosition startPosition;
        for (var thisPeice : currentPieces.entrySet()) {
            if(thisPeice.getValue().getPieceType() == currPieceType) {
                startPosition = thisPeice.getKey();
            }
        }

        var currRow = newMove.charAt(0); // this one needs to change
        var currCol = newMove.charAt(1); // this one does not.

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

        String[] topAndBottomLetters = { "A", "B", "C", "D ", "E", "F", "G", "H"};

        ChessPosition newPosition = new ChessPosition(currRow, currCol);
        ChessMove newMove = new ChessMove(startPosition, newPosition, null);
        modifiiedGame.makeMove()

    }

}
