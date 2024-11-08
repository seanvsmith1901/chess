
import serverfacade.State;
import ui.ChessClient;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
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

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }
//
    private void printPrompt() {
        var statement = "\n";
        if (client.getState() == State.SIGNEDIN) {
            statement += "[SIGNED IN]";
        }
        else {
            statement += "[NOT SIGNED IN]";
        }
        statement += " >>> ";
        System.out.println(statement);
    }

}