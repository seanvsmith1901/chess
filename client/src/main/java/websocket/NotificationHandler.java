package websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}
