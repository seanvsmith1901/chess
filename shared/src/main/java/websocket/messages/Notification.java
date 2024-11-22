package websocket.messages;


public class Notification extends ServerMessage {
    String message;

    public Notification(ServerMessageType type, String currentMessage) {
        super(type);
        this.message = currentMessage;
    }

    public String getMessage() {
        return message;
    }

}
