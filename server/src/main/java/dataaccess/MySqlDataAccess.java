package dataaccess;

import com.google.gson.Gson;

import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame; // need for serializing chess game model methinks.

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public Object deleteEverything() throws DataAccessException {

    }



    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: 500, Unable to configure database: " + ex.getMessage());
        }
    }

    private final String[] createStatements = {  // this should create my authData, gameData and userData tables. please.
            """
            CREATE TABLE IF NOT EXISTS  authData (
              'authToken' string NOT NULL,
              'username' varchar(256) NOT NULL,
              PRIMARY KRY ('authToken'),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            
            CREATE TABLE IF NOT EXISTS gameData (
              'id' int NOT NULL AUTO_INCREMENT,
              'gameName' varchar(256) NOT NULL,
              'whiteUserName' varChar(256) DEFAULT NULL,
              'blackUserName' varChar(256) DEFAULT NULL,
              'chessGame' varChar(256),
              PRIMARY KRY ('id'),
              INDEX(gameName)
              INDEX(username)
              INDEX(whiteUserName)
              INDEX(blackUserName)
              ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            
            CREATE TABLE IF NOT EXISTS userData (
              'name' varchar(256) NOT NULL,
              'password' varchar(256) NOT NULL,
              'email' varchar(256) NOT NULL,
              PRIMARY KRY ('name'),
              INDEX(password),
              INDEX(email),
              ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: 500, Unable to configure database: " + ex.getMessage());
        }
    }
}