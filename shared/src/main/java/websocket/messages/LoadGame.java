package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

public class LoadGame extends ServerMessage {

    GameData game;

    public LoadGame(ServerMessageType type, String message, GameData currentGame) {
        super(type, message);
        this.game = currentGame;
    }

    public GameData getGame() {
        return game;
    }


}
