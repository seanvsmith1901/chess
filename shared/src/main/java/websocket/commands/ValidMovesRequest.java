package websocket.commands;



public class ValidMovesRequest extends UserGameCommand {
    Position startPosition;
    String gameName;


    public ValidMovesRequest(CommandType commandType, String authToken, Integer gameID, Position newPosition, String gameName) {
        super(commandType, authToken, gameID);
        startPosition = newPosition;
        this.gameName = gameName;
    }

    public Position getStartPosition() { return startPosition; }

    public String getGameName() { return gameName; }
}
