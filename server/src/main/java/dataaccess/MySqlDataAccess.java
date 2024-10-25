package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() {
        try {
            configureDatabase();
        }
        catch (DataAccessException e) {
            e.printStackTrace();
        }

    }

    public Object deleteEverything() throws DataAccessException {
        var statement = "TRUNCATE userData";
        var statement2 = "TRUNCATE gameData";
        var statement3 = "TRUNCATE authData";
        executeUpdate(statement);
        executeUpdate(statement2);
        executeUpdate(statement3);
        return null;
    }

    public void createUser(UserData newUser) throws DataAccessException {
        var statement = "INSERT INTO userData (username, password) VALUES (?, ?)";
        executeUpdate(statement, newUser);
    }

    public UserData getUser(String userName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT userName, json FROM userData WHERE userName = ?";
            try (var stmt = conn.prepareStatement(statement)) {
                stmt.setString(1, userName);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DataAccessException("Literally no clue how you got here. abor");
    }

    public void addAuth(AuthData newAuth) throws DataAccessException {
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        //var json = new Gson().toJson(newAuth);
        executeUpdate(statement, newAuth.authToken(), newAuth.username());
        //return new Pet(id, pet.name(), pet.type());
    }

    public int getAuthSize() throws DataAccessException {
        var result = new ArrayList<AuthData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData";
            try (var stmt = conn.prepareStatement(statement)) {
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.add(readAuth(rs));
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result.size();
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM authData WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement(statement)) {
                stmt.setString(1, authToken);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DataAccessException("Literally no clue how you got here.");
    }

    public AuthData getAuthObjectFromUsername(String username) throws DataAccessException {
        //var statement = "SELECT * FROM authData WHERE username = ?";
        var result = "";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData";
            try (var stmt = conn.prepareStatement(statement)) {
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DataAccessException("Literally no clue how you got here.");

    }

    public void deleteAuthToken(AuthData currentAuth) throws DataAccessException {
        AuthData checkAuth = getAuthObject(currentAuth.authToken()); // make sure auth exists

        var authToken = currentAuth.authToken();
        var statement = "DELETE FROM authData WHERE authToken = ?"; // actually deletes the authToken.
        executeUpdate(statement, authToken);
    }

    public HashSet<model.GameData> getGames() throws DataAccessException {
        var result = new HashSet<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUserName, blackUserName, gameName, chessGame FROM gameData";
            try (var stmt = conn.prepareStatement(statement)) {
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO gameData (whiteUserName, blackUserName, gameName, chessGame) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(new ChessGame());
        //var json = new ChessGame().toString();

        executeUpdate(statement, "null", "null", gameName, json);
    }





    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
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
            throw new DataAccessException("Error: 500, Unable to configure database: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS authData (
      authToken VARCHAR(256) NOT NULL,
      username VARCHAR(256) NOT NULL,
      PRIMARY KEY (authToken),
      INDEX idx_username (username)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,

            """
    CREATE TABLE IF NOT EXISTS gameData (
      id INT NOT NULL AUTO_INCREMENT,
      whiteUserName VARCHAR(256) DEFAULT NULL,
      blackUserName VARCHAR(256) DEFAULT NULL,
      gameName VARCHAR(256) NOT NULL,
      chessGame TEXT NOT NULL,
      PRIMARY KEY (id),
      INDEX idx_gameName (gameName),
      INDEX idx_whiteUserName (whiteUserName),
      INDEX idx_blackUserName (blackUserName)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,

            """
    CREATE TABLE IF NOT EXISTS userData (
      name VARCHAR(256) NOT NULL,
      password VARCHAR(256) NOT NULL,
      email VARCHAR(256) NOT NULL,
      PRIMARY KEY (name),
      INDEX idx_password (password),
      INDEX idx_email (email)
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

    private UserData readUser(ResultSet rs) throws SQLException {
        var userName = rs.getString("name");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(userName, password, email); // creates the new user object and returns it.
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("id");
        var whiteUserName = rs.getString("whiteUserName");
        var blackUsername = rs.getString("blackUserName");
        var gameName = rs.getString("gameName");
        var json = rs.getString("chessGame");
        var chessGame = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUserName, blackUsername, gameName, chessGame);
    }

}