package dataaccess;

import chess.*;
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

    public GameData updateGame(Integer gameID, String username, String oldPosition, String newPosition, String teamColor, String promotionPiece) throws DataAccessException {
        var currentGame = dataAccess.getGameFromID(String.valueOf(gameID));
        if (currentGame.gameCompleted()) {
            throw new DataAccessException("Game over! no more moves! Sorry :(");
        }
        ChessGame modifiiedGame = currentGame.game();


        ChessGame.TeamColor currColor = getTeamColor(teamColor, modifiiedGame);

        //var currentBoard = modifiiedGame.getBoard();

        var oldRow = oldPosition.charAt(0); // this one needs to change
        var oldCol = oldPosition.charAt(1); // this one does not.
        oldRow = charToIntRow(oldRow);

        var currRow = newPosition.charAt(0); // this one needs to change
        var currCol = newPosition.charAt(1); // this one does not.
        currRow = charToIntRow(currRow);

        ChessPosition oldFormalPosition = new ChessPosition(Character.getNumericValue(oldCol), oldRow);
        ChessPosition newFormalPosition = new ChessPosition(Character.getNumericValue(currCol), currRow);
        ChessPiece.PieceType newPiece = null;
        if(!Objects.equals(promotionPiece, "none")) {
            newPiece = getPieceType(promotionPiece);
        }

        ChessMove newChessMove = new ChessMove(oldFormalPosition, newFormalPosition, newPiece);

        try {
            modifiiedGame.makeMove(newChessMove);
            modifiiedGame.changeTeamTurn(currColor); // adjusts the turn appropriately (should really move this to inside makeMove haha)
            dataAccess.updateGame(gameID, modifiiedGame); // this should update the actual game
            System.out.println("WE HAVE MADE A MOVE! SHOULD ONLY HAPPEN ONCE");
            var returnGame = dataAccess.getGameFromID(String.valueOf(gameID));
            // chekc to see if they are in check or whatnot.
            return returnGame;
        } catch (InvalidMoveException e) {
            throw new DataAccessException(e.getMessage()); // update this later (should tell them if it doesn't get em out of check or whatnot)
        }

    }

    public void markGameCompleted(GameData gameData) throws DataAccessException {
        GameData newGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.game(), true);
        dataAccess.replaceGame(gameData.gameID(), newGame);
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



    private static ChessPiece.PieceType getPieceType(String peice) {
        var currPieceType = ChessPiece.PieceType.BISHOP;


        if(Objects.equals(peice, "Q") || Objects.equals(peice, "q")) {
            currPieceType = ChessPiece.PieceType.QUEEN;
        }
        if(Objects.equals(peice, "K") || Objects.equals(peice, "k")) {
            currPieceType = ChessPiece.PieceType.KING;
        }
        if(Objects.equals(peice, "P") || Objects.equals(peice, "p")) {
            currPieceType = ChessPiece.PieceType.PAWN;
        }
        if(Objects.equals(peice, "B") || Objects.equals(peice, "b")) {
            currPieceType = ChessPiece.PieceType.BISHOP;
        }
        if(Objects.equals(peice, "R") || Objects.equals(peice, "r")) {
            currPieceType = ChessPiece.PieceType.ROOK;
        }
        if(Objects.equals(peice, "N") || Objects.equals(peice, "n")) {
            currPieceType = ChessPiece.PieceType.KNIGHT;
        }
        return currPieceType;
    }

    private static ChessGame.TeamColor getTeamColor(String teamColor, ChessGame modifiiedGame) throws DataAccessException {
        ChessGame.TeamColor currColor = null;

        if(Objects.equals(teamColor, "WHITE") || Objects.equals(teamColor, "white")) {
            currColor = ChessGame.TeamColor.WHITE;
        }
        if(Objects.equals(teamColor, "BLACK") || Objects.equals(teamColor, "black")) {
            currColor = ChessGame.TeamColor.BLACK;
        }


        if(modifiiedGame.getTeamTurn() != currColor) { // make sure the player can in fact make a move.
            throw new DataAccessException("Wait yo turn!");
        }
        return currColor;
    }

}
