import chess.*;
import spark.*;
import java.util.*;
import server.Server;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        Server server = new Server();
        server.run(8080);


    }
//    private static void createServer() {
//        Spark.port(8080);
//        Spark.staticFiles.location("/public");
//        createRoutes();
//        Spark.awaitInitialization();
//        System.out.println("listening on port " + 8000);
//    }
//
//
//    private static void createRoutes() {
//        Spark.get("/hello", ((request, response) -> "I really need to go to the bathroom"));
//    }
}

