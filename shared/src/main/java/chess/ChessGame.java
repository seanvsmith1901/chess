package chess;

//import java.lang.foreign.FunctionDescriptor;
import java.sql.Array;
import java.util.*;

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
    public boolean checkMove(ChessMove move) throws InvalidMoveException { // from valid moves, can move there?

        ChessPiece currentPiece = board.getPiece(move.getStartPosition()); // gets the piece of interest
        var teamColor = currentPiece.getTeamColor(); // we are just checking to see if we can move there
        Collection<ChessMove> validMoves = currentPiece.pieceMoves(board, move.getStartPosition()); // get valid moves

        if (validMoves.contains(move)) { // if our move IS, in fact, a valid move
            if(teamColor == getTeamTurn()) { // checks if its our turn
                if (isInCheck(teamColor)) { // if we are in check, make sure we get out of check
                    ChessPiece possiblePiece = executeMove(move);
                    if(isInCheck(teamColor)) { // we are still in check boys, that move did NOT save us
                        unExecuteMove(move, possiblePiece); // undo that move and throw the exception
                        throw new InvalidMoveException("You're still in check and that doesn't get you out! beware!");
                    }
                    unExecuteMove(move, possiblePiece); // move is valid and will execute upon completion
                }
                else {
                    // check for pawn position and promotion piece (make sure promotion is valid)
                    if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        if (move.getPromotionPiece() != null) {
                            if (!((teamColor == TeamColor.BLACK && move.getEndPosition().getRow() == 1) ||
                                    (teamColor == TeamColor.WHITE && move.getEndPosition().getRow() == 8))) {
                                throw new InvalidMoveException("You can't promote a pawn there, don't do that");
                            }
                        }
                    }
                }
                // if we have cleard everything, make the move
                ChessPiece possiblePiece = executeMove(move);
                if(isInCheck(teamColor)) { // if that move puts us in check,
                    unExecuteMove(move, possiblePiece); // we need to walk it back
                    throw new InvalidMoveException("that move puts your king in check so don't do that"); // throw error
                }
                unExecuteMove(move, possiblePiece); // undoes the above move, walks it back to where we started.
            }
            else {
                throw new InvalidMoveException("Not your turn yet fetcher"); // not your turn!
            }
        }
        else {
            throw new InvalidMoveException("Not a valid move, try again"); //
        }
        return true;
    }



    public void makeMove(ChessMove move) throws InvalidMoveException { // tries to make user request move
        if (board != null) {
            if (board.getPiece(move.getStartPosition()) != null) { // makes sure we are dealing with a valid piece
                if(checkMove(move)) { // if that move is a valid move (using what we used above
                    executeMove(move); // we don't care about return value cause this is going through for sure.
                    changeTeamTurn(board.getPiece(move.getEndPosition()).getTeamColor()); // changes turn from our color
                }
            }
            else {
                throw new InvalidMoveException("brotha there aint no piece there!");
            }
        }
        else {
            throw new InvalidMoveException("there ain't not board!");
        }
    }


    public Boolean getOutOfCheck(TeamColor teamColor) { // wants to see if the king CAN get out of check.
        if (!(canKingMove(teamColor))) { // can the king more or nah
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
                    unExecuteMove(currentMove, possiblePiece); // walk that move back
                }
            }
        }
        return false; // if none of the other pieces can save us, give up
    }



    public ChessPosition getKingPosition(TeamColor teamColor) { // find where our king is
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getTeamPieces(teamColor); // gets all of our pieces
        ChessPosition checkPosition = new ChessPosition(-1,-1); // if no king, at invalid position
        for (ChessPosition currentPosition : currentPieces.keySet()) { // for every piece that we have in our vector
            if(currentPieces.get(currentPosition).getPieceType() == ChessPiece.PieceType.KING) { // if is king
                checkPosition = currentPosition; // return the kiugns position up
            }
        }
        return checkPosition; // no king? return invalid position we can't be checked or checkmated.
    }

    public ChessPiece executeMove(ChessMove move) { // moves piece and returns peice we killed
        ChessPiece currentPiece = board.getPiece(move.getStartPosition()); // gets the piece of interest
        ChessPiece possiblePiece = null; // if we capture a piece, we need to be able to put him back and store him
        if(board.getPiece(move.getEndPosition()) != null) {
            possiblePiece = board.getPiece(move.getEndPosition()); // keep track of this dude
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

    public void unExecuteMove(ChessMove move, ChessPiece oldPiece) { // walks back move, puts back killed peice
        ChessPiece currentPiece = board.getPiece(move.getEndPosition()); // gets the piece we need to walk back
        board.deletePiece(move.getEndPosition()); // deletes where he was
        board.addPiece(move.getStartPosition(), currentPiece); // puts him back where he started
        if (oldPiece != null){ // if there was a captured piece
            board.addPiece(move.getEndPosition(), oldPiece); // put him back too.
        }
    }


    public void changeTeamTurn(TeamColor teamColor) { // Criss-cross! changes from white to black and vice versa
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
            var checkPosition = getKingPosition(teamColor); // checks if king is in danger from his position
            var currentPieces = board.getOtherTeamPieces(teamColor); // gets all the pieces that could attack the king

            for (ChessPosition currentPosition : currentPieces.keySet()) { // for all dangerous pieces
                Collection<ChessMove> currentPieceMoves =
                        currentPieces.get(currentPosition).pieceMoves(board, currentPosition);
                // need to get all teh end posiitons in order for this to work.
                Collection<ChessPosition> endPositions = new ArrayList<>();

                for (ChessMove currentMove : currentPieceMoves) { // gets all the end positions of those generated moves
                    endPositions.add(currentMove.getEndPosition());
                }

                if (endPositions.contains(checkPosition)) { // if any of those generated moves contains the endposition
                    return true; // we are in check!
                }
            }
            return false; // we are safe, nothing can hit us
        }
        return false; // there is no board, so we CAN'T be in check. neat
    }



    public boolean isPositionAvaliable(TeamColor teamColor, ChessPosition checkPosition) { // no clue what this does
        HashMap<ChessPosition, ChessPiece> currentPieces = board.getOtherTeamPieces(teamColor);

        for (ChessPosition currentPosition : currentPieces.keySet()) {
            Collection<ChessMove> currentPieceMoves =
                    currentPieces.get(currentPosition).pieceMoves(board, currentPosition);
            if (!(currentPieceMoves.contains(checkPosition))) {
                return false;
            }
        }
        return true;
    }
    

    public boolean canKingMove(TeamColor teamColor) { // can king escape by moving himself
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
            if (!(canKingMove(teamColor)) && !(getOutOfCheck(teamColor)) && isInCheck(teamColor)) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
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
            if  (!(canKingMove(teamColor)) && !(getOutOfCheck(teamColor)) && !(isInCheck(teamColor))) {
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
