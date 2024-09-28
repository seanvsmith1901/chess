package chess;

import java.sql.Array;
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
        if (turn == TeamColor.WHITE) {
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
            turn = TeamColor.WHITE;
        }
        else{
            turn = TeamColor.BLACK;
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
        Collection<ChessMove> validMoves = currentPiece.pieceMoves(board, startPosition);

        if (currentPiece == null) {
            return null; // covers base case 1
        } else {
                for (ChessMove currentMove : validMoves) {
                    try {
                        checkMove(currentMove);

                    } catch (InvalidMoveException e) {
                        //System.out.println(e.getMessage());
                        validMoves.remove(currentMove);
                    }
                }
            return validMoves;
        }
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void checkMove(ChessMove move) throws InvalidMoveException { // given a move from a valid move list, check to make sure we can in fact move there.
        ChessPiece currentPiece = board.getPiece(move.getStartPosition());
        var teamColor = currentPiece.getTeamColor();
        Collection<ChessMove> validMoves = currentPiece.pieceMoves(board, move.getStartPosition());

        if(teamColor == getTeamTurn()) { // checks if its our turn
            // get the actual piece moves here lol, this is gonna call itself recuresively.
                if (isInCheck(teamColor)) { // if we are in check, make sure we get out of check
                    executeMove(move);
                    if(isInCheck(teamColor)) { // we are still in check boys
                        unExecuteMove(move);
                        throw new InvalidMoveException("You're still in check and that doesn't get you out! beware!");
                    }
                    unExecuteMove(move); // unexecute it regardless because we will still do it at the end.
                }
                else {
                    // check for pawn position and promotion piece (make sure promotion is valid)
                    if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        if (!((teamColor == TeamColor.BLACK && move.getEndPosition().getRow() == 1) || (teamColor == TeamColor.WHITE && move.getEndPosition().getRow() == 8))) {
                            throw new InvalidMoveException("You can't promote a pawn there, don't do that");
                        }
                    }
                }
                // if we have cleard everything, make the move
                executeMove(move);
                if(isInCheck(teamColor)) { // if that move puts us in check, we need to walk it back and try again
                    unExecuteMove(move);
                    throw new InvalidMoveException("that move puts your king in check so don't do that");
                }
                unExecuteMove(move); // this function doesn't actually DO the move, it just checks to make sure its valid. undo it when you done.
        }
        else {
            throw new InvalidMoveException("Not your turn yet fetcher");
        }
    }



    public void makeMove(ChessMove move) throws InvalidMoveException {
        ;
    }




    public Boolean getOutOfCheck(TeamColor teamColor) {
        if (!(canKingMove(teamColor))) {
            ArrayList<ChessPosition> myPieces = board.getTeamPositions(teamColor);

            //ArrayList<ChessMove> movesThatCancelCheck = new ArrayList<ChessMove>(); // incase we need to tell players where they CAN move

            // check every move that every one of my peices can make, and if it can get me out of check.
            for(ChessPosition myPiecePosition : myPieces) {
                for(ChessMove currentMove : validMoves(myPiecePosition)) {
                    try {
                        makeMove(currentMove);
                    }
                    catch(InvalidMoveException e) {
                        System.out.println("try again");
                    }
                    if(!(isInCheck(teamColor))) {
                        return true;
                    }
                    unExecuteMove(currentMove);
                }
            }
        }
        return false;
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
            // need to get all teh end posiitons in order for this to work.
            Collection<ChessPosition> endPositions = new ArrayList<>();

            for (ChessMove currentMove : currentPieceMoves) { // gets all the end positions
                endPositions.add(currentMove.getEndPosition());
            }

            if (endPositions.contains(checkPosition)) {
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
       if (!(canKingMove(teamColor)) && !(getOutOfCheck(teamColor)) && isInCheck(teamColor)) {
           return true;
       }
       else {
           return false;
       }
        
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if  (!(canKingMove(teamColor)) && !(getOutOfCheck(teamColor)) && isInCheck(teamColor)) {
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
        this.board = board;
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
