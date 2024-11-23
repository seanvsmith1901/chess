package websocket.messages;

import chess.ChessMove;
import model.GameData;

import java.util.Collection;

public class validMoves extends ServerMessage {
    Collection<ChessMove> validMoves;
    GameData game;


    public validMoves(ServerMessageType type, Collection<ChessMove> validMoves, GameData game) {
        super(type);
        this.validMoves = validMoves;
        this.game = game;
    }

    public Collection<ChessMove> returnValidMoves() {return validMoves; }

    public GameData returnGame() {return game; }

}