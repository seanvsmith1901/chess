package websocket.commands;

public class leaveGameRequest extends UserGameCommand {

    private final String username;

    private final String gameName;

    private final String teamColor;

    public leaveGameRequest(CommandType commandType, String authToken, Integer gameID, String username, String gameName, String teamColor) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.gameName = gameName;
        this.teamColor = teamColor;
    }

    public String getUsername() {
        return username;
    }
    public String getGameName() {
        return gameName;
    }
    public String getTeamColor() {
        return teamColor;
    }
}
