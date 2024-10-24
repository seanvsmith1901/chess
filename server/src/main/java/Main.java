import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import service.Services;
import server.Server;


public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);
        try {
            DataAccess dataAccess = new MySqlDataAccess();

            Services services = new Services(dataAccess);
        }
        catch (DataAccessException e) {
            e.printStackTrace(); // just a generic to make sure it gets handled.
        }

    }
}

