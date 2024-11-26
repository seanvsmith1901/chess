package websocket.commands;

public class Move {
    private final Position startPosition;
    private final Position endPosition;

    public Move(Position startPosition, Position endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public Position getStartPosition() { return startPosition; }
    public Position getEndPosition() { return endPosition; }
}

