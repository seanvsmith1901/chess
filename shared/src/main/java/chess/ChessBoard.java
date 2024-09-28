package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 */
public class ChessBoard {
    // regardless its alwasys going to be an 8 by 8 array, so we don't need a constructor cause its always the same
    private ChessPiece[][] squares = new ChessPiece[8][8];
    HashMap<ChessPosition, ChessPiece> whitePieces = new HashMap<>();
    HashMap<ChessPosition, ChessPiece> blackPieces = new HashMap<>();

    public ChessBoard() {

    }

    public HashMap<ChessPosition, ChessPiece> getTeamPieces(ChessGame.TeamColor teamColor) {
        if(teamColor == ChessGame.TeamColor.BLACK)
        {
            return blackPieces;
        }
        else {
            return whitePieces;
        }
    }

    public ArrayList<ChessPosition> getTeamPositions(ChessGame.TeamColor teamColor) {
        ArrayList<ChessPosition> positions = new ArrayList<>();
        if(teamColor == ChessGame.TeamColor.BLACK) {
            for (ChessPosition newPosition : blackPieces.keySet()) {
                positions.add(newPosition);
            }
        }
        else {
            for (ChessPosition newPosition : whitePieces.keySet()) {
                positions.add(newPosition);
            }
        }
        return positions;
    }

    public ArrayList<ChessPosition> getOtherTeamPositions(ChessGame.TeamColor teamColor) {
        ArrayList<ChessPosition> positions = new ArrayList<>();
        if(teamColor == ChessGame.TeamColor.WHITE) {
            positions.addAll(blackPieces.keySet());
        }
        else {
           positions.addAll(blackPieces.keySet());
        }
        return positions;
    }

    public HashMap<ChessPosition, ChessPiece> getOtherTeamPieces(ChessGame.TeamColor teamColor) {
        if(teamColor == ChessGame.TeamColor.BLACK)
        {
            return whitePieces;
        }
        else {
            return blackPieces;
        }
    }


    // adds a peice to board given the position and the piece
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK)
        {
            blackPieces.put(position, piece);
        }
        else {
            whitePieces.put(position, piece);
        }
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void deletePiece(ChessPosition position) {
        if(getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            blackPieces.remove(position);
        }
        else {
            whitePieces.remove(position);
        }
        squares[position.getRow()-1][position.getColumn()-1] = null;

    }

    // snags a piece given the position on the board.
    public ChessPiece getPiece(ChessPosition position) {
        // its -1 becuase we represent the positions as 1-8 on the surface but they are really 0-7, need to adjust
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    public void resetMaps() {

        // color is implied through name but we can always double check, but here we just have the positions of all the pieces of each color and then the piece itself.
        HashMap<ChessPosition, ChessPiece> newWhitePieces = new HashMap<>();
        HashMap<ChessPosition, ChessPiece> newBlackPieces = new HashMap<>();

        whitePieces.clear();
        blackPieces.clear();


        for(var row = 1; row < 9; row++) {
            for (var column = 1; column < 9; column++) {
                var newChessPosition = new ChessPosition(row, column);
                if(getPiece(newChessPosition) != null) {
                    if (getPiece(newChessPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        newBlackPieces.put(newChessPosition, getPiece(newChessPosition));
                    }
                    else {
                        newWhitePieces.put(newChessPosition, getPiece(newChessPosition));
                    }
                }
            }
        }
        whitePieces = newWhitePieces;
        blackPieces = newBlackPieces;
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
        resetMaps();
    }


    public void eraseBoard() {
        for (var row = 0; row < 8; row++) {
            for (var col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }
    }

    public ChessBoard setBoard(ChessBoard newBoard) {
        eraseBoard();
        for (var row = 1; row < 9; row++) {
            for (var col = 1; col < 9; col++) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if(newBoard.getPiece(newPosition) != null) {
                    addPiece(newPosition, newBoard.getPiece(newPosition));
                }
            }
        }
        return this;
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
            for (int columns = 0; columns <= 7; columns++) {
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
