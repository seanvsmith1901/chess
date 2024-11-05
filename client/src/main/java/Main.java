import chess.*;

public class Main {
    public static void main(String[] args) {
        var serverURL = "http://localhost:8080";
        if (args.length == 1) {
            serverURL = args[0];
        }
        System.out.println("♕ Welcome to 240 Chess. Type help to get started. ♕" );
        var ourRepl = new Repl(serverURL);
        ourRepl.run();

    }
}