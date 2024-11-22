package websocket.messages;

public class Error extends ServerMessage {
    String message;

    public Error(ServerMessageType type, String currentMessage) {
        super(type);
        this.message = currentMessage;
    }

    public String getErrorMessage() {
        return "ERROR: " + message;
    }

}
