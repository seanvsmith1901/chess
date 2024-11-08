package model;

import java.util.ArrayList;
import java.util.HashSet;

public record GamesList (HashSet<GameData> games) {

    @Override
    public String toString() {
        if (games.isEmpty()) {
            return "There are no games! Consider creating one and bringing some friends :)";
        }

        StringBuilder result = new StringBuilder();
        int i = 1;
        result.append("\n");
        for (var game: games) {
            result.append(Integer.toString(i)).append(": ").append(game.gameName()).append("\nWhite user: ")
                    .append(game.whiteUsername()).append(" Black user: ").append(game.blackUsername()).append("\n\n");
            i++;
        }
        return result.toString();
    }
    public int size() {
        return games.size();
    }

}
