package dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class Users {
    private static Map<String, MySocket> userList = new LinkedHashMap<>();

    public synchronized Map<String, MySocket> getAllUser() {
        return userList;
    }

    public synchronized MySocket getUser(String key) {
        return userList.get(key);
    }

    public synchronized MySocket getUser(MySocket socket) {
        return userList.get(socket);
    }

    public synchronized void putUser(String key, MySocket socket) {
        userList.put(key, socket);
    }

    public synchronized void removeUser(String id) {
        userList.remove(id);
    }
}
