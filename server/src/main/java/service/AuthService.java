package service;

import model.*;

import java.util.Collection;

import java.security.SecureRandom;

import dataaccess.*;


public class AuthService {

    private DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object deleteEverything() throws DataAccessException {
        return dataAccess.deleteEverything();
    }

    public AuthData createAuthToken(String username) throws DataAccessException {
        if(username == null || username.isEmpty()) {
            throw new DataAccessException("Username cannot be null or empty");
        }
        var authentication = generateRandomString(8);
        var newAuthData = new AuthData(authentication, username);
        dataAccess.addAuth(newAuthData);
        return newAuthData; // returns the new authentication object
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException {
        return dataAccess.getAuthObject(authToken);
    }

    public AuthData getAuthObjectFromUserName(String username) throws DataAccessException {
        return dataAccess.getAuthObjectFromUsername(username);
    }

    public void deleteAuthObject(AuthData authToken) throws DataAccessException {
        dataAccess.deleteAuthToken(authToken);
    }

    public int getAuthSize() throws DataAccessException {
        return dataAccess.getAuthSize();
    }

    private String generateRandomString(int length) {

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
