import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import handler.FrakenHandler;
import spark.*;
import java.util.*;
import server.Server;


public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        DataAccess dataAccess = new MemoryDataAccess();

        FrakenHandler handler = new FrakenHandler(dataAccess);

    }
}

