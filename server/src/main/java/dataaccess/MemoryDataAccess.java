package dataaccess;

import com.google.gson.Gson;
import model.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.HashMap;
import java.util.Map;


public class MemoryDataAccess implements DataAccess {

    Map<String, AuthData> authenticationTokens = new HashMap<String, AuthData>();
    Map<String, GameData> gameTokens = new HashMap<String, GameData>();
    Map<String, UserData> userTokens = new HashMap<String, UserData>();

    public Object deleteEverything() {
        authenticationTokens.clear();
        gameTokens.clear();
        userTokens.clear();
        return this;
    }

    public UserData getUser(String username) {
        return userTokens.get(username);
    }

    public void createUser(UserData currentUser) {
        var userName = currentUser.name();
        userTokens.put(userName, currentUser);
    }


    // so this is where I actually have to like, store everything which is going to be annoying.
    // I need to be able to implement the three different kinds of model data structures as like a hash map or something
    // and then we can have a function that just uhh throws back nothing to show that we have nothing in there.

}
