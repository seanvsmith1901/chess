package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }




    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (var color = 0; color < 2; color++) {
            var teamColor = ChessGame.TeamColor.WHITE;
            if (color == 1) {
                teamColor = ChessGame.TeamColor.BLACK;
            }
            for (var i = 1; i < 9; i++) {
                var newChessPosition = new ChessPosition(2, i);
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    newChessPosition = new ChessPosition(7, i);
                }
                var newChessPiece = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
                addPiece(newChessPosition,  newChessPiece);
            }
            for (var i = 1; i < 9; i++) {
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
        for (int rows = 0; rows < 8; rows++) {
            for (int columns = 0; columns < 8; columns++) {
                board += "|";
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
