package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType pieceType;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.pieceType = promotionPiece;

    }

    // just some getters, we manuall maek new ones a lot so no setters, easier to reset whole thing
    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    // we haven't used this yet but we will get there.
    public ChessPiece.PieceType getPromotionPiece() {
        return this.pieceType;
    }

    // override equals hashcode for tests and toString for debugging.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && pieceType == chessMove.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, pieceType);

    }

    @Override
    public String toString() {
        // return startPosition + ", " + endPosition + ", " + pieceType;
        return endPosition.toString();
    }
}
