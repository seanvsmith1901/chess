package service;

import model.*;

import java.util.Collection;

import dataaccess.*;


public class AuthService {

    private DataAccess dataAccess;

    public void deleteEverything() throws DataAccessException {
        dataAccess.deleteEverything();
    }

}
