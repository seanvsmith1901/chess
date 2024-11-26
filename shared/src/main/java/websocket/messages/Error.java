package websocket.messages;

public class Error extends ServerMessage {
    String errorMessage;

    public Error(ServerMessageType type, String currentMessage) {
        super(type);
        this.errorMessage = currentMessage;
    }

    public String getErrorMessage() {
        return "ERROR: " + errorMessage;
    }

}
