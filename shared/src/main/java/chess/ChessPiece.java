package chess;

import com.sun.source.tree.NewArrayTree;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

// a single chess piece with all its methods
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

    // calculates all possible moves and then returns an arraylist of all of those moves.
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // first get the piece and create the return object
        var currentPiece = board.getPiece(myPosition);
        var newArrayList = new ArrayList<ChessMove>();

        // I really should make this a match statement but I am lazy
        if (currentPiece.getPieceType() == PieceType.BISHOP) {
            newArrayList = bishopMoves(myPosition, board);
        }
        if (currentPiece.getPieceType() == PieceType.QUEEN) {
            newArrayList = queenMoves(myPosition, board);
        }
        if (currentPiece.getPieceType() == PieceType.ROOK) {
            newArrayList = rookMoves(myPosition, board);
        }
        if (currentPiece.getPieceType() == PieceType.KING) {
            newArrayList = kingMoves(myPosition, board);
        }
        if (currentPiece.getPieceType() == PieceType.KNIGHT) {
            newArrayList = knightMoves(myPosition, board);
        }
        if (currentPiece.getPieceType() == PieceType.PAWN) {
            newArrayList = pawnMoves(myPosition, board);
        }
        // if the piece is null we return an empty list, which is jsut right
        return newArrayList;
    }

    // this one is by far the most complicated.
    public ArrayList<ChessMove> pawnMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        var currColumn = myPosition.getColumn();
        var currRow = myPosition.getRow();
        // first check that we are in a valid spot then its color
        if ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
            if (this.teamColor == ChessGame.TeamColor.WHITE) {
                if (currRow == 2) { // if we are in the starting position we can try to double jump so check all that
                    var newChessPosition = new ChessPosition(currRow + 2, currColumn);
                    var intermediateChessPosition = new ChessPosition(currRow + 1, currColumn);
                    if ((board.getPiece(newChessPosition) == null) && (board.getPiece(intermediateChessPosition) == null)) {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.addAll(multiplyPawn(newChessMove));
                    }
                }
                currRow++; // either way we can try advancing one (if we are white)
            } else {
                if (currRow == 7) { // we are in the black starting position
                    var newChessPosition = new ChessPosition(currRow - 2, currColumn);
                    var intermediateChessPosition = new ChessPosition(currRow - 1, currColumn);
                    if ((board.getPiece(newChessPosition) == null) && (board.getPiece(intermediateChessPosition) == null)) {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.addAll(multiplyPawn(newChessMove));
                    }
                }
                currRow--; // either way we can try moving only one
            }
            if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) { // bounds checking

                var newChessPosition = new ChessPosition(currRow, currColumn); // for our new position
                if (board.getPiece(newChessPosition) == null) { // if no one is there
                    var newChessMove = new ChessMove(myPosition, newChessPosition, null); // create the chess move
                    possibleMoves.addAll(multiplyPawn(newChessMove)); // this checks for promotions and returns all possible promotiosn
                }

                // test the left and right stuff
                currColumn = myPosition.getColumn(); // reset cause this is color neutral
                currColumn++; // check to our right
                newChessPosition = new ChessPosition(currRow, currColumn);
                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) { // check bounds
                    if (board.getPiece(newChessPosition) != null) { // check to see if there is a piece to the diagonal
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) { // peice and wrong color?
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.addAll(multiplyPawn(newChessMove)); // helper function
                        }
                    }
                }
                // same thing as the above but to the left
                currColumn = myPosition.getColumn();
                currColumn--;
                newChessPosition = new ChessPosition(currRow, currColumn);
                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                    if (board.getPiece(newChessPosition) != null) {
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.addAll(multiplyPawn(newChessMove));
                        }
                    }
                }
            }
        }
        return possibleMoves; // return the possible moves
    }

    // checks for promotions and returns all possible promotions
    public ArrayList<ChessMove> multiplyPawn(ChessMove currentMove) {
        var newMoves = new ArrayList<ChessMove>();
        // checks if we are in a promition spot (there's probably a less costly version of this)
        if (((currentMove.getEndPosition().getRow() == 1) && (getTeamColor() == ChessGame.TeamColor.BLACK)) || (currentMove.getEndPosition().getRow() == 8) && (getTeamColor() == ChessGame.TeamColor.WHITE)) {
            for (var j = 0; j < 4; j++) {
                var newPieceType = PieceType.PAWN;
                if (j == 0) {
                    newPieceType = PieceType.QUEEN;
                }
                if (j == 1) {
                    newPieceType = PieceType.ROOK;
                }
                if (j == 2) {
                    newPieceType = PieceType.KNIGHT;
                }
                if (j == 3) {
                    newPieceType = PieceType.BISHOP;
                }
                var newChessMove = new ChessMove(currentMove.getStartPosition(), currentMove.getEndPosition(), newPieceType);
                newMoves.add(newChessMove);
            }
        } else { // if no promotion possible, return the normal move with a null promotion
            newMoves.add(currentMove);
        }
        return newMoves; // returns all possible stubbins
    }

    // king time
    public ArrayList<ChessMove> kingMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        // so we need to check 8 directions
        // but only run once
        for (var i = 0; i < 8; i++) {
            var currColumn = myPosition.getColumn();
            var currRow = myPosition.getRow();
            if ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) { // check bounds
                // check each of the possible 8 moves
                if (i == 0) {
                    currRow += 1;
                    currColumn += 1;
                }
                if (i == 1) {
                    currRow -= 1;
                    currColumn += 1;
                }
                if (i == 2) {
                    currRow -= 1;
                    currColumn -= 1;
                }
                if (i == 3) {
                    currRow += 1;
                    currColumn -= 1;
                }
                if (i == 4) {
                    currRow += 1;
                }
                if (i == 5) {
                    currRow -= 1;
                }
                if (i == 6) {
                    currColumn += 1;
                }
                if (i == 7) {
                    currColumn -= 1;
                }
                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) { // bounds check again
                    var newChessPosition = new ChessPosition(currRow, currColumn);
                    if (board.getPiece(newChessPosition) != null) { // if there is a capture possible
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.add(newChessMove);
                        }
                    } else { // no capture possible? just move forward.
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                    }
                }
            }
        }
        return possibleMoves;
    }

    // I thought this one was going to be hard but he was easy money
    public ArrayList<ChessMove> knightMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        // so we need to check 8 directions, but only run once
        for (var i = 0; i < 8; i++) {
            var currColumn = myPosition.getColumn();
            var currRow = myPosition.getRow();
            if ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) { // bounds checking
                if (i == 0) {
                    currRow += 2;
                    currColumn += 1;
                }
                if (i == 1) {
                    currRow += 2;
                    currColumn -= 1;
                }
                if (i == 2) {
                    currRow -= 2;
                    currColumn += 1;
                }
                if (i == 3) {
                    currRow -= 2;
                    currColumn -= 1;
                }
                if (i == 4) {
                    currRow += 1;
                    currColumn += 2;
                }
                if (i == 5) {
                    currRow += 1;
                    currColumn -= 2;
                }
                if (i == 6) {
                    currRow -= 1;
                    currColumn += 2;
                }
                if (i == 7) {
                    currRow -= 1;
                    currColumn -= 2;
                }

                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) { // bounds checking again
                    var newChessPosition = new ChessPosition(currRow, currColumn);
                    if (board.getPiece(newChessPosition) != null) { // if there is a capture possible
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.add(newChessMove);
                        }
                    } else { // just add the move
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                    }
                }
            }
        }

        return possibleMoves; // retunrs all the moves

    }

    // our first infinite scaler - follow this format for bisohp queen and rook
    public ArrayList<ChessMove> bishopMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        for (var i = 0; i < 4; i++) { // check all directions (4)
            var currColumn = myPosition.getColumn();
            var currRow = myPosition.getRow();
            while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) { // this is a while bc we can move till edge of board
                if (i == 0) { // check each of the 4 combinations
                    currRow += 1;
                    currColumn += 1;
                }
                if (i == 1) {
                    currRow -= 1;
                    currColumn += 1;
                }
                if (i == 2) {
                    currRow -= 1;
                    currColumn -= 1;
                }
                if (i == 3) {
                    currRow += 1;
                    currColumn -= 1;
                }
                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) { // make sure new move is in bounds
                    var newChessPosition = new ChessPosition(currRow, currColumn);
                    if (board.getPiece(newChessPosition) != null) { // if there is a piece
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.add(newChessMove);
                            break; // we can't move any further forward
                        } else {
                            break; // have to explicitly break on infinite repeaters otherwise we WILL keep moving
                        }
                    } else { // no piece just keep moving forward
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                    }
                }
            }
        }
        return possibleMoves; // return the moves
    }

    // basically the same as the bishop
    public ArrayList<ChessMove> queenMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        for (var i = 0; i < 8; i++) {
            var currColumn = myPosition.getColumn();
            var currRow = myPosition.getRow();
            while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
                if (i == 0) {
                    currRow += 1;
                    currColumn += 1;
                }
                if (i == 1) {
                    currRow -= 1;
                    currColumn += 1;
                }
                if (i == 2) {
                    currRow -= 1;
                    currColumn -= 1;
                }
                if (i == 3) {
                    currRow += 1;
                    currColumn -= 1;
                }
                if (i == 4) {
                    currRow += 1;
                }
                if (i == 5) {
                    currRow -= 1;
                }
                if (i == 6) {
                    currColumn += 1;
                }
                if (i == 7) {
                    currColumn -= 1;
                }
                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                    var newChessPosition = new ChessPosition(currRow, currColumn);
                    if (board.getPiece(newChessPosition) != null) {
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.add(newChessMove);
                            break;
                        } else {
                            break;
                        }
                    } else {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                    }
                }
            }

        }
        return possibleMoves;
    }

    // basically the same as the bishop
    public ArrayList<ChessMove> rookMoves(ChessPosition myPosition, ChessBoard board) {
        var possibleMoves = new ArrayList<ChessMove>();
        for (var i = 0; i < 4; i++) {
            var currColumn = myPosition.getColumn();
            var currRow = myPosition.getRow();
            while ((currRow < 9) && (currColumn < 9) && (currRow > 0) && (currColumn > 0)) {
                if (i == 0) {
                    currRow += 1;
                }
                if (i == 1) {
                    currRow -= 1;
                }
                if (i == 2) {
                    currColumn += 1;
                }
                if (i == 3) {
                    currColumn -= 1;
                }
                if (!((currRow > 8) || (currColumn > 8) || (currRow < 1) || (currColumn < 1))) {
                    var newChessPosition = new ChessPosition(currRow, currColumn);
                    if (board.getPiece(newChessPosition) != null) {
                        if (board.getPiece(newChessPosition).getTeamColor() != this.teamColor) {
                            var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                            possibleMoves.add(newChessMove);
                            break;
                        } else {
                            break;
                        }
                    } else {
                        var newChessMove = new ChessMove(myPosition, newChessPosition, null);
                        possibleMoves.add(newChessMove);
                    }
                }
            }
        }
        return possibleMoves;
    }


    // override the equals and hashcode for tests, and the toString for debugging
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    @Override
    public String toString() {
        var string = "L";
        // make sure that white is upper and black is lower
        if (ChessGame.TeamColor.BLACK.equals(teamColor)) {
            if (type == PieceType.KING) {
                string = "k";
            }
            if (type == PieceType.QUEEN) {
                string = "q";
            }
            if (type == PieceType.KNIGHT) {
                string = "n";
            }
            if (type == PieceType.BISHOP) {
                string = "b";
            }
            if (type == PieceType.ROOK) {
                string = "r";
            }
            if (type == PieceType.PAWN) {
                string = "p";
            }
        }
        if (ChessGame.TeamColor.WHITE.equals(teamColor)) {
            if (type == PieceType.KING) {
                string = "K";
            }
            if (type == PieceType.QUEEN) {
                string = "Q";
            }
            if (type == PieceType.KNIGHT) {
                string = "N";
            }
            if (type == PieceType.BISHOP) {
                string = "B";
            }
            if (type == PieceType.ROOK) {
                string = "R";
            }
            if (type == PieceType.PAWN) {
                string = "P";
            }
        }
        return string;
    }
}


