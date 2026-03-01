package dataaccess;

import kotlin.NotImplementedError;

import java.util.Collection;

public class DataAccess {
    public static UserData addUser(String username,String password, String email) {
        throw new NotImplementedError();
    }

    public static UserData getUser(String username) {
        throw new NotImplementedError();
    }

    public static AuthData addAuth() {
        throw new NotImplementedError();
    }

    public static AuthData getAuth() {
        throw new NotImplementedError();
    }

    public static GameData addGame(String gamename){
        throw new NotImplementedError();
    }

    public static GameData getGame() {
        throw new NotImplementedError();
    }

    public static Collection<GameData> listGames(){
        throw new NotImplementedError();
    }
}
