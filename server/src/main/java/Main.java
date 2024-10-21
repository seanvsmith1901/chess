import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.Services;
import server.Server;


public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        DataAccess dataAccess = new MemoryDataAccess();

        Services services = new Services(dataAccess);

    }
}

