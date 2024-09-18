package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 */
public class ChessBoard {
    // regardless its alwasys going to be an 8 by 8 array, so we don't need a constructor cause its always the same
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    // adds a peice to board given the position and the piece
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    // snags a piece given the position on the board.
    public ChessPiece getPiece(ChessPosition position) {
        // its -1 becuase we represent the positions as 1-8 on the surface but they are really 0-7, need to adjust
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    // resets the board to the opening configuration.
    public void resetBoard() {
        for (var color = 0; color < 2; color++) {
            var teamColor = ChessGame.TeamColor.WHITE;
            if (color == 1) {
                teamColor = ChessGame.TeamColor.BLACK;
            }
            for (var i = 1; i < 9; i++) { // adds the pawns first
                var newChessPosition = new ChessPosition(2, i);
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    newChessPosition = new ChessPosition(7, i);
                }
                var newChessPiece = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
                addPiece(newChessPosition,  newChessPiece);
            }
            for (var i = 1; i < 9; i++) { // add everything else, white or black. (the order is the same)
                var newChessPosition = new ChessPosition(1, i);
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    newChessPosition = new ChessPosition(8, i);
                }
                var pieceType = ChessPiece.PieceType.PAWN;
                if (i == 1 || i == 8) {
                    pieceType = ChessPiece.PieceType.ROOK;
                }
                if (i == 2 || i == 7) {
                    pieceType = ChessPiece.PieceType.KNIGHT;
                }
                if (i == 3 || i == 6) {
                    pieceType = ChessPiece.PieceType.BISHOP;
                }
                if (i == 4) {
                    pieceType = ChessPiece.PieceType.QUEEN;
                }
                if (i == 5) {
                    pieceType = ChessPiece.PieceType.KING;
                }
                var newChessPiece = new ChessPiece(teamColor, pieceType);
                addPiece(newChessPosition, newChessPiece);
            }
        }
    }

    // override the equals, hashcode and tostring for testing purposes.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        var board  = "";
        for (int rows = 7; rows >= 0; rows--) {
            board += "|";
            for (int columns = 7; columns >= 0; columns--) {
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
    }
}
