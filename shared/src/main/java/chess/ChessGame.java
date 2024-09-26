package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn = TeamColor.WHITE;
    private ChessBoard board; //thats it I guess

    // need to somehow give it access to a board.


    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (turn.equals("White")) {
            return TeamColor.WHITE;
        }
        else {
            return TeamColor.BLACK;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { // just flips it like a coin
        if(team.equals(TeamColor.WHITE)) {
            turn = TeamColor.BLACK;
        }
        else{
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
            var currentPiece = board.getPiece(startPosition);
            var validMoves = new ArrayList<ChessMove>();

            if(currentPiece == null) {
                return null;
            }
            else {
                return currentPiece.pieceMoves(board, startPosition);
            }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            ChessPiece currentPiece = board.getPiece(move.getStartPosition());
            var teamColor = currentPiece.getTeamColor();

            if(teamColor == getTeamTurn()) {
                if(validMoves(move.getStartPosition()).contains(move))  {
                    if (isInCheck(teamColor)) {

                        if(currentPiece.getPieceType() != ChessPiece.PieceType.KING) {
                            throw new InvalidMoveException("Your king is in check! try again");
                        }
                        else {
                            if(isPositionAvaliable(teamColor, move.getEndPosition())) {

                            }
                        }
                    }
                    else {
                        // check for pawn position and promotion piece
                        if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {

                        }



                        //after we preform the move, make sure we don't end up in check lol

                    }

                    // we know its real but we need to make sure we aren't in check and we we aren't in stalement
                    // if the king is in check we need to make sure the are moving the king
                    // if there is a promotion piece and there is a pawn in range we need to make sure to change that piece to that piece.
                    // also if the move goves in we also need to change the player color
                    //
                    // and then at the very end we get to do this
                    executeMove(move);
                    if(isInCheck(teamColor)) {
                        unExecuteMove(move);
                        throw new InvalidMoveException("that move puts your king in check so don't do that");
                    }
                }
            }
            else {
                throw new InvalidMoveException("Not your turn yet fetcher");
            }



            if ((isInCheckmate(teamColor)) || (isInStalemate(teamColor))) {
                ; // idk what to do here, but we are here
                // end the game!
            }





        }
        catch(Exception invalidMovesException) {
            invalidMovesException.printStackTrace(); // just to do something but IDK how we want to handle this
        }


    }


    public Boolean getOutOfCheck(TeamColor teamColor) {
        if (!(canKingMove(teamColor))) {
            getChecKPieces(teamColor) {

            }
        }
        return false;
    }

    Collection<ChessPosition> getChecKPieces(TeamColor teamColor) { // returns all the pieces that have the king in they sight
        ChessPosition kingPosition = getKingPosition(teamColor);
        var otherPieces = board.getOtherTeamPieces(teamColor);
        Collection<ChessPosition> piecesThatWIllKillYOu;
        Collection<ChessMove> currentPieceMoves = piecesThatWIllKillYOu.get(kingPosition).pieceMoves(board, kingPosition);




    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getTeamPieces(teamColor);
        ChessPosition checkPosition = new ChessPosition(-1,-1); // not even on the board, will never trip
        for (ChessPosition currentPosition : currentPieces.keySet()) {
            if(currentPieces.get(currentPosition).getPieceType() == ChessPiece.PieceType.KING) {
                checkPosition = currentPosition; // gets hte king position of our color
            }
        }
        return checkPosition;
    }

    public void executeMove(ChessMove move) { // only used after we've checked everything else lol
        setTeamTurn(getTeamTurn());
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        board.deletePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), currentPiece);
    }

    public void unExecuteMove(ChessMove move) {
        setTeamTurn(getTeamTurn()); // its kind of like a coin
        ChessPiece currentPiece = board.getPiece(move.getEndPosition());
        board.deletePiece(move.getEndPosition());
        board.addPiece(move.getStartPosition(), currentPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var checkPosition = getKingPosition(teamColor);
        var currentPieces = board.getOtherTeamPieces(teamColor);

        for (ChessPosition currentPosition : currentPieces.keySet()) {
            Collection<ChessMove> currentPieceMoves = currentPieces.get(currentPosition).pieceMoves(board, currentPosition);
            if (currentPieceMoves.contains(checkPosition)) {
                return true;
            }

        }
        return false;
    }

    
    public boolean isPositionAvaliable(TeamColor teamColor, ChessPosition checkPosition) {
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getOtherTeamPieces(teamColor);

        for (ChessPosition currentPosition : currentPieces.keySet()) {
            Collection<ChessMove> currentPieceMoves = currentPieces.get(currentPosition).pieceMoves(board, currentPosition);
            if (!(currentPieceMoves.contains(checkPosition))) {
                return false;
            }
        }
        return true;
    }
    

    public boolean canKingMove(TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getTeamPieces(teamColor);
        // first we need to get the king and find his valid moves
        Collection<ChessMove> kingMoves = List.of();
        for (ChessPosition currentPosition : currentPieces.keySet()) {
            if(currentPieces.get(currentPosition).getPieceType() == ChessPiece.PieceType.KING) {
                kingMoves = currentPieces.get(currentPosition).pieceMoves(board, currentPosition);
            }
        }
        for (ChessMove currentMove : kingMoves) {
            ChessPosition endPosition = currentMove.getEndPosition();
            if (!(isPositionAvaliable(teamColor, endPosition))) {
                return false;
            }
        }
        return true;


    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) { // I have to reprogram this a lot.
        if ((canKingMove(teamColor) == false) && (isInCheck(teamColor))) {
            return true;
        }
        return false;
        
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if ((canKingMove(teamColor) == false) && (isInCheck(teamColor) == false)) {
            
            return true;
        }
        return false;
                
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = this.board.setBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
