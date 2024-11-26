package dataaccess;

import chess.*;
import model.GameData;
import websocket.commands.Move;

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

    public void removeUserWithGameID(String gameID, String username) throws DataAccessException {
        dataAccess.removeUserWithGameID(gameID, username);
    }

    public GameData updateGame(Integer gameID, String username, Move move, ChessGame.TeamColor teamColor, String promotionPiece) throws DataAccessException {
        var currentGame = dataAccess.getGameFromID(String.valueOf(gameID));
        if (currentGame.gameCompleted()) {
            throw new DataAccessException("Game over! no more moves! Sorry :(");
        }
        ChessGame modifiiedGame = currentGame.game();

        //var currentBoard = modifiiedGame.getBoard();
        // TODO: Fix this once you can format moves correctly.

        ChessPosition oldFormalPosition = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getCol());
        ChessPosition newFormalPosition = new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getCol());

        ChessPiece.PieceType newPiece = null;
        if(!Objects.equals(promotionPiece, null)) {
            newPiece = getPieceType(promotionPiece);
        }

        ChessMove newChessMove = new ChessMove(oldFormalPosition, newFormalPosition, newPiece);

        try {
            checkTeamColor(modifiiedGame, teamColor);
            modifiiedGame.makeMove(newChessMove);
            modifiiedGame.changeTeamTurn(teamColor); // adjusts the turn appropriately (should really move this to inside makeMove haha)
            dataAccess.updateGame(gameID, modifiiedGame); // this should update the actual game
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

    private void checkTeamColor(ChessGame currentGame, ChessGame.TeamColor currColor) throws DataAccessException {
        if (currColor != currentGame.getTeamTurn()) {
            throw new DataAccessException("Wait yo turn pls");
        }
    }

}
