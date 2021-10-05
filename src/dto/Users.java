package dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class Users {
    private static Users users = null;
    private Map<String, MySocket> userList = new LinkedHashMap<>();

    private Users() {}

    public static Users getInstance() {
        if (users == null) {
            users = new Users();
        }
        return users;
    }

    public synchronized Map<String, MySocket> getAllUser() {
        return userList;
    }

    public synchronized MySocket getUser(String key) {
        return userList.get(key);
    }

    public synchronized void putUser(String key, MySocket socket) {
        userList.put(key, socket);
    }

    public synchronized void removeUser(String id) {
        userList.remove(id);
    }
}
