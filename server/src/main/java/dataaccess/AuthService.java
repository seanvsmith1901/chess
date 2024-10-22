package dataaccess;

import model.AuthData;

import java.security.SecureRandom;


public class AuthService {

    private static DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) { // correct data access setting
        this.dataAccess = dataAccess;
    }

    public Object deleteEverything() throws DataAccessException { // does exactly what you think it does
        return dataAccess.deleteEverything();
    }

    public AuthData createAuthToken(String username) throws DataAccessException { // creates a new authtoken for a user
        if(username == null || username.isEmpty()) {
            throw new DataAccessException("Username cannot be null or empty");
        }
        var authentication = generateRandomString(8);
        var newAuthData = new AuthData(authentication, username);
        dataAccess.addAuth(newAuthData);
        return newAuthData; // returns the new authentication object
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException { // gets the authobject
        return dataAccess.getAuthObject(authToken);
    }

    public AuthData getAuthObjectFromUserName(String username) throws DataAccessException { // return it other way
        return dataAccess.getAuthObjectFromUsername(username);
    }

    public void deleteAuthObject(AuthData authToken) throws DataAccessException { // just blows it up
        dataAccess.deleteAuthToken(authToken);
    }

    public int getAuthSize() throws DataAccessException { // used for testing purposes
        return dataAccess.getAuthSize();
    }

    private String generateRandomString(int length) { // used to help generate the random auth tokens

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);

        }
        return sb.toString();
    }

}
