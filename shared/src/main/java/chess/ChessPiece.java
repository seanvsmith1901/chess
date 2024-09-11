package chess;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // so given that we get teh board, and the position
        // we need to find the peice that we are dealing with
        // run that function
        // and then return the stubbins

        var currentPiece = board.getPiece(myPosition);

        // for whatever reason I can't get it to check the type right.
        var currentPieceType = currentPiece.getPieceType();
        var newArrayList = new ArrayList<ChessMove>();

        // I mean I guess we start with the bishop

        if (currentPiece.getPieceType() == PieceType.BISHOP) {
            newArrayList = BishopMoves(myPosition, board);
        }

        return newArrayList;
    }


    public ArrayList<ChessMove> KingMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        return possibleMoves;

    }

    //change the paradigm - we should only enter pieces positions between 1 and 8,
    // so surface is still between 1 and 8
    public ArrayList<ChessMove> BishopMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();

        //added this comment to make sure everything is working correctly

        // first, up and right
        var currColumn = myPosition.getColumn();
        var currRow = myPosition.getRow();

        while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
            currRow += 1;
            currColumn += 1;
            if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                var newChessPosition = new ChessPosition(currRow, currColumn);
                if (board.getPiece(newChessPosition) != null) {
                    if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                        break;
                    }
                    else {
                        break;
                    }
                }
                else {
                    var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                    possibleMoves.add(newChessMove);
                }
            }
        }

        // now down and right
        currColumn = myPosition.getColumn();
        currRow = myPosition.getRow();

        while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
            currRow -= 1;
            currColumn += 1;
            if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                var newChessPosition = new ChessPosition(currRow, currColumn);
                if (board.getPiece(newChessPosition) != null) {
                    if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                        break;
                    }
                    else {
                        break;
                    }

                }
                else {
                    var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                    possibleMoves.add(newChessMove);
                }
            }
        }


        // down and left
        currColumn = myPosition.getColumn();
        currRow = myPosition.getRow();

        while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
            currRow -= 1;
            currColumn -= 1;
            if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                var newChessPosition = new ChessPosition(currRow, currColumn);
                if (board.getPiece(newChessPosition) != null) {
                    if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                        break;
                    }
                    else {
                        break;
                    }

                }
                else {
                    var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                    possibleMoves.add(newChessMove);
                }
            }
        }

        // up and left
        currColumn = myPosition.getColumn();
        currRow = myPosition.getRow();

        while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
            currRow += 1;
            currColumn -= 1;
            if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                var newChessPosition = new ChessPosition(currRow, currColumn);
                if (board.getPiece(newChessPosition) != null) {
                    if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                        break;
                    }
                    else {
                        break;
                    }

                }
                else {
                    var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                    possibleMoves.add(newChessMove);
                }
            }
        }

        return possibleMoves;

    }

}


