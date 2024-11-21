package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;

public class LoadGame extends ServerMessage {

    ChessGame game;

    public LoadGame(ServerMessageType type, String message, ChessGame currentGame) {
        super(type, message);
        this.game = currentGame;
    }


}
