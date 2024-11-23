package websocket.commands;

public class validMovesRequest extends UserGameCommand {
    String startPosition;


    public validMovesRequest(CommandType commandType, String authToken, Integer gameID, String newPosition) {
        super(commandType, authToken, gameID);
        startPosition = newPosition;
    }

    public String getStartPosition() { return startPosition; }
}
