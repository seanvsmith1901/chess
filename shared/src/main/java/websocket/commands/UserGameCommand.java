package websocket.commands;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final String username;

    private final String teamColor;

    private final String gameName;

    private final String peice;

    private final String newMove;

    private final String promotionPeice;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, String username, String teamColor, String peice, String newMove, String promotionPeice, String gameName) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.username = username;
        this.teamColor = teamColor;
        this.peice = peice;
        this.newMove = newMove;
        this.gameName = gameName;
        this.promotionPeice = promotionPeice;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getUsername() { return username; }

    public String getTeamColor() { return teamColor; }

    public String getPeice() { return peice; }

    public String getNewMove() { return newMove; }

    public String getGameName() { return gameName; }

    public String getPromotionPeice() { return promotionPeice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
