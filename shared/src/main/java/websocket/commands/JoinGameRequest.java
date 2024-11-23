package websocket.commands;

public class JoinGameRequest extends UserGameCommand {

    private final String username;

    private final String teamColor;


    public JoinGameRequest(CommandType commandType, String authToken, Integer gameID, String username, String teamColor) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.teamColor = teamColor;
    }

    public String getUsername() {return username; }

    public String getTeamColor() {return teamColor; }

}
