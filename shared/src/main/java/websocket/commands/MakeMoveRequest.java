package websocket.commands;

public class MakeMoveRequest extends UserGameCommand {

    private final String username;
    private final String teamColor;
    private final String gameName;
    private final Move move;  // Change to Move object
    private final String promotionPeice;

    public MakeMoveRequest(CommandType commandType, String authToken, Integer gameID, Move move, String username,
                           String teamColor, String gameName, String promotionPeice) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.teamColor = teamColor;
        this.gameName = gameName;
        this.move = move;
        this.promotionPeice = promotionPeice;
    }

    public String getUsername() { return username; }
    public String getTeamColor() { return teamColor; }
    public Move getMove() { return move; }
    public String getGameName() { return gameName; }
    public String getPromotionPeice() { return promotionPeice; }
}


