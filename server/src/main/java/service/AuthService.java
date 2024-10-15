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

    public Object deleteEverything(DataAccess thisDataAccess) throws DataAccessException {
        return thisDataAccess.deleteEverything();
    }

    public Object createAuthToken(String username) {
        var authentication = generateRandomString(8);
        return new AuthData(authentication, username); // returns the new authentication object
    }

    public AuthData getAuthObject(String authToken) throws DataAccessException {
        return dataAccess.getAuthObject(authToken);
    }

    public void deleteAuthObject(AuthData authToken) throws DataAccessException {
        dataAccess.deleteAuthToken(authToken);
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
