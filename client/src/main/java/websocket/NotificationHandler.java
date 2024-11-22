package websocket;

import chess.ChessGame;
import websocket.messages.*;
import websocket.messages.Error;

public interface NotificationHandler {

    void notify(Notification notification);

    void displayError(Error errorNotification);

}
