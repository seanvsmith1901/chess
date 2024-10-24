package dataaccess;

import com.google.gson.Gson;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
            try (var stmt = conn.prepareStatement(statement)) {
                stmt.setString(1, newAuth.authToken());
                stmt.setString(2, newAuth.username());
                stmt.executeUpdate(statement);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new DataAccessException("Literally no clue how you got here. abor");

    }

    public void getAuthSize() throws DataAccessException {

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
      gameName VARCHAR(256) NOT NULL,
      whiteUserName VARCHAR(256) DEFAULT NULL,
      blackUserName VARCHAR(256) DEFAULT NULL,
      chessGame VARCHAR(256),
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

}