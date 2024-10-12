package service;

import model.*;

import java.util.Collection;

import dataaccess.*;


public class AuthService {

    private DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object deleteEverything(DataAccess thisDataAccess) throws DataAccessException {
        return thisDataAccess.deleteEverything();
    }

}
