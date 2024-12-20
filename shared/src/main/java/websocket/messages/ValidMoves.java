package websocket.messages;

import chess.ChessMove;
import model.GameData;

import java.util.Collection;

public class ValidMoves extends ServerMessage {
    Collection<ChessMove> validMoves;
    GameData game;


    public ValidMoves(ServerMessageType type, Collection<ChessMove> validMoves, GameData game) {
        super(type);
        this.validMoves = validMoves;
        this.game = game;
    }

    public Collection<ChessMove> returnValidMoves() {return validMoves; }

    public GameData returnGame() {return game; }

}