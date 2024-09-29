package chess;

//import java.lang.foreign.FunctionDescriptor;
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
    private ChessBoard board = new ChessBoard(); //thats it I guess

    // need to somehow give it access to a board.


    public ChessGame() { // this just sets the board to defualt upon a new chess game, otherwise I assume we are being given a board.
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { // just a little guy / getter
        return turn;
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
        setTeamTurn(currentPiece.getTeamColor());  // this fetching infuriates me lol
        Collection<ChessMove> validMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> finalMoves = currentPiece.pieceMoves(board, startPosition);

        if (currentPiece == null) {
            return null; // covers no piece base case
        } else {
                for (ChessMove currentMove : validMoves) { // makes cycles through all possible valid moves generated under chess piece
                    try {
                         checkMove(currentMove);

                    } catch (InvalidMoveException e) { // if the move is invalid, we remove it from the finalMoves vector.
                        //System.out.println(e.getMessage());
                        finalMoves.remove(currentMove);
                    }
                }
            return finalMoves; // returns all the moves that HAVEN't thrown an exception.
        }
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public boolean checkMove(ChessMove move) throws InvalidMoveException { // given a move from a valid move list, check to make sure we can in fact move there.

        ChessPiece currentPiece = board.getPiece(move.getStartPosition()); // gets the piece of interest
        var teamColor = currentPiece.getTeamColor(); // we are just checking, not executing, make sure we can move where we want.
        Collection<ChessMove> validMoves = currentPiece.pieceMoves(board, move.getStartPosition()); // creates all the valid moves for that piece

        if (validMoves.contains(move)) { // if our move IS, in fact, a valid move
            if(teamColor == getTeamTurn()) { // checks if its our turn
                if (isInCheck(teamColor)) { // if we are in check, make sure we get out of check
                    ChessPiece possiblePiece = executeMove(move);
                    if(isInCheck(teamColor)) { // we are still in check boys, that move did NOT save us
                        unExecuteMove(move, possiblePiece); // undo that move and throw the exception
                        throw new InvalidMoveException("You're still in check and that doesn't get you out! beware!");
                    }
                    unExecuteMove(move, possiblePiece); // move is valid and we will execute it at the end of the function
                }
                else {
                    // check for pawn position and promotion piece (make sure promotion is valid)
                    if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        if (move.getPromotionPiece() != null) {
                            if (!((teamColor == TeamColor.BLACK && move.getEndPosition().getRow() == 1) || (teamColor == TeamColor.WHITE && move.getEndPosition().getRow() == 8))) {
                                throw new InvalidMoveException("You can't promote a pawn there, don't do that");
                            }
                        }
                    }
                }
                // if we have cleard everything, make the move
                ChessPiece possiblePiece = executeMove(move);
                if(isInCheck(teamColor)) { // if that move puts us in check,
                    unExecuteMove(move, possiblePiece); // we need to walk it back
                    throw new InvalidMoveException("that move puts your king in check so don't do that"); // and throw the exception
                }
                unExecuteMove(move, possiblePiece); // undoes the above move, walks it back to where we started.
            }
            else {
                throw new InvalidMoveException("Not your turn yet fetcher"); // this never goes off IIRC, just under make move.
            }
        }
        else {
            throw new InvalidMoveException("Not a valid move, try again"); // your move is not in the valid moves list for that piece.
        }
        return true;
    }



    public void makeMove(ChessMove move) throws InvalidMoveException { // this actually makes the user requested move, if it can
        if (board != null) {
            if (board.getPiece(move.getStartPosition()) != null) { // makes sure we are dealing with a valid piece
                if(checkMove(move)) { // if that move is a valid move (using what we used above
                    executeMove(move); // we don't care about the promotion piece that will get stored on the board as appropriate
                    changeTeamTurn(board.getPiece(move.getEndPosition()).getTeamColor()); // gets the color of the piece that we JUST moved, and changes the board turn appropriately.
                }
            }
            else {
                throw new InvalidMoveException("brotha there aint no piece there!"); // i mean there ain't no pieces there
            }
        }
        else {
            throw new InvalidMoveException("there ain't not board!"); // i mean there ain't no board, nothing has been set.
        }

    }


    public Boolean getOutOfCheck(TeamColor teamColor) { // wants to see if the king CAN get out of check.
        if (!(canKingMove(teamColor))) { // if the king can't move, we need to test to see if any of the other pieces can save him
            ArrayList<ChessPosition> myPieces = board.getTeamPositions(teamColor); // gets all of our pieces.

            for(ChessPosition myPiecePosition : myPieces) {  // for every piece on the board
                for(ChessMove currentMove : validMoves(myPiecePosition)) { // for every move that that piece can make
                    ChessPiece possiblePiece = executeMove(currentMove); // need possible pieces for edgecases later.
                    unExecuteMove(currentMove, possiblePiece); // walk it back, just needed the piece not the move.
                    try {
                        makeMove(currentMove); // try making that move
                    }
                    catch(InvalidMoveException e) { // if that move throws an error, catch it here.
                        System.out.println("try again"); // this should be game over actually.
                    }
                    if(!(isInCheck(teamColor))) { // if after that move we are no longer in check
                        return true; // we can get out of check, return true.
                    }
                    unExecuteMove(currentMove, possiblePiece); // walk that move back, we were just worried about it as a concept.
                }
            }
        }
        return false; // if none of the other pieces can save us, give up
    }



    public ChessPosition getKingPosition(TeamColor teamColor) { // long fetching funciton to just find out where our team's king is on the board.
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getTeamPieces(teamColor); // gets all of our team pieces off of the board.
        ChessPosition checkPosition = new ChessPosition(-1,-1); // not even on the board, will never show up under valid moves. (THIS IS ASSUMING WE ALWAYS HAVE A KING ON THE BOARD
        for (ChessPosition currentPosition : currentPieces.keySet()) { // for every piece that we have in our vector
            if(currentPieces.get(currentPosition).getPieceType() == ChessPiece.PieceType.KING) { // if that piece type is a king
                checkPosition = currentPosition; // return the kiugns position up
            }
        }
        return checkPosition; // needs a return statement - if we don't have a king, we can never be checkmated so we return him as being off the board.
    }

    public ChessPiece executeMove(ChessMove move) { // just picks up the piece, puts him in the new spot and handles a couple of edgecases in the meantime
        ChessPiece currentPiece = board.getPiece(move.getStartPosition()); // gets the piece of interest
        ChessPiece possiblePiece = null; // if we capture a piece, we need to be able to put him back when we are done adn we gotta put him somewhere.
        if(board.getPiece(move.getEndPosition()) != null) {
            possiblePiece = board.getPiece(move.getEndPosition()); // sets him to non-null if we accidentally capturd a peep.
        }
        board.deletePiece(move.getStartPosition()); // deletes the piece that we are moving
        if(move.getPromotionPiece() != null) { // if we need to promote, put down that piecetype piece instead
            ChessPiece newPiece = new ChessPiece(currentPiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), newPiece);
        }
        else { // there is no promotion, just add our piece to his new end position.
            board.addPiece(move.getEndPosition(), currentPiece);

        }
        return possiblePiece; // returns the possibly captured piece
    }

    public void unExecuteMove(ChessMove move, ChessPiece oldPiece) { // walks back our move, and puts back the piece that we might have captured
        ChessPiece currentPiece = board.getPiece(move.getEndPosition()); // gets the piece we need to walk back
        board.deletePiece(move.getEndPosition()); // deletes where he was
        board.addPiece(move.getStartPosition(), currentPiece); // puts him back where he started
        if (oldPiece != null){ // if there was a captured piece
            board.addPiece(move.getEndPosition(), oldPiece); // put him back too.
        }
    }


    public void changeTeamTurn(TeamColor teamColor) { // Criss-cross! changes from white to black and vice versa, represents turn shifting.
        if (teamColor == TeamColor.BLACK) {
            turn = TeamColor.WHITE;
        }
        else {
            turn = TeamColor.BLACK;
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if (this.board != null) { // gotta make sure we got a board first and foremost.
            var checkPosition = getKingPosition(teamColor); // gets our kings position and we need to see if that position is in danger
            var currentPieces = board.getOtherTeamPieces(teamColor); // gets all of the pieces that could attack the king

            for (ChessPosition currentPosition : currentPieces.keySet()) { // for all of the pieces that could attakc the king
                Collection<ChessMove> currentPieceMoves = currentPieces.get(currentPosition).pieceMoves(board, currentPosition); // gets all of their valid moves
                // need to get all teh end posiitons in order for this to work.
                Collection<ChessPosition> endPositions = new ArrayList<>();

                for (ChessMove currentMove : currentPieceMoves) { // gets all the end positions of those generated moves
                    endPositions.add(currentMove.getEndPosition());
                }

                if (endPositions.contains(checkPosition)) { // if any of those generated moves contains the endposition that is the same as the kings position, he can be captured and we gotta help him.
                    return true; // we are in check!
                }
            }
            return false; // we are safe, nothing can hit us
        }
        return false; // there is no board, so we CAN'T be in check. neat
    }



    public boolean isPositionAvaliable(TeamColor teamColor, ChessPosition checkPosition) { // I have no idea what this does. I wrote this as a one off IG?
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getOtherTeamPieces(teamColor);

        for (ChessPosition currentPosition : currentPieces.keySet()) {
            Collection<ChessMove> currentPieceMoves = currentPieces.get(currentPosition).pieceMoves(board, currentPosition);
            if (!(currentPieceMoves.contains(checkPosition))) {
                return false;
            }
        }
        return true;
    }
    

    public boolean canKingMove(TeamColor teamColor) { // checks to see if the king can move to any of the positions around him without escapitn check
        // used under checkmate stalemate and get out of check.
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
    public boolean isInCheckmate(TeamColor teamColor) { // once the underlying logic was down this was easy

        if (this.board != null) {
            if (!(canKingMove(teamColor)) && !(getOutOfCheck(teamColor)) && isInCheck(teamColor)) { // if he can't move, he can't get out of check and he is in check
                return true;
            }
            else {
                return false;
            }
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
        if(this.board != null) {
            if  (!(canKingMove(teamColor)) && !(getOutOfCheck(teamColor)) && !(isInCheck(teamColor))) { // if he can't move or get out of check but hes not IN check
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) { // funny little setter
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() { // funny little getter 
        return board;
    }
}
