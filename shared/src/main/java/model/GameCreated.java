package model;

public record GameCreated (int gameID) {

    @Override
    public String toString() {
        return Integer.toString(gameID);
    }

}
