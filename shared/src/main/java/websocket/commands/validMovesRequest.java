package websocket.commands;

public class validMovesRequest extends UserGameCommand {
    String startPosition;
    String gameName;


    public validMovesRequest(CommandType commandType, String authToken, Integer gameID, String newPosition, String gameName) {
        super(commandType, authToken, gameID);
        startPosition = newPosition;
        this.gameName = gameName;
    }

    public String getStartPosition() { return startPosition; }

    public String getGameName() { return gameName; }
}
