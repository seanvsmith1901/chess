
import serverfacade.State;
import ui.Bucket;
import ui.ChessClient;

import websocket.NotificationHandler;
import websocket.messages.ServerMessage;
import ui.EscapeSequences;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;
    private static Bucket bucket;

    public Repl(String serverUrl, Bucket bucket) {
        client = new ChessClient(serverUrl, this);
        this.bucket = bucket;
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

    public void notify(ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.println(notification.message);
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