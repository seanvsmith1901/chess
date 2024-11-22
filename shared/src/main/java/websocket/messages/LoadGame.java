package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage {

    GameData game;

    public LoadGame(ServerMessageType type, String message, GameData currentGame) {
        super(type);
        this.game = currentGame;
    }

    public GameData getGame() {
        return game;
    }


}
