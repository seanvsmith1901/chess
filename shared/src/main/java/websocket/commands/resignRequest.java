package websocket.commands;

public class resignRequest extends UserGameCommand {
    String gameName;
    String username;
    public resignRequest(CommandType commandType, String authToken, Integer gameID, String gameName, String username) {
        super(commandType, authToken, gameID);
        this.gameName = gameName;
        this.username = username;
    }
    public String getGameName() {
        return gameName;
    }
    public String getUsername() {
        return username;
    }

}
