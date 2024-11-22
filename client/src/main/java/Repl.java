
import serverfacade.State;
import ui.Bucket;
import ui.ChessClient;

import websocket.NotificationHandler;
import websocket.messages.Error;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import ui.EscapeSequences;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl, Bucket bucket) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {


        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(Notification notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println(notification.getMessage());
        }

    }

    public void displayError(Error notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            System.out.println(notification.getErrorMessage()); // make this print red IG at some point.
        }
    }

    private void printPrompt() {
        var statement = "\n";
        if (client.getState() == State.SIGNEDIN) {
            statement += "[SIGNED IN]";
        }
        else if (client.getState() == State.INGAME) {
            statement += "[IN GAME]";
        }
        else {
            statement += "[NOT SIGNED IN]";
        }
        statement += " >>> ";
        System.out.println(statement);
    }

}