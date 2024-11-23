package websocket.commands;

public class makeMoveRequest extends UserGameCommand {

    private final String username;

    private final String teamColor;

    private final String gameName;

    private final String oldPosition;

    private final String newPosition;

    private final String promotionPeice;

    public makeMoveRequest(CommandType commandType, String authToken, Integer gameID, String username, String teamColor, String gameName, String oldPosition, String newPosition, String promotionPeice) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.teamColor = teamColor;
        this.gameName = gameName;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.promotionPeice = promotionPeice;
    }

    public String getUsername() { return username; }

    public String getTeamColor() { return teamColor; }

    public String getNewPosition() { return newPosition; }

    public String getOldPosition() { return oldPosition; }

    public String getGameName() { return gameName; }

    public String getPromotionPeice() { return promotionPeice; }




}
