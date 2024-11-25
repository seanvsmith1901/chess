package websocket.commands;

public class JoinGameRequest extends UserGameCommand {

    private final String username;

    private final String teamColor;

    private final String gameName;

    public JoinGameRequest(CommandType commandType, String authToken, Integer gameID, String username, String teamColor, String gameName) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.teamColor = teamColor;
        this.gameName = gameName;
    }

    public String getUsername() {return username; }

    public String getTeamColor() {return teamColor; }

    public String getGameName() { return gameName; }

}
